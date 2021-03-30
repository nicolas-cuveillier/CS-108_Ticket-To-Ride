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
        //1.create GameState
        GameState gameState = GameState.initial(tickets, rng);

        //2.communicate names
        players.get(gameState.currentPlayerId()).initPlayers(gameState.currentPlayerId(), playerNames);
        players.get(gameState.currentPlayerId().next()).initPlayers(gameState.currentPlayerId().next(), playerNames);

        Info currentPlayer = new Info(playerNames.get(gameState.currentPlayerId()));
        Info nextPlayer = new Info(playerNames.get(gameState.currentPlayerId().next()));

        //3. willPlayFirstInfo
        receiveInfoForBothPlayer(players, currentPlayer.willPlayFirst());

        //4.setInitialTicketChoice
        players.get(gameState.currentPlayerId()).setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
        gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        players.get(gameState.currentPlayerId().next()).setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
        gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);

        //5.update state (1)
        updateGameState(players, gameState);

        //6.chooseInitialTickets
        for (PlayerId p : players.keySet()) {
            SortedBag<Ticket> initialKeptTickets = players.get(p).chooseInitialTickets();
            gameState.playerState(p).withAddedTickets(initialKeptTickets);
        }

        //7.receive info
        receiveInfoForBothPlayer(players, currentPlayer.keptTickets(gameState.currentPlayerState().ticketCount()));
        receiveInfoForBothPlayer(players, nextPlayer.keptTickets(gameState.playerState(gameState.currentPlayerId().next()).ticketCount()));

        boolean isPlaying = true;//TODO : find condition to stop it

        while (isPlaying){


            //1.update state (2)
            updateGameState(players, gameState);

            //2. info
            receiveInfoForBothPlayer(players, currentPlayer.canPlay());

            //3. check last Turn
            if (gameState.lastTurnBegins()) {
                receiveInfoForBothPlayer(players, currentPlayer.lastTurnBegins(gameState.currentPlayerState().carCount()));
            }

            //4.next turn
            Player.TurnKind turn = players.get(gameState.currentPlayerId()).nextTurn();

            switch (turn) {

                case DRAW_TICKETS:

                    receiveInfoForBothPlayer(players, currentPlayer.drewTickets(Constants.IN_GAME_TICKETS_COUNT));

                    SortedBag<Ticket> chosenTickets = players.get(gameState.currentPlayerId()).chooseTickets(gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT));
                    gameState = gameState.withoutTopTickets(Constants.IN_GAME_TICKETS_COUNT);

                    receiveInfoForBothPlayer(players, currentPlayer.keptTickets(chosenTickets.size()));

                    break;

                case DRAW_CARDS:

                    gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                    int firstSlot = players.get(gameState.currentPlayerId()).drawSlot();
                    gameState = (firstSlot == Constants.DECK_SLOT) ? gameState.withBlindlyDrawnCard() : gameState.withDrawnFaceUpCard(firstSlot);

                    receiveInfoForBothPlayerWithCondition(players, currentPlayer.drewBlindCard(),
                            currentPlayer.drewVisibleCard(gameState.cardState().faceUpCard(firstSlot)), firstSlot == Constants.DECK_SLOT);

                    updateGameState(players, gameState);//3

                    gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                    int secondSlot = players.get(gameState.currentPlayerId()).drawSlot();
                    gameState = (secondSlot == Constants.DECK_SLOT) ? gameState.withBlindlyDrawnCard() : gameState.withDrawnFaceUpCard(secondSlot);

                    receiveInfoForBothPlayerWithCondition(players, currentPlayer.drewBlindCard(),
                            currentPlayer.drewVisibleCard(gameState.cardState().faceUpCard(firstSlot)), firstSlot == Constants.DECK_SLOT);

                    break;

                case CLAIM_ROUTE:

                    Route claimRoute = players.get(gameState.currentPlayerId()).claimedRoute();
                    SortedBag<Card> initialClaimCards = players.get(gameState.currentPlayerId()).initialClaimCards();

                    if (claimRoute.level() == Route.Level.OVERGROUND) {

                        receiveInfoForBothPlayer(players, currentPlayer.claimedRoute(claimRoute, initialClaimCards));
                        gameState = gameState.withClaimedRoute(claimRoute, initialClaimCards);
                        //TODO : checker si il a les bonnes cartes ?

                    } else {

                        receiveInfoForBothPlayer(players, currentPlayer.attemptsTunnelClaim(claimRoute, initialClaimCards));

                        SortedBag.Builder<Card> drawnCards = new SortedBag.Builder<>();
                        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            drawnCards.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                        }

                        int additionalNumberOfCards = claimRoute.additionalClaimCardsCount(initialClaimCards, drawnCards.build());
                        List<SortedBag<Card>> possibleAdditionalClaimCards = gameState.currentPlayerState().possibleAdditionalCards(additionalNumberOfCards, initialClaimCards, drawnCards.build());

                        receiveInfoForBothPlayer(players, currentPlayer.drewAdditionalCards(drawnCards.build(), additionalNumberOfCards));

                        //Computing possibilities for the player
                        List<SortedBag<Card>> option = new ArrayList<>();
                        boolean containsAdditional = false;
                        for (SortedBag<Card> c : possibleAdditionalClaimCards) {
                            if (gameState.currentPlayerState().cards().contains(c)) {
                                containsAdditional = true;
                                option.add(c);
                            }
                        }

                        //handle the decision of the player
                        if (additionalNumberOfCards >= 1 && containsAdditional) {
                            SortedBag<Card> playedCard = players.get(gameState.currentPlayerId()).chooseAdditionalCards(option);

                            if (playedCard.isEmpty()) {
                                receiveInfoForBothPlayer(players, currentPlayer.didNotClaimRoute(claimRoute));
                            } else {
                                receiveInfoForBothPlayer(players, currentPlayer.claimedRoute(claimRoute, initialClaimCards.union(playedCard)));
                                gameState = gameState.withClaimedRoute(claimRoute, initialClaimCards.union(playedCard));
                            }
                            //TODO : enlever les cards au currentplayer?
                        }
                    }

                    break;
            }

            isPlaying = gameState.currentPlayerId() != gameState.lastPlayer();
            
            //change current player
            gameState = gameState.forNextTurn();
        }

        //update 4
        updateGameState(players, gameState);

        // Compute longest
        Map<Integer, PlayerId> longestTrail = new HashMap<>();

        for (PlayerId p : players.keySet()) {
            longestTrail.put(Trail.longest(gameState.playerState(p).routes()).length(), p);
        }

        //find longest
        int lengthMax = 0;
        for (Integer length : longestTrail.keySet()) {
            if (lengthMax < length) {
                longestTrail.remove(lengthMax);
                lengthMax = length;
            } else if (lengthMax > length) {
                longestTrail.remove(length);
            }
        }

        //Info for longestTrailBonus
        for (PlayerId p : longestTrail.values()) {
            if (!longestTrail.isEmpty()) {
                Info player = new Info(playerNames.get(p));
                receiveInfoForBothPlayer(players, player.getsLongestTrailBonus(Trail.longest(gameState.playerState(p).routes())));
            }
        }

        //Compute all points
        Map<Integer, PlayerId> playerPoints = new HashMap<>();
        for (PlayerId p : players.keySet()) {
            if (longestTrail.containsValue(p)) {
                playerPoints.put(gameState.playerState(p).finalPoints() + Constants.LONGEST_TRAIL_BONUS_POINTS, p);
            } else {
                playerPoints.put(gameState.playerState(p).finalPoints(), p);
            }
        }

        //find winner
        int maxPoints = 0;
        for (Integer points : playerPoints.keySet()) {
            if (maxPoints < points) {
                playerPoints.remove(maxPoints);
                maxPoints = points;
            } else if (maxPoints > points) {
                playerPoints.remove(points);
            }
        }

        //Info winner
        for (PlayerId p : playerPoints.values()) {

            if (playerPoints.size() == 1) {
                Info player = new Info(p.name());
                //receiveInfoForBothPlayer(players,player.won(playerPoints.get(p),)); loser points
            } else {
                List<String> playerNamesList = new ArrayList<>();
                playerNames.forEach((playerId, s) -> playerNamesList.add(s));
                receiveInfoForBothPlayer(players, Info.draw(playerNamesList, maxPoints));
            }
        }


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
