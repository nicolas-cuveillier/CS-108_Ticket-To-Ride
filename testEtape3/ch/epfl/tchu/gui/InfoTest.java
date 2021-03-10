package ch.epfl.tchu.gui;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.epfl.tchu.game.Card;

class InfoTest {

    private Info infoP1 = new Info("Player 1");
    private Info infoP2 = new Info("Player 2");
    
    @Test
    void checkCardName() {
        Assertions.assertEquals("noire", infoP1.cardName(Card.BLACK, 1));
        Assertions.assertEquals("bleues", infoP1.cardName(Card.BLUE, 3));
        Assertions.assertEquals("locomotives", infoP1.cardName(Card.LOCOMOTIVE, 2));
    }
    
    @Test
    void checkDraw() {
        List<String> l = new ArrayList<>();
        l.add("Player_1");
        l.add("Player_2");
        Assertions.assertEquals("\nPlayer_1 et Player_2 sont ex æqo avec 3 points !\n", infoP1.draw(l, 3));
    }

    @Test
    void checkWillPlayFirst() {
        Assertions.assertEquals(String.format(StringsFr.WILL_PLAY_FIRST,"Player 1"), infoP1.willPlayFirst());
        Assertions.assertEquals(String.format(StringsFr.WILL_PLAY_FIRST,"Player 2"), infoP2.willPlayFirst());
    }
    
    @Test
    void checkKeptTickets() {
        
    }
    
    @Test
    void checkCanPlay() {
        
    }
    
    @Test
    void checkDrewTickets() {
        
    }
    
    @Test
    void checkDrewBlindCard() {
        
    }
    
    @Test
    void checkDrewVisibleCard() {
    
    }
    
    @Test
    void checkClaimedRoute() {
        
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
}
