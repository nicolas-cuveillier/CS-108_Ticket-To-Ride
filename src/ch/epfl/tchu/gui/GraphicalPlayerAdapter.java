package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import static javafx.application.Platform.runLater;

/**
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public final class GraphicalPlayerAdapter implements Player {
    private GraphicalPlayer graphicalPlayer;
    private ArrayBlockingQueue<SortedBag<Ticket>> blockingTicketsQueue;
    private ArrayBlockingQueue<TurnKind> blockingTurnKindQueue;
    private ArrayBlockingQueue<Integer> blockingCardIndexQueue;
    private ArrayBlockingQueue<Route> blockingRouteQueue;
    private ArrayBlockingQueue<SortedBag<Card>> blockingCardsQueue;

    public GraphicalPlayerAdapter() {
        blockingTicketsQueue = new ArrayBlockingQueue<>(5);//todo not sure about that
        blockingTurnKindQueue = new ArrayBlockingQueue<>(1);
        blockingCardIndexQueue = new ArrayBlockingQueue<>(1);
        blockingRouteQueue = new ArrayBlockingQueue<>(1);
        blockingCardsQueue = new ArrayBlockingQueue<>(Constants.ALL_CARDS.size());
        graphicalPlayer = null;
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        runLater(() -> graphicalPlayer = new GraphicalPlayer(ownId, playerNames));
    }

    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(() -> graphicalPlayer.setState(newState, ownState));
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        runLater(() -> graphicalPlayer.chooseTickets(tickets, s -> blockingTicketsQueue.add(s)));
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        try {
            return blockingTicketsQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    public TurnKind nextTurn() {
        runLater(() -> {
            graphicalPlayer.startTurn(() -> blockingTurnKindQueue.add(TurnKind.DRAW_TICKETS),
                    (s) -> {
                        blockingTurnKindQueue.add(TurnKind.DRAW_CARDS);
                        blockingCardIndexQueue.add(s);
                    },
                    (r, s) -> {
                        blockingTurnKindQueue.add(TurnKind.CLAIM_ROUTE);
                        blockingRouteQueue.add(r);
                        blockingCardsQueue.add(s);
                    });
        });

        try {
            return blockingTurnKindQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        setInitialTicketChoice(options);
        return chooseInitialTickets();
    }

    @Override
    public int drawSlot() {
        if (!blockingCardIndexQueue.isEmpty())
            return blockingCardIndexQueue.remove();
        else {
            runLater(() -> graphicalPlayer.drawCard((s) -> blockingCardIndexQueue.add(s)));
            try {
                return blockingCardIndexQueue.take();
            } catch (InterruptedException e) {
                throw new Error();
            }
        }

    }

    @Override
    public Route claimedRoute() {
        try {
            return blockingRouteQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        try {
            return blockingCardsQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        runLater(() -> graphicalPlayer.chooseAdditionalCards(options,(s) -> blockingCardsQueue.add(s)));
        try {
            return blockingCardsQueue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }
}
