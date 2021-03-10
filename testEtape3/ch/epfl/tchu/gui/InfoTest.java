package ch.epfl.tchu.gui;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.epfl.tchu.SortedBag;

class InfoTest {

    String p1 = "Player 1";
    String p2 = "Player 2";
    
    int i1 = 3;
    int i2 = 1;
    
    private Info infoP1 = new Info(p1);
    private Info infoP2 = new Info(p2);
    
    @Test
    void checkCardName() {
        Assertions.assertEquals("noire", infoP1.cardName(Card.BLACK, 1));
        Assertions.assertEquals("bleues", infoP1.cardName(Card.BLUE, 3));
        Assertions.assertEquals("locomotives", infoP1.cardName(Card.LOCOMOTIVE, 2));
        Assertions.assertEquals("noire", infoP2.cardName(Card.BLACK, 1));
        Assertions.assertEquals("bleues", infoP2.cardName(Card.BLUE, 3));
        Assertions.assertEquals("locomotives", infoP2.cardName(Card.LOCOMOTIVE, 2));
    }
    
    @Test
    void checkDraw() {
        List<String> l = new ArrayList<>();
        l.add(p1);
        l.add(p2);
        Assertions.assertEquals(String.format(StringsFr.DRAW, String.format("%s%s%s", l.get(0),StringsFr.AND_SEPARATOR, l.get(1)), i1), infoP1.draw(l, i1));
        Assertions.assertEquals(String.format(StringsFr.DRAW, String.format("%s%s%s", l.get(0),StringsFr.AND_SEPARATOR, l.get(1)), i2), infoP2.draw(l, i2));
    }

    @Test
    void checkWillPlayFirst() {
        Assertions.assertEquals(String.format(StringsFr.WILL_PLAY_FIRST,"Player 1"), infoP1.willPlayFirst());
        Assertions.assertEquals(String.format(StringsFr.WILL_PLAY_FIRST,"Player 2"), infoP2.willPlayFirst());
    }
    
    @Test
    void checkKeptTickets() {
        Assertions.assertEquals("Player 1 a gardé 3 billets.\n", infoP1.keptTickets(i1));
        Assertions.assertEquals("Player 2 a gardé 1 billet.\n", infoP2.keptTickets(i2));
    }
    
    @Test
    void checkCanPlay() {
        Assertions.assertEquals("\nC'est à Player 1 de jouer.\n", infoP1.canPlay());
        Assertions.assertEquals(String.format(StringsFr.CAN_PLAY, "Player 1"), infoP1.canPlay());
        Assertions.assertEquals(String.format(StringsFr.CAN_PLAY, "Player 2"), infoP2.canPlay());
    }
    
    @Test
    void checkDrewTickets() {
        Assertions.assertEquals("Player 2 a tiré 1 billet...\n", infoP2.drewTickets(i2));
        Assertions.assertEquals("Player 1 a tiré 5 billets...\n", infoP1.drewTickets(5));
        Assertions.assertEquals(String.format(StringsFr.DREW_TICKETS, p1, i1, StringsFr.plural(i1)), infoP1.drewTickets(i1));
        Assertions.assertEquals(String.format(StringsFr.DREW_TICKETS, p1, i2, StringsFr.plural(i2)), infoP1.drewTickets(i2));
    }
    
    @Test
    void checkDrewBlindCard() {
        Assertions.assertEquals("Player 1 a tiré une carte de la pioche.\n", infoP1.drewBlindCard());
        Assertions.assertEquals(String.format(StringsFr.DREW_BLIND_CARD, p1), infoP1.drewBlindCard());
        Assertions.assertEquals(String.format(StringsFr.DREW_BLIND_CARD, p2), infoP2.drewBlindCard());
    }
    
    @Test
    void checkDrewVisibleCard() {
        Assertions.assertEquals("Player 2 a tiré une carte bleue visible.\n", infoP2.drewVisibleCard(Card.BLUE));
        Assertions.assertEquals(String.format(StringsFr.DREW_VISIBLE_CARD, p1, getFrName(Card.BLACK,1)), infoP1.drewVisibleCard(Card.BLACK));
        Assertions.assertEquals(String.format(StringsFr.DREW_VISIBLE_CARD, p1, getFrName(Card.BLUE,1)), infoP1.drewVisibleCard(Card.BLUE));
        Assertions.assertEquals(String.format(StringsFr.DREW_VISIBLE_CARD, p1, getFrName(Card.LOCOMOTIVE,1)), infoP1.drewVisibleCard(Card.LOCOMOTIVE));
        Assertions.assertEquals(String.format(StringsFr.DREW_VISIBLE_CARD, p2, getFrName(Card.BLACK,1)), infoP2.drewVisibleCard(Card.BLACK));
        Assertions.assertEquals(String.format(StringsFr.DREW_VISIBLE_CARD, p2, getFrName(Card.BLUE,1)), infoP2.drewVisibleCard(Card.BLUE));
        Assertions.assertEquals(String.format(StringsFr.DREW_VISIBLE_CARD, p2, getFrName(Card.LOCOMOTIVE,1)), infoP2.drewVisibleCard(Card.LOCOMOTIVE));
    }
    
    @Test
    void checkClaimedRoute() {
        Route routeTest = new Route("routeTest", new Station(0, "From"),
                new Station(1, "To"), 2, Route.Level.OVERGROUND, Color.BLACK);

        Assertions.assertEquals("Player 1 a pris possession de la route From" + StringsFr.EN_DASH_SEPARATOR + "To au moyen de 2 noires.\n", infoP1.claimedRoute(routeTest, SortedBag.of(2, Card.BLACK)));

        //ton test passe pas chez moi car ca met la ref de la route
        /*Route r1 = new Route("r1", new Station(0,"A"), new Station(1,"B"), i1, Route.Level.OVERGROUND, Color.BLACK);

        SortedBag<Card> c1 = SortedBag.of(2, Card.BLACK, 1, Card.LOCOMOTIVE);
        String s = "";
        for(Card c : c1.toSet()) {
            int n = c1.countOf(c);
                if(c1.get(c1.size()-1).equals(c)) {
                    s += " et ";
                }
                else if(!c1.get(0).equals(c)){
                    s+= ", ";
                }
                s += n + " " + getFrName(c, n);
            }
        Assertions.assertEquals(String.format(StringsFr.CLAIMED_ROUTE, p1, r1, s), infoP1.claimedRoute(r1, c1));

         */
    }

    @Test
    void checkAttemptsTunnelClaim() {
        Route routeTest = new Route("routeTest", new Station(0, "From"),
                new Station(1, "To"), 3, Route.Level.UNDERGROUND, Color.BLACK);

        SortedBag.Builder<Card> b = new SortedBag.Builder<>();
        b.add(2, Card.BLACK).add(1, Card.LOCOMOTIVE);

        Assertions.assertEquals("Player 1 tente de s'emparer du tunnel From" + StringsFr.EN_DASH_SEPARATOR + "To au moyen de 2 noires, 1 locomotive !\n",
                infoP1.attemptsTunnelClaim(routeTest, b.build()));
    }

    @Test
    void checkDrewAdditionalCards() {
        SortedBag.Builder<Card> b = new SortedBag.Builder<>();
        b.add(1, Card.YELLOW).add(1, Card.RED).add(1, Card.BLACK);

        Assertions.assertEquals("Les cartes supplémentaires sont 1 noire, 1 jaune, 1 rouge. Elles n'impliquent aucun coût additionnel.\n"
                , infoP1.drewAdditionalCards(b.build(), 0));
        Assertions.assertEquals("Les cartes supplémentaires sont 1 noire, 1 jaune, 1 rouge. Elles impliquent un coût additionnel de 2 cartes.\n"
                , infoP1.drewAdditionalCards(b.build(), 2));
        Assertions.assertEquals("Les cartes supplémentaires sont 1 noire, 1 jaune, 1 rouge. Elles impliquent un coût additionnel de 1 carte.\n"
                , infoP1.drewAdditionalCards(b.build(), 1));

    }

    @Test
    void checkDidNotClaimRoute() {
        Route routeTest = new Route("routeTest", new Station(0, "From"),
                new Station(1, "To"), 3, Route.Level.UNDERGROUND, Color.BLACK);
        Assertions.assertEquals("Player 2 n'a pas pu (ou voulu) s'emparer de la route From"
                + StringsFr.EN_DASH_SEPARATOR + "To.\n", infoP2.didNotClaimRoute(routeTest));
    }

    @Test
    void checkLastTurnBegins() {
        Assertions.assertEquals("\nPlayer 2 n'a plus que 2 wagons, le dernier tour commence !\n", infoP2.lastTurnBegins(2));
        Assertions.assertEquals("\nPlayer 2 n'a plus que 1 wagon, le dernier tour commence !\n", infoP2.lastTurnBegins(1));

    }

    @Test
    void checkGetsLongestTrailBonus() {
        Route r1 = new Route("BER_LUC_1", new Station(16, "Lucerne"), new Station(3, "Berne"), 4, Route.Level.OVERGROUND, null);
        Route r2 = new Route("BER_NEU_1", new Station(3, "Berne"), new Station(19, "Neuchâtel"), 2, Route.Level.OVERGROUND, Color.RED);
        Route r3 = new Route("NEU_SOL_1", new Station(19, "Neuchâtel"), new Station(26, "Soleure"), 4, Route.Level.OVERGROUND, Color.GREEN);
        Route r4 = new Route("BER_SOL_1", new Station(26, "Soleure"), new Station(3, "Berne"), 2, Route.Level.OVERGROUND, Color.BLACK);
        Route r5 = new Route("BER_FRI_1", new Station(3, "Berne"), new Station(9, "Fribourg"), 1, Route.Level.OVERGROUND, Color.ORANGE);

        List<Route> routes = List.of(r1, r2, r3, r4, r5);

        Assertions.assertEquals("\nPlayer 1 reçoit un bonus de 10 points pour le plus long trajet (Lucerne" + StringsFr.EN_DASH_SEPARATOR + "Fribourg).\n", infoP1.getsLongestTrailBonus(Trail.longest(routes)));
    }

    @Test
    void checkWon() {
        Assertions.assertEquals("\nPlayer 1 remporte la victoire avec 20 points, contre 12 points !\n", infoP1.won(20, 12));
        Assertions.assertEquals("\nPlayer 1 remporte la victoire avec 20 points, contre 1 point !\n", infoP1.won(20, 1));
    }


    private String getFrName(Card card, int count) {
        switch (card) {
        case BLACK:
            return StringsFr.BLACK_CARD + StringsFr.plural(count);
        case VIOLET:
            return StringsFr.VIOLET_CARD + StringsFr.plural(count);
        case BLUE:
            return StringsFr.BLUE_CARD + StringsFr.plural(count);
        case GREEN:
            return StringsFr.GREEN_CARD + StringsFr.plural(count);
        case YELLOW:
            return StringsFr.YELLOW_CARD + StringsFr.plural(count);
        case ORANGE:
            return StringsFr.ORANGE_CARD + StringsFr.plural(count);
        case RED:
            return StringsFr.RED_CARD + StringsFr.plural(count);
        case WHITE:
            return StringsFr.WHITE_CARD + StringsFr.plural(count);
        case LOCOMOTIVE:
            return StringsFr.LOCOMOTIVE_CARD + StringsFr.plural(count);
        default:
            return "";
    }
    }
}
