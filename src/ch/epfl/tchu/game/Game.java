package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;

/**
 * <h1>Game</h1>
 * None instantiable class that implements the core step sequence of the game.
 *
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public final class Game {
    /**
     * Constant that expresses the numbers of cards that will be draw during a drawing cards turn.
     */
    private static final int DRAWING_PER_DRAW_CARDS_TURN = 2;

    private Game() {
    }

    /**
     * The game is played in three steps :
     * <p>
     * Firstly, before the while loop, is the initialization : the GameState is created, players are initialized,
     * tickets are chosen by the players and information are display.
     * <p>
     * Then, in the while loop, all the game takes place : the current player choose the kind of turn he wants
     * (draw tickets, draw cards, claim route). if he draw tickets, he will have to choose the one he keep. If he draw
     * cards, he can draw a card from the faceUpCards or the blind top deck card two times. If he try to claim a Route,
     * it will depends on the route's level. For underground Route, he will have to play additional cards or abandon.
     * At every step, information will be display.
     * <p>
     * Finally, after the while loop, final points for each player will be computed and the winner (or winners)
     * will be computed and information will be displayed.
     *
     * @param players     a map that matches PlayerId with Player so each PLayerId can be seen as a Player
     * @param playerNames a map that matches PlayerIds with their names, contains the name of each Player
     * @param tickets     a SortedBag of Ticket that is, all the initial tickets of the game
     * @param rng         a Random use to compute the initial GameState
     * @throws IllegalArgumentException if players or playerNames doesn't contains the number of PlayerIds
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {

        Preconditions.checkArgument(players.size() == PlayerId.NUMBER_OF_PLAYER && playerNames.size() == PlayerId.NUMBER_OF_PLAYER);

        List<PlayerId> playerIds = new ArrayList<>(players.keySet());

        //1.communicate names
        players.forEach((id, player) -> player.initPlayers(id, playerNames));

        //2.create GameState
        GameState gameState = GameState.initial(tickets, rng);

        Info currentPlayerInfo = new Info(playerNames.get(gameState.currentPlayerId()));

        //3. willPlayFirstInfo
        receiveInfoForAllPlayer(players, currentPlayerInfo.willPlayFirst());

        //4.setInitialTicketChoice
        for (PlayerId p : playerIds) {
            players.get(p).setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        }

        //5.update state (1)
        updateGameState(players, gameState);

        //6.chooseInitialTickets
        for (PlayerId p : playerIds) {
            SortedBag<Ticket> chosenTickets = players.get(p).chooseInitialTickets();
            gameState = gameState.withInitiallyChosenTickets(p, chosenTickets);
        }

        //7.receive info
        for (PlayerId p : playerIds) {
            receiveInfoForAllPlayer(players, new Info(playerNames.get(p)).keptTickets(gameState.playerState(p).ticketCount()));
        }

        //set a map for Info
        EnumMap<PlayerId, Info> playersInfo = new EnumMap<>(PlayerId.class);
        for (PlayerId playerId : playerIds) {
            playersInfo.put(playerId, new Info(playerNames.get(playerId)));
        }

        boolean isPlaying = true;
        while (isPlaying) {

            //0.
            PlayerId currentPlayer = gameState.currentPlayerId();

            //1.update info for current player
            currentPlayerInfo = playersInfo.get(currentPlayer);

            //2. info
            receiveInfoForAllPlayer(players, currentPlayerInfo.canPlay());

            //3.update state (2)
            updateGameState(players, gameState);

            //4.next turn
            Player.TurnKind turn = players.get(currentPlayer).nextTurn();

            switch (turn) {

                case DRAW_TICKETS:

                    SortedBag<Ticket> drawnTickets = gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                    receiveInfoForAllPlayer(players, currentPlayerInfo.drewTickets(drawnTickets.size()));

                    SortedBag<Ticket> chosenTickets = players.get(currentPlayer).chooseTickets(drawnTickets);
                    gameState = gameState.withChosenAdditionalTickets(drawnTickets, chosenTickets);

                    receiveInfoForAllPlayer(players, currentPlayerInfo.keptTickets(chosenTickets.size()));
                    break;

                case DRAW_CARDS:

                    for (int i = 0; i < DRAWING_PER_DRAW_CARDS_TURN; ++i) {
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);

                        updateGameState(players, gameState);// (3)

                        int slot = players.get(currentPlayer).drawSlot();

                        if (slot == Constants.DECK_SLOT) {
                            receiveInfoForAllPlayer(players, currentPlayerInfo.drewBlindCard());
                            gameState = gameState.withBlindlyDrawnCard();
                        } else {
                            receiveInfoForAllPlayer(players, currentPlayerInfo.drewVisibleCard(gameState.cardState().faceUpCard(slot)));
                            gameState = gameState.withDrawnFaceUpCard(slot);
                        }

                        updateGameState(players, gameState);// (3)
                    }

                    break;

                case CLAIM_ROUTE:

                    Route claimRoute = players.get(currentPlayer).claimedRoute();
                    SortedBag<Card> initialClaimCards = players.get(currentPlayer).initialClaimCards();

                    if (claimRoute.level() == Route.Level.OVERGROUND) {

                        receiveInfoForAllPlayer(players, currentPlayerInfo.claimedRoute(claimRoute, initialClaimCards));
                        gameState = gameState.withClaimedRoute(claimRoute, initialClaimCards);

                    } else {

                        receiveInfoForAllPlayer(players, currentPlayerInfo.attemptsTunnelClaim(claimRoute, initialClaimCards));

                        SortedBag.Builder<Card> drawnCardsBuilder = new SortedBag.Builder<>();
                        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            drawnCardsBuilder.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                        }
                        SortedBag<Card> drawnCards = drawnCardsBuilder.build();

                        gameState = gameState.withMoreDiscardedCards(drawnCards);

                        int additionalCardsCost = claimRoute.additionalClaimCardsCount(initialClaimCards, drawnCards);

                        receiveInfoForAllPlayer(players, currentPlayerInfo.drewAdditionalCards(drawnCards, additionalCardsCost));

                        //handle the decision of the player
                        if (additionalCardsCost > 0) {
                            SortedBag<Card> playedCard = SortedBag.of();

                            //Computing possibilities for the player
                            List<SortedBag<Card>> option = gameState.currentPlayerState()
                                    .possibleAdditionalCards(additionalCardsCost, initialClaimCards);

                            if (!option.isEmpty())
                                playedCard = players.get(currentPlayer).chooseAdditionalCards(option);

                            if (playedCard.isEmpty()) {
                                receiveInfoForAllPlayer(players, currentPlayerInfo.didNotClaimRoute(claimRoute));
                            } else {
                                receiveInfoForAllPlayer(players, currentPlayerInfo.claimedRoute(claimRoute, initialClaimCards.union(playedCard)));
                                gameState = gameState.withClaimedRoute(claimRoute, initialClaimCards.union(playedCard));
                            }

                        } else {
                            receiveInfoForAllPlayer(players, currentPlayerInfo.claimedRoute(claimRoute, initialClaimCards));
                            gameState = gameState.withClaimedRoute(claimRoute, initialClaimCards);
                        }
                    }

                    updateGameState(players, gameState);

                    break;

                default:
                    throw new Error();
            }

            //check condition for the loop to stop
            isPlaying = gameState.currentPlayerId() != gameState.lastPlayer();

            //check last Turn
            if (gameState.lastTurnBegins()) {
                receiveInfoForAllPlayer(players, currentPlayerInfo.lastTurnBegins(gameState.currentPlayerState().carCount()));
            }

            //change current player
            gameState = gameState.forNextTurn();
        }

        //update (4)
        updateGameState(players, gameState);

        // Compute longestTrail and winner(s)
        final Map<PlayerId, Trail> longestTrail = new EnumMap<>(PlayerId.class);
        final Map<PlayerId, Integer> playerPoints = new EnumMap<>(PlayerId.class);

        for (PlayerId p : playerIds) {
            longestTrail.put(p, Trail.longest(gameState.playerState(p).routes()));
        }

        //find longest
        final int lengthMax = longestTrail.values()
                .stream()
                .mapToInt(Trail::length)
                .max()
                .orElse(0);

        //Info for longestTrailBonus
        for (PlayerId p : playerIds) {
            int length = longestTrail.get(p).length();
            Info playerInfo = new Info(playerNames.get(p));

            if (length == lengthMax) {
                receiveInfoForAllPlayer(players, playerInfo.getsLongestTrailBonus(longestTrail.get(p)));
                playerPoints.put(p, gameState.playerState(p).finalPoints() + Constants.LONGEST_TRAIL_BONUS_POINTS);
            } else playerPoints.put(p, gameState.playerState(p).finalPoints());
        }

        receiveInfoForAllPlayer(players, Info.ranking(playerPoints, playerNames));
    }

    /**
     * Compute the update of a new gameState for each players.
     *
     * @param players      a map that matches PlayerId with Player so each PLayerId can be seen as a Player
     * @param newGameState the new GameState used for the update
     */
    private static void updateGameState(Map<PlayerId, Player> players, GameState newGameState) {
        players.forEach((pi, p) -> p.updateState(newGameState, newGameState.playerState(pi)));

    }

    /**
     * Display information for both player.
     *
     * @param players a map that matches PlayerId with Player so each PLayerId can be seen as a Player
     * @param info    information to display
     */
    private static void receiveInfoForAllPlayer(Map<PlayerId, Player> players, String info) {
        players.values().forEach(p -> p.receiveInfo(info));
    }
}
