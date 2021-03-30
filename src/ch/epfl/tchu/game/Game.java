package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 * <p>
 * A none instantiable class that explicitly describe the procedure of the game
 */
public final class Game {
    /**
     * constant that express the numbers of cards that will be draw during a drawing cards turn
     */
    private static final int CARDS_PER_DRAW_CARDS_TURN = 2;

    private Game() {
    }

    /**
     * The game is played in three step :
     * <p>
     * Firstly, before the while loop, is the initialisation : the GameState is created, players are initialised,
     * tickets are chosen by the players and information are display.
     * <p>
     * Then, in the while loop, all the game takes place : the current player choose the kind of turn he wants
     * (draw tickets, draw cards, claim route). if he draw tickets, he will have to choose the one he keep. If he draw
     * cards, he can draw a card from the faceUpCards or the blind top deck card two times. If he try to claim a Route,
     * it will depends on the route's level. For underground Route, he will have to play additional cards or abandon.
     * At every step, information will be display.
     * <p>
     * Finally, after the while loop, final points for each player will be computed and the winner (or winners)
     * will be compute and information will be display.
     *
     * @param players     a map that matches PlayerId with Player so each PLayerId can be seen as a Player
     * @param playerNames a map that matches PlayerIds with their names, contains the name of each Player
     * @param tickets     a SortedBag of Ticket that is, all the initial tickets of the game
     * @param rng         a Random use to compute the initial GameState
     * @throws IllegalArgumentException if players or playerNames doesn't contains the number of PlayerIds
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size() == PlayerId.COUNT && playerNames.size() == PlayerId.COUNT);
        //TODO : use foreach, stream etc when possible

        //1.communicate names
        players.forEach((id, player) -> player.initPlayers(id, playerNames));

        //2.create GameState
        GameState gameState = GameState.initial(tickets, rng);

        Info currentPlayer = new Info(playerNames.get(gameState.currentPlayerId()));

        //3. willPlayFirstInfo
        receiveInfoForBothPlayer(players, currentPlayer.willPlayFirst());

        //4.setInitialTicketChoice
        for (PlayerId p : players.keySet()) {
            players.get(p).setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        }

        //5.update state (1)
        updateGameState(players, gameState);

        //6.chooseInitialTickets
        for (PlayerId p : players.keySet()) {
            gameState = gameState.withInitiallyChosenTickets(p, players.get(p).chooseInitialTickets());
        }

        //7.receive info
        for (PlayerId p : players.keySet()) {
            receiveInfoForBothPlayer(players, new Info(playerNames.get(p)).keptTickets(gameState.playerState(p).ticketCount()));
        }

        boolean isPlaying = true;

        while (isPlaying) { //TODO info last turn begins

            //update info for current player
            currentPlayer = new Info(playerNames.get(gameState.currentPlayerId()));

            //2. info
            receiveInfoForBothPlayer(players, currentPlayer.canPlay());

            //3. check last Turn
            if (gameState.lastTurnBegins()) {
                receiveInfoForBothPlayer(players, currentPlayer.lastTurnBegins(gameState.currentPlayerState().carCount()));
            }

            //1.update state (2)
            updateGameState(players, gameState);

            //4.next turn
            Player.TurnKind turn = players.get(gameState.currentPlayerId()).nextTurn();

            switch (turn) {

                case DRAW_TICKETS:

                    SortedBag<Ticket> drawnTickets = gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                    receiveInfoForBothPlayer(players, currentPlayer.drewTickets(drawnTickets.size()));

                    SortedBag<Ticket> chosenTickets = players.get(gameState.currentPlayerId()).chooseTickets(drawnTickets);
                    gameState = gameState.withoutTopTickets(Constants.IN_GAME_TICKETS_COUNT);
                    receiveInfoForBothPlayer(players, currentPlayer.keptTickets(chosenTickets.size()));

                    break;

                case DRAW_CARDS:

                    for (int i = 0; i < CARDS_PER_DRAW_CARDS_TURN; ++i) {
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);

                        int slot = players.get(gameState.currentPlayerId()).drawSlot();
                        gameState = (slot == Constants.DECK_SLOT) ? gameState.withBlindlyDrawnCard() : gameState.withDrawnFaceUpCard(slot);

                        receiveInfoForBothPlayerWithCondition(players, currentPlayer.drewBlindCard(),
                                currentPlayer.drewVisibleCard(gameState.cardState().faceUpCard(slot)), slot == Constants.DECK_SLOT);

                        updateGameState(players, gameState);// (3) le faire qu'une fois
                    }

                    break;

                case CLAIM_ROUTE:

                    Route claimRoute = players.get(gameState.currentPlayerId()).claimedRoute();
                    SortedBag<Card> initialClaimCards = players.get(gameState.currentPlayerId()).initialClaimCards();

                    if (claimRoute.level() == Route.Level.OVERGROUND) {

                        receiveInfoForBothPlayer(players, currentPlayer.claimedRoute(claimRoute, initialClaimCards));
                        gameState = gameState.withClaimedRoute(claimRoute, initialClaimCards);

                    } else {
                        //TODO : défausser les cartes ?
                        receiveInfoForBothPlayer(players, currentPlayer.attemptsTunnelClaim(claimRoute, initialClaimCards));

                        SortedBag.Builder<Card> drawnCardsBuilder = new SortedBag.Builder<>();
                        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            drawnCardsBuilder.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                        }
                        SortedBag<Card> drawnCards = drawnCardsBuilder.build();

                        //optional ?
                        gameState = gameState.withMoreDiscardedCards(drawnCards);

                        int additionalNumberOfCards = claimRoute.additionalClaimCardsCount(initialClaimCards, drawnCards);

                        receiveInfoForBothPlayer(players, currentPlayer.drewAdditionalCards(drawnCards, additionalNumberOfCards));

                        SortedBag<Card> playedCard = SortedBag.of();

                        //handle the decision of the player
                        if (additionalNumberOfCards > 0) {

                            //Computing possibilities for the player
                            List<SortedBag<Card>> option = gameState.currentPlayerState().possibleAdditionalCards(additionalNumberOfCards, initialClaimCards, drawnCards);

                            if(!option.isEmpty()) {
                                playedCard = players.get(gameState.currentPlayerId()).chooseAdditionalCards(option);
                            }

                            if (playedCard.isEmpty()) {
                                receiveInfoForBothPlayer(players, currentPlayer.didNotClaimRoute(claimRoute));
                            } else {
                                receiveInfoForBothPlayer(players, currentPlayer.claimedRoute(claimRoute, initialClaimCards.union(playedCard)));
                                gameState = gameState.withClaimedRoute(claimRoute, initialClaimCards.union(playedCard));
                                //enlève deja les cartes au player
                            }

                        } else {
                            receiveInfoForBothPlayer(players, currentPlayer.claimedRoute(claimRoute,initialClaimCards));
                            gameState = gameState.withClaimedRoute(claimRoute, initialClaimCards);
                        }
                    }

                    break;

                default:
                    break;
            }

            isPlaying = gameState.currentPlayerId() != gameState.lastPlayer();

            //change current player
            gameState = gameState.forNextTurn();
        }



        // Compute longest
        Map<PlayerId, Trail> longestTrail = new EnumMap<>(PlayerId.class);
        Map<PlayerId,Integer> playerPoints = new EnumMap<>(PlayerId.class);

        for (PlayerId p : players.keySet()) {
            longestTrail.put(p, Trail.longest(gameState.playerState(p).routes()));
        }

        //find longest
        int lengthMax = longestTrail.values().stream().mapToInt(Trail::length).max().orElse(0);

        //Info for longestTrailBonus
        for (PlayerId p:longestTrail.keySet()) {
            int length = longestTrail.get(p).length();
            Info playerInfo = new Info(p.name());

            if( length == lengthMax){
                receiveInfoForBothPlayer(players,playerInfo.getsLongestTrailBonus(longestTrail.get(p)));
                playerPoints.put(p,gameState.playerState(p).finalPoints() + Constants.LONGEST_TRAIL_BONUS_POINTS);
            } else {
                playerPoints.put(p, gameState.playerState(p).finalPoints());
            }
        }

        //TODO : stream
        boolean winnerIsAlone = playerPoints.values().stream().distinct().count() == 1 ;

        if(winnerIsAlone){

        }
        //Compute all points in loop for nfo for longestTrailBonus
        for (PlayerId p : players.keySet()) {

        }

        //update 4
        updateGameState(players, gameState);

        //find winner

        //Info winner



        //TODO : end of the game


    }

    /**
     * compute the update of a new gameState for each players
     *
     * @param players      a map that matches PlayerId with Player so each PLayerId can be seen as a Player
     * @param newGameState the new GameState used for the update
     */
    private static void updateGameState(Map<PlayerId, Player> players, GameState newGameState) {
        for (PlayerId p : players.keySet()) {
            players.get(p).updateState(newGameState, newGameState.playerState(p));
        }

    }

    /**
     * display information for both player
     *
     * @param players a map that matches PlayerId with Player so each PLayerId can be seen as a Player
     * @param info    information to display
     */
    private static void receiveInfoForBothPlayer(Map<PlayerId, Player> players, String info) {
        for (Player p : players.values()) {
            p.receiveInfo(info);
        }
    }

    /**
     * display some information for both player according to a condition
     *
     * @param players   a map that matches PlayerId with Player so each PLayerId can be seen as a Player
     * @param info1     information to display
     * @param info2     information to display
     * @param condition boolean condition that will make the method display info1 if condition is true and info2 if not
     */
    private static void receiveInfoForBothPlayerWithCondition(Map<PlayerId, Player> players, String info1, String info2, boolean condition) {
        for (Player p : players.values()) {
            if (condition) {
                p.receiveInfo(info1);
            } else {
                p.receiveInfo(info2);
            }
        }
    }
}
