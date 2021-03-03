package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public class RouteTest {
    @Test
    void checkIllegalArgumentExceptionWhenSameStation(){
        Station s = new Station(0,"TEST");
        Assertions.assertThrows(IllegalArgumentException.class, ()->{new Route("test",s,s,2, Route.Level.OVERGROUND,Color.BLACK);});
        Assertions.assertThrows(IllegalArgumentException.class, ()->{new Route("test",s,s,7, Route.Level.OVERGROUND,Color.BLACK);});
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
    /*
    un truc à verifier : en gros je sais pas si par exemple pour prendre une route bleu, si tu tires 2 cartes bleue et une autre carte
    ca fait 2 additional card ou juste une. Genre est-ce que tu comptes que y'a deux bleue dans la pioche donc 2additonal ou juste 1.
    si c'est juste une il faut changer la methode
     */
    @Test
    void checkNormalAdditionalClaimCardCount(){
        Station s1 = new Station(0,"TEST");
        Station s2 = new Station(1,"TEST1");
        Route r  = new Route("test",s1,s2,2, Route.Level.UNDERGROUND,Color.BLUE);

        SortedBag<Card> claimCards = SortedBag.of(2,Card.BLUE);
        SortedBag<Card> claimCards2 = SortedBag.of(2,Card.LOCOMOTIVE);

        SortedBag.Builder b = new SortedBag.Builder();
        b.add(Card.BLUE).add(Card.BLUE).add(Card.YELLOW);
        SortedBag.Builder b2 = new SortedBag.Builder();
        b2.add(1,Card.ORANGE).add(1,Card.BLACK).add(1,Card.YELLOW);

        SortedBag<Card> drawnCards = SortedBag.of(2,Card.BLUE,1,Card.LOCOMOTIVE);
        SortedBag<Card> drawnCards2 = b.build();
        SortedBag<Card> drawnCards3 = b2.build();
        Assertions.assertEquals(3,r.additionalClaimCardsCount(claimCards,drawnCards));
        Assertions.assertEquals(2,r.additionalClaimCardsCount(claimCards,drawnCards2));
        Assertions.assertEquals(0,r.additionalClaimCardsCount(claimCards,drawnCards3));
        Assertions.assertEquals(1,r.additionalClaimCardsCount(claimCards2,drawnCards));
    }

    @Test
    void checkPossibleClaimCards(){
        SortedBag.Builder b = new SortedBag.Builder();
        SortedBag.Builder b1 = new SortedBag.Builder();
        SortedBag.Builder b2 = new SortedBag.Builder();
        SortedBag.Builder b3 = new SortedBag.Builder();
        SortedBag.Builder b4;
        SortedBag.Builder b5 ;
        SortedBag.Builder b6 ;
        List<SortedBag<Card>> possibleClaimCards = new ArrayList<>();
        List<SortedBag<Card>> possibleClaimCards2 = new ArrayList<>();
        List<SortedBag<Card>> possibleClaimCards3 = new ArrayList<>();
        List<SortedBag<Card>> possibleClaimCards4= new ArrayList<>();

        b.add(2,Card.of(Color.YELLOW));
        b1.add(1,Card.of(Color.YELLOW)).add(1,Card.LOCOMOTIVE);
        b2.add(2,Card.LOCOMOTIVE);
        b3.add(2,Card.of(Color.YELLOW));


        possibleClaimCards.add(b.build());
        possibleClaimCards.add(b1.build());
        possibleClaimCards.add(b2.build());
        possibleClaimCards2.add(b3.build());

        for (Color c :Color.ALL) {
            b4 = new SortedBag.Builder();
            b4.add(2,c);
            possibleClaimCards3.add(b4.build());
        }

        for (Color c :Color.ALL) {
            b5 = new SortedBag.Builder();
            b5.add(2,Card.of(c));
            possibleClaimCards4.add(b5.build());
        }
        b5 = new SortedBag.Builder();
        b5.add(1,Card.BLACK).add(1,Card.LOCOMOTIVE);
        possibleClaimCards4.add(b5.build());
        b5 = new SortedBag.Builder();
        b5.add(1,Card.VIOLET).add(1,Card.LOCOMOTIVE);
        possibleClaimCards4.add(b5.build());
        b5 = new SortedBag.Builder();
        b5.add(1,Card.BLUE).add(1,Card.LOCOMOTIVE);
        possibleClaimCards4.add(b5.build());
        b5 = new SortedBag.Builder();
        b5.add(1,Card.GREEN).add(1,Card.LOCOMOTIVE);
        possibleClaimCards4.add(b5.build());
        b5 = new SortedBag.Builder();
        b5.add(1,Card.YELLOW).add(1,Card.LOCOMOTIVE);
        possibleClaimCards4.add(b5.build());
        b5 = new SortedBag.Builder();
        b5.add(1,Card.ORANGE).add(1,Card.LOCOMOTIVE);
        possibleClaimCards4.add(b5.build());
        b5 = new SortedBag.Builder();
        b5.add(1,Card.RED).add(1,Card.LOCOMOTIVE);
        possibleClaimCards4.add(b5.build());
        b5 = new SortedBag.Builder();
        b5.add(1,Card.WHITE).add(1,Card.LOCOMOTIVE);
        possibleClaimCards4.add(b5.build());
        b5 = new SortedBag.Builder();
        b5.add(2,Card.LOCOMOTIVE);
        possibleClaimCards4.add(b5.build());


        Station s1 = new Station(0,"TEST");
        Station s2 = new Station(1,"TEST1");
        Route r  = new Route("test",s1,s2,2, Route.Level.UNDERGROUND,Color.YELLOW);
        Route r1  = new Route("test1",s1,s2,2, Route.Level.OVERGROUND,Color.YELLOW);
        Route r2  = new Route("test2",s1,s2,2, Route.Level.OVERGROUND,null);
        Route r3  = new Route("test3",s1,s2,2, Route.Level.UNDERGROUND,null);

        Assertions.assertEquals(possibleClaimCards,r.possibleClaimCards());
        Assertions.assertEquals(possibleClaimCards2,r1.possibleClaimCards());
        Assertions.assertEquals(possibleClaimCards3,r2.possibleClaimCards());
       Assertions.assertEquals(possibleClaimCards4,r3.possibleClaimCards());

    }
}
