package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public class ClientTest {
    public static void main(String[] args) {
        System.out.println("Starting client!");
        RemotePlayerClient playerClient =
                new RemotePlayerClient(new TestPlayer(),
                        "localhost",
                        5108, "TestClient_Name");
        playerClient.run();
        System.out.println("Client done!");
    }

    private final static class TestPlayer implements Player {
        private String name;
        
        @Override
        public void initPlayers(PlayerId ownId,
                                Map<PlayerId, String> names) {
            System.out.printf("ownId: %s\n", ownId);
            System.out.printf("playerNames: %s\n", names);
        }

        @Override
        public void receiveInfo(String info) {
            System.out.println(info);
        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            System.out.printf("new Public game state current PLayerId : %s\n", newState.currentPlayerId());
        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {

        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            var tickets = SortedBag.of(1,ChMap.tickets().get(0),1,ChMap.tickets().get(1));
            System.out.printf("initial tickets choice : %s \n", tickets );
            return tickets;
        }

        @Override
        public TurnKind nextTurn() {
            return null;
        }

        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            return null;
        }

        @Override
        public int drawSlot() {
            return 0;
        }

        @Override
        public Route claimedRoute() {
            return null;
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            return null;
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            var additionalCards = options.get(0);
            System.out.printf(" additional cards : %s \n", additionalCards);
            return additionalCards;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public void name(String n) {
            name = n;
            
        }

        // … autres méthodes de Player
    }
}
