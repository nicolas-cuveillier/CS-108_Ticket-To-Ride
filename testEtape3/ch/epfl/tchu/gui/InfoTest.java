package ch.epfl.tchu.gui;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Color;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Station;
import sun.rmi.server.WeakClassHashMap;

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
        Assertions.assertEquals(String.format(StringsFr.KEPT_N_TICKETS, "Player 1", i1), infoP1.keptTickets(i1));
        Assertions.assertEquals(String.format(StringsFr.KEPT_N_TICKETS, "Player 2", i2), infoP2.keptTickets(i2));
    }
    
    @Test
    void checkCanPlay() {
        Assertions.assertEquals(String.format(StringsFr.CAN_PLAY, "Player 1"), infoP1.canPlay());
        Assertions.assertEquals(String.format(StringsFr.CAN_PLAY, "Player 2"), infoP2.canPlay());
    }
    
    @Test
    void checkDrewTickets() {
        Assertions.assertEquals(String.format(StringsFr.DREW_TICKETS, p1, i1, StringsFr.plural(i1)), infoP1.drewTickets(i1));
        Assertions.assertEquals(String.format(StringsFr.DREW_TICKETS, p1, i2, StringsFr.plural(i2)), infoP1.drewTickets(i2));
    }
    
    @Test
    void checkDrewBlindCard() {
        Assertions.assertEquals(String.format(StringsFr.DREW_BLIND_CARD, p1), infoP1.drewBlindCard());
        Assertions.assertEquals(String.format(StringsFr.DREW_BLIND_CARD, p2), infoP2.drewBlindCard());
    }
    
    @Test
    void checkDrewVisibleCard() {
        Assertions.assertEquals(String.format(StringsFr.DREW_VISIBLE_CARD, p1, Card.BLACK), infoP1.drewVisibleCard(Card.BLACK));
        Assertions.assertEquals(String.format(StringsFr.DREW_VISIBLE_CARD, p1, Card.BLUE), infoP1.drewVisibleCard(Card.BLUE));
        Assertions.assertEquals(String.format(StringsFr.DREW_VISIBLE_CARD, p1, Card.LOCOMOTIVE), infoP1.drewVisibleCard(Card.LOCOMOTIVE));
        Assertions.assertEquals(String.format(StringsFr.DREW_VISIBLE_CARD, p2, Card.BLACK), infoP2.drewVisibleCard(Card.BLACK));
        Assertions.assertEquals(String.format(StringsFr.DREW_VISIBLE_CARD, p2, Card.BLUE), infoP2.drewVisibleCard(Card.BLUE));
        Assertions.assertEquals(String.format(StringsFr.DREW_VISIBLE_CARD, p2, Card.LOCOMOTIVE), infoP2.drewVisibleCard(Card.LOCOMOTIVE));
    }
    
    @Test
    void checkClaimedRoute() {
        Route r1 = new Route("r1", new Station(0,"A"), new Station(1,"B"), i1, Route.Level.OVERGROUND, Color.BLACK);
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
    }
    
    @Test
    void checkAttemptsTunnelClaim() {
        
    }
    
    @Test
    void checkDrewAdditionalCards() {
        
    }
    
    @Test
    void checkDidNotClaimRoute() {
        
    }
    
    @Test
    void checkLastTurnBegins() {
        
    }
    
    @Test
    void checkGetsLongestTrailBonus() {
        
    }
    
    @Test
    void checkWon() {
        
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
