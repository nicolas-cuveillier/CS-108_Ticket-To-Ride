package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        b.add(Card.BLUE);
        b.add(Card.BLACK);
        b.add(Card.YELLOW);
        SortedBag.Builder b2 = new SortedBag.Builder();
        b2.add(1,Card.ORANGE);
        b2.add(1,Card.BLACK);
        b2.add(1,Card.YELLOW);
        SortedBag<Card> drawnCards2 = b.build();
        System.out.println(drawnCards2.size());
        SortedBag<Card> drawnCards3 = b2.build();
        Assertions.assertEquals(3,r.additionalClaimCardsCount(claimCards,drawnCards));
        Assertions.assertEquals(1,r.additionalClaimCardsCount(claimCards,drawnCards2));
        Assertions.assertEquals(0,r.additionalClaimCardsCount(claimCards,drawnCards3));
       Assertions.assertEquals(1,r.additionalClaimCardsCount(claimCards2,drawnCards));
    }







}
