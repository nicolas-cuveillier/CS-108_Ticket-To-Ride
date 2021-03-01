package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nicolas Cuveillier
 */
public class RouteTest {
    @Test
    void checkIllegalArgumentExceptionWhenSameStation(){
        Station s = new Station(0,"TEST");
        Assertions.assertThrows(IllegalArgumentException.class, ()->{new Route("test",s,s,2, Route.Level.OVERGROUND,Color.BLACK);});
    }

    @Test
    void checkNulPointerExceptionWhenNullStation(){
        Station s = null;
        Station s1 = new Station(0,"TEST");
        Station s2 = new Station(1,"TEST1");
        Assertions.assertThrows(NullPointerException.class, ()->{new Route("test",s,s,2, Route.Level.OVERGROUND,Color.BLACK);});
        Assertions.assertThrows(NullPointerException.class, ()->{new Route("test",s1,s2,2, null,Color.BLACK);});

    }

    @Test
    void checkStations(){
        Station s1 = new Station(0,"TEST");
        Station s2 = new Station(1,"TEST1");
        Route r  = new Route("test",s1,s2,2, Route.Level.OVERGROUND,Color.BLACK);
        Assertions.assertEquals(List.of(s1,s2),r.stations());
    }
    @Test
    void checkOppositeStations(){
        Station s1 = new Station(0,"TEST");
        Station s2 = new Station(1,"TEST1");
        Route r  = new Route("test",s1,s2,2, Route.Level.OVERGROUND,Color.BLACK);
        Assertions.assertEquals(s2,r.stationOpposite(s1));
        Assertions.assertThrows(IllegalArgumentException.class, ()->{r.stationOpposite(new Station(3,"other"));});
    }
    @Test
    void checkNormalAdditionalClaimCardCount(){
        Station s1 = new Station(0,"TEST");
        Station s2 = new Station(1,"TEST1");
        Route r  = new Route("test",s1,s2,2, Route.Level.UNDERGROUND,Color.BLUE);
        SortedBag<Card> claimCards = SortedBag.of(2,Card.BLUE);
        SortedBag<Card> claimCards2 = SortedBag.of(2,Card.LOCOMOTIVE);
        SortedBag<Card> drawnCards = SortedBag.of(2,Card.BLUE,1,Card.LOCOMOTIVE);
        SortedBag.Builder b = new SortedBag.Builder();
        b.add(Card.BLUE).add(Card.BLACK).add(Card.YELLOW);
        SortedBag.Builder b2 = new SortedBag.Builder();
        b2.add(1,Card.ORANGE).add(1,Card.BLACK).add(1,Card.YELLOW);
        SortedBag<Card> drawnCards2 = b.build();
        System.out.println(drawnCards2.size());
        SortedBag<Card> drawnCards3 = b2.build();
        Assertions.assertEquals(3,r.additionalClaimCardsCount(claimCards,drawnCards));
        Assertions.assertEquals(1,r.additionalClaimCardsCount(claimCards,drawnCards2));
        Assertions.assertEquals(0,r.additionalClaimCardsCount(claimCards,drawnCards3));
       Assertions.assertEquals(1,r.additionalClaimCardsCount(claimCards2,drawnCards));
    }

    @Test
    void checkPossibleClaimCards(){
        SortedBag.Builder b = new SortedBag.Builder();
        SortedBag.Builder b1 = new SortedBag.Builder();
        SortedBag.Builder b2 = new SortedBag.Builder();
        List<SortedBag<Card>> possibleClaimCards = new ArrayList<>();
        b.add(2,Card.of(Color.YELLOW));
        b1.add(1,Card.of(Color.YELLOW)).add(1,Card.LOCOMOTIVE);
        b2.add(2,Card.LOCOMOTIVE);
        possibleClaimCards.add(b.build());
        possibleClaimCards.add(b1.build());
        possibleClaimCards.add(b2.build());

        Station s1 = new Station(0,"TEST");
        Station s2 = new Station(1,"TEST1");
        Route r  = new Route("test",s1,s2,2, Route.Level.OVERGROUND,Color.YELLOW);

        Assertions.assertEquals(possibleClaimCards,r.possibleClaimCards());

    }








}
