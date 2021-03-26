package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public final class Game {
    private Game() {
    }

    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size() == PlayerId.COUNT && playerNames.size() == PlayerId.COUNT);
        //TODO : use foreach, stream etc
        //1.create GameState
        GameState gameState = GameState.initial(tickets, rng);

        //2.communicate names
        players.get(gameState.currentPlayerId()).initPlayers(gameState.currentPlayerId(), playerNames);
        players.get(gameState.currentPlayerId().next()).initPlayers(gameState.currentPlayerId().next(), playerNames);

        Info currentPlayer = new Info(playerNames.get(gameState.currentPlayerId()));
        Info nextPlayer = new Info(playerNames.get(gameState.currentPlayerId().next()));

        //3. willPlayFirstInfo
        for (Player p : players.values()) {
            p.receiveInfo(currentPlayer.willPlayFirst());
        }

        //4.setInitialTicketChoice
        players.get(gameState.currentPlayerId()).setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
        gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        players.get(gameState.currentPlayerId().next()).setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
        gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);

        //5.update state
        players.get(gameState.currentPlayerId()).updateState(gameState, gameState.playerState(gameState.currentPlayerId()));
        players.get(gameState.currentPlayerId().next()).updateState(gameState, gameState.playerState(gameState.currentPlayerId().next()));

        //6.chooseInitialTickets
        for (Player p : players.values()) {
            p.chooseInitialTickets();
        }

        //7.receive info
        for (Player p : players.values()) {
            p.receiveInfo(currentPlayer.keptTickets(gameState.playerState(gameState.currentPlayerId()).ticketCount()));
        }
        for (Player p : players.values()) {
            p.receiveInfo(nextPlayer.keptTickets(gameState.playerState(gameState.currentPlayerId().next()).ticketCount()));
        }

        boolean isPlaying = true;

        while (isPlaying) {

            //1.update state
            players.get(gameState.currentPlayerId()).updateState(gameState, gameState.playerState(gameState.currentPlayerId()));
            players.get(gameState.currentPlayerId().next()).updateState(gameState, gameState.playerState(gameState.currentPlayerId().next()));

            //2. info
            for (Player p : players.values()) {
                p.receiveInfo(currentPlayer.canPlay());
            }

            //3.next turn
            Player.TurnKind turn = players.get(gameState.currentPlayerId()).nextTurn();
            //TODO : insert method ifDeckRecreatedIfNeeded
            switch (turn) {

                case DRAW_TICKETS:
                    SortedBag<Ticket> chosenTickets = players.get(gameState.currentPlayerId()).chooseTickets(gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT));
                    gameState = gameState.withoutTopTickets(Constants.IN_GAME_TICKETS_COUNT);

                    for (Player p : players.values()) {
                        p.receiveInfo(currentPlayer.drewTickets(Constants.IN_GAME_TICKETS_COUNT));
                        p.receiveInfo(currentPlayer.keptTickets(chosenTickets.size()));
                    }

                    break;

                case DRAW_CARDS:
                    gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                    int slot1 = players.get(gameState.currentPlayerId()).drawSlot();
                    gameState = (slot1 == Constants.DECK_SLOT) ? gameState.withBlindlyDrawnCard() : gameState.withDrawnFaceUpCard(slot1);

                    for (Player p : players.values()) {
                        if ((slot1 == Constants.DECK_SLOT)) {
                            p.receiveInfo(currentPlayer.drewBlindCard());
                        } else {
                            p.receiveInfo(currentPlayer.drewVisibleCard(gameState.cardState().faceUpCard(slot1)));
                        }
                    }

                    players.get(gameState.currentPlayerId()).updateState(gameState, gameState.playerState(gameState.currentPlayerId()));
                    players.get(gameState.currentPlayerId().next()).updateState(gameState, gameState.playerState(gameState.currentPlayerId().next()));

                    gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                    int slot2 = players.get(gameState.currentPlayerId()).drawSlot();
                    gameState = (slot2 == Constants.DECK_SLOT) ? gameState.withBlindlyDrawnCard() : gameState.withDrawnFaceUpCard(slot2);

                    for (Player p : players.values()) {
                        if ((slot2 == Constants.DECK_SLOT)) {
                            p.receiveInfo(currentPlayer.drewBlindCard());
                        } else {
                            p.receiveInfo(currentPlayer.drewVisibleCard(gameState.cardState().faceUpCard(slot1)));
                        }
                    }

                    break;

                case CLAIM_ROUTE:

                    Route claimRoute = players.get(gameState.currentPlayerId()).claimedRoute();
                    SortedBag<Card> initialClaimCards = players.get(gameState.currentPlayerId()).initialClaimCards();

                    if (claimRoute.level() == Route.Level.UNDERGROUND) {
                        for (Player p : players.values()) {
                            p.receiveInfo(currentPlayer.claimedRoute(claimRoute, initialClaimCards));
                        }
                    } else {
                        for (Player p : players.values()) {
                            p.receiveInfo(currentPlayer.attemptsTunnelClaim(claimRoute, initialClaimCards));
                        }

                        SortedBag.Builder<Card> drawnCards = new SortedBag.Builder<>();
                        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            drawnCards.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                        }

                        int additionalCards = claimRoute.additionalClaimCardsCount(initialClaimCards, drawnCards.build());
                        List<SortedBag<Card>> possibleClaimCards = gameState.playerState(gameState.currentPlayerId()).possibleAdditionalCards(additionalCards,initialClaimCards,drawnCards.build());

                        //TODO : que le joueur courant a dans sa main les cartes additionnelles nécessaires, alors :
                        //la méthode chooseAdditionalCards est appelée pour déterminer s'il désire jouer des cartes additionnelles, et si oui, lesquelles.
                        //TODO : receive info
                        //TODO : end of the game

                        break;
                    }
            }


        }

    }
}
