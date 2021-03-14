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
        SortedBag.Builder<Card> b = new SortedBag.Builder<>();
        b.add(Card.BLACK).add(Card.LOCOMOTIVE).add(2, Card.ORANGE);
        SortedBag<Card> cards = b.build();
        PlayerState p = PlayerState.initial(cards);
        Route r = new Route("test",new Station(0,"From"),
                new Station(1,"To"),2, Route.Level.OVERGROUND,Color.ORANGE);
        //test
        SortedBag.Builder<Card> b2 = new SortedBag.Builder<>();
        b2.add(2, Card.ORANGE);
        SortedBag<Card> cards2 = b2.build();
        //b2 = new SortedBag.Builder<>();
        //b2.add(Card.ORANGE).add(Card.LOCOMOTIVE);
        //SortedBag<Card> cards3 = b2.build();
        List<SortedBag<Card>> neededList = List.of(cards2);

        Assertions.assertEquals(neededList,p.possibleClaimCards(r));

    }
    @Test
    void checkPossibleAdditionalCards(){
        //checkException
        SortedBag.Builder<Card> initialCards1 = new SortedBag.Builder<>();
        initialCards1.add(2,Card.BLACK);

        SortedBag.Builder<Card> toLargeDrawnCards = new SortedBag.Builder<>();
        toLargeDrawnCards.add(4,Card.LOCOMOTIVE);
        SortedBag.Builder<Card> normalDrawnCards = new SortedBag.Builder<>();
        normalDrawnCards.add(3,Card.LOCOMOTIVE);

        SortedBag.Builder<Card> cardsBuilder = new SortedBag.Builder<>();
        cardsBuilder.add(Card.BLACK).add(Card.LOCOMOTIVE).add(2, Card.ORANGE);
        SortedBag<Card> cards = cardsBuilder.build();
        PlayerState p = PlayerState.initial(cards);

        Assertions.assertThrows(IllegalArgumentException.class,
                ()->p.possibleAdditionalCards(2,initialCards1.build(),toLargeDrawnCards.build()));
        Assertions.assertThrows(IllegalArgumentException.class,
                ()->p.possibleAdditionalCards(4,initialCards1.build(),normalDrawnCards.build()));

        //concrete check up
        SortedBag.Builder<Card> Cards = new SortedBag.Builder<>();
        Cards.add(3,Card.GREEN).add(2,Card.BLUE).add(2,Card.LOCOMOTIVE);

        PlayerState p2 = new PlayerState(SortedBag.of(),Cards.build(),List.of());
        SortedBag.Builder<Card> initialCards = new SortedBag.Builder<>();
        initialCards.add(Card.GREEN);

        SortedBag.Builder<Card> neededCards = new SortedBag.Builder<>();
        neededCards.add(2,Card.GREEN);
        SortedBag.Builder<Card> neededCards1 = new SortedBag.Builder<>();
        neededCards1.add(1,Card.GREEN).add(Card.LOCOMOTIVE);
        SortedBag.Builder<Card> neededCards2 = new SortedBag.Builder<>();
        neededCards2.add(2,Card.LOCOMOTIVE);

        Assertions.assertEquals(List.of(neededCards.build(),neededCards1.build(),neededCards2.build()),
                p2.possibleAdditionalCards(2,initialCards.build(),normalDrawnCards.build()));
    }
    @Test
    void checkWithClaimedRoute(){
        SortedBag.Builder<Card> b = new SortedBag.Builder<>();
        b.add(Card.BLACK).add(Card.LOCOMOTIVE).add(2, Card.ORANGE);
        SortedBag<Card> cards = b.build();
        PlayerState p = PlayerState.initial(cards);
        Route r = new Route("test",new Station(0,"From"),
                new Station(1,"To"),2, Route.Level.OVERGROUND,Color.ORANGE);
        //test
        SortedBag.Builder<Card> b2 = new SortedBag.Builder<>();
        b2.add(2, Card.ORANGE);
        SortedBag<Card> cards2 = b2.build();

        SortedBag.Builder<Card> b3 = new SortedBag.Builder<>();
        b3.add(Card.BLACK).add(Card.LOCOMOTIVE);
        SortedBag<Card> cards3 = b3.build();
        Assertions.assertEquals(List.of(r),p.withClaimedRoute(r,cards2).routes());
        Assertions.assertEquals(cards3,p.withClaimedRoute(r,cards2).cards());
    }
    @Test
    void checkTicketPoints(){


    }
    @Test
    void checkFinalPoints(){

    }
}
