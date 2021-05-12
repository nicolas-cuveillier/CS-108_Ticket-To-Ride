package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import static javafx.application.Platform.runLater;

/**
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 * <p>
 * adapte the notion of graphical player as a real Player
 */
public final class GraphicalPlayerAdapter implements Player {
    private GraphicalPlayer graphicalPlayer;
    private final ArrayBlockingQueue<SortedBag<Ticket>> blockingTicketsQueue;
    private final ArrayBlockingQueue<TurnKind> blockingTurnKindQueue;
    private final ArrayBlockingQueue<Integer> blockingCardIndexQueue;
    private final ArrayBlockingQueue<Route> blockingRouteQueue;
    private final ArrayBlockingQueue<SortedBag<Card>> blockingCardsQueue;

    /**
     * Unique constructor of a Graphical adapter which is in charge to simply initialize BlockingQueue
     */
    public GraphicalPlayerAdapter() {
        blockingTicketsQueue = new ArrayBlockingQueue<>(5);
        blockingTurnKindQueue = new ArrayBlockingQueue<>(1);
        blockingCardIndexQueue = new ArrayBlockingQueue<>(1);
        blockingRouteQueue = new ArrayBlockingQueue<>(1);
        blockingCardsQueue = new ArrayBlockingQueue<>(Constants.ALL_CARDS.size());
        graphicalPlayer = null;
    }

    /**
     * Build an instance of the JavaFX thread an instance of Graphical player.
     *
     * @param ownId       Id of the player (self)
     * @param playerNames Ids of all the payers, mapped to their name (includes self)
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        runLater(() -> graphicalPlayer = new GraphicalPlayer(ownId, playerNames));
    }

    /**
     * Call the method receiveInfo of the graphicalPlayer on the JavaFX thread
     *
     * @param info Information to give to the player, parsed by the class Info
     */
    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    /**
     * Call the method setState of the graphicalPlayer on the JavaFX thread
     *
     * @param newState new state of the game
     * @param ownState current state of the player
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(() -> graphicalPlayer.setState(newState, ownState));
    }

    /**
     * Call the method chooseTickets of the graphicalPlayer on the JavaFX thread so that the human player choose
     * tickets and those tickets will be added to the corresponding blocking queue
     *
     * @param tickets 5 initial tickets assigned to the player
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        runLater(() -> graphicalPlayer.chooseTickets(tickets, blockingTicketsQueue::add));
    }

    /**
     * The Blocking Queue of tickets waits until the human player made its choice and then return its choice
     *
     * @return the sortedBag of tickets that the human player has chosen
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        try {
            return blockingTicketsQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    /**
     * Call the method startTurn of the graphicalPlayer on the JavaFX thread and fill the different Handler. For the
     * DrawTicketsHandler, does nothing except adding the corresponding TurnKind to the corresponding blocking queue.
     * For DrawCardsHandler, does the same thing and add the index of the card in the corresponding blocking queue. For
     * ClaimRouteHandler, does the same thing but with adding the route and the cards in the corresponding blocking queue.
     *
     * @return the Kind of the turn that the player has chosen to play
     */
    @Override
    public TurnKind nextTurn() {
        runLater(() -> graphicalPlayer.startTurn(() -> blockingTurnKindQueue.add(TurnKind.DRAW_TICKETS),
                (s) -> {
                    blockingTurnKindQueue.add(TurnKind.DRAW_CARDS);
                    blockingCardIndexQueue.add(s);
                },
                (r, s) -> {
                    blockingTurnKindQueue.add(TurnKind.CLAIM_ROUTE);
                    blockingRouteQueue.add(r);
                    blockingCardsQueue.add(s);
                }));

        try {
            return blockingTurnKindQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    /**
     * Call successively the setInitialTicketChoice and the chooseInitialTickets method.
     *
     * @param options the tickets drawn by the player
     * @return the sortedBag of tickets that the human player has chosen
     * @see #setInitialTicketChoice(SortedBag)
     * @see #chooseInitialTickets()
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        setInitialTicketChoice(options);
        return chooseInitialTickets();
    }

    /**
     * If the card index blocking queue is containing a value, it return the value. Otherwise, it means that drawSlot
     * is called for the second time of the turn, so it call, on the JavaFX thread, the drawCard method of the
     * graphicPlayer, before blocking while waiting for the next card index to be placed in the queue, which is then
     * extracted and returned.
     *
     * @return the index of the drawn card
     */
    @Override
    public int drawSlot() {
        if (!blockingCardIndexQueue.isEmpty())
            return blockingCardIndexQueue.remove();
        else {
            runLater(() -> graphicalPlayer.drawCard(blockingCardIndexQueue::add));
            try {
                return blockingCardIndexQueue.take();
            } catch (InterruptedException e) {
                throw new Error();
            }
        }

    }

    /**
     * Block while waiting for the blocking queue of Route to be fill with the claimed route by the human player and
     * returned it.
     *
     * @return the claimed route
     */
    @Override
    public Route claimedRoute() {
        try {
            return blockingRouteQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    /**
     * Block while waiting for the blocking queue of sortedBag of cards to be fill with the chosen cards by the human
     * player and returned it.
     *
     * @return the sortedBag of cards chosen by the player
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        try {
            return blockingCardsQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    /**
     * Call the method chooseAdditionalCards of the graphicalPlayer on the JavaFX thread with the list of option and
     * define the ChooseCardsHandler so it put the result in the corresponding blocking queue. Then, Block while waiting
     * for the blocking queue of sortedBag of cards to be fill with the chosen cards by the human player and returned it.
     *
     * @param options the additional cards to claim the tunnel
     * @return the sortedBag of additional cards chosen by the player
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        runLater(() -> graphicalPlayer.chooseAdditionalCards(options, blockingCardsQueue::add));
        try {
            return blockingCardsQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }
}
