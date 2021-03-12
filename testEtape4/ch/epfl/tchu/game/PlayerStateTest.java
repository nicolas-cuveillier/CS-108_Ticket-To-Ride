package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public class PlayerStateTest {
    @Test
    void checkInitial(){
        SortedBag.Builder<Card> b = new SortedBag.Builder<>();
        b.add(Card.BLACK).add(Card.LOCOMOTIVE).add(2, Card.ORANGE).add(Card.WHITE).add(Card.BLUE).add(Card.GREEN);
        SortedBag<Card> cards = b.build();

        SortedBag.Builder<Card> b2 = new SortedBag.Builder<>();
        b2.add(Card.BLACK).add(Card.LOCOMOTIVE);
        SortedBag<Card> cards2 = b2.build();

        Assertions.assertThrows(IllegalArgumentException.class,()->PlayerState.initial(cards));
        Assertions.assertThrows(IllegalArgumentException.class,()->PlayerState.initial(cards2));
    }
    @Test
    void checkWithAddedTickets(){
        SortedBag.Builder<Card> b = new SortedBag.Builder<>();
        b.add(Card.BLACK).add(Card.LOCOMOTIVE).add(2, Card.ORANGE);
        SortedBag<Card> cards = b.build();
        PlayerState p = PlayerState.initial(cards);
        //tickets
        SortedBag.Builder<Ticket> t = new SortedBag.Builder<>();
        t.add(new Ticket(new Station(0,"From"),new Station(1,"To"),2));
        SortedBag<Ticket> tickets = t.build();

        Assertions.assertEquals(new PlayerState(tickets,cards, List.of()).tickets(),p.withAddedTickets(tickets).tickets());
    }
    @Test
    void checkWithAddedCard(){
        SortedBag.Builder<Card> b = new SortedBag.Builder<>();
        b.add(Card.BLACK).add(Card.LOCOMOTIVE).add(2, Card.ORANGE);
        SortedBag<Card> cards = b.build();
        PlayerState p = PlayerState.initial(cards);

        SortedBag.Builder<Card> b2 = new SortedBag.Builder<>();
        b2.add(Card.BLACK).add(Card.LOCOMOTIVE).add(2, Card.ORANGE).add(Card.WHITE);
        SortedBag<Card> cards2 = b2.build();

        Assertions.assertEquals(new PlayerState(SortedBag.of(),cards2, List.of()).cards(),p.withAddedCard(Card.WHITE).cards());
    }
    @Test
    void checkWithAddedCards(){
        SortedBag.Builder<Card> b = new SortedBag.Builder<>();
        b.add(Card.BLACK).add(Card.LOCOMOTIVE).add(2, Card.ORANGE);
        SortedBag<Card> cards = b.build();
        PlayerState p = PlayerState.initial(cards);

        b = new SortedBag.Builder<>();
        b.add(Card.WHITE).add(Card.GREEN);
        SortedBag<Card> moreCards = b.build();

        SortedBag.Builder<Card> b2 = new SortedBag.Builder<>();
        b2.add(Card.BLACK).add(Card.LOCOMOTIVE).add(2, Card.ORANGE).add(Card.WHITE).add(Card.GREEN);
        SortedBag<Card> cards2 = b2.build();

        Assertions.assertEquals(new PlayerState(SortedBag.of(),cards2, List.of()).cards(),p.withAddedCards(moreCards).cards());
    }
    @Test
    void checkCanClaimRoute(){
        SortedBag.Builder<Card> b = new SortedBag.Builder<>();
        b.add(Card.BLACK).add(Card.LOCOMOTIVE).add(2, Card.ORANGE);
        SortedBag<Card> cards = b.build();
        PlayerState p = PlayerState.initial(cards);
        Route r = new Route("test",new Station(0,"From"),new Station(1,"To"),2, Route.Level.OVERGROUND,Color.ORANGE);
        Route r2 = new Route("test",new Station(0,"From"),new Station(1,"To"),2, Route.Level.OVERGROUND,Color.BLACK);
        Assertions.assertTrue(p.canClaimRoute(r));
        Assertions.assertFalse(p.canClaimRoute(r2));
    }

    @Test
    void checkPossibleClaimCards(){

    }
    @Test
    void checkPossibleAdditionalCards(){

    }
    @Test
    void checkWithClaimedRoute(){

    }
    @Test
    void checkTicketPoints(){

    }
    @Test
    void checkFinalPoints(){

    }
}
