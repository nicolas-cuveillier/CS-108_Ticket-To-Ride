package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public class GameStateTest {

    @Test
    void checkInitial(){
        GameState g = GameState.initial(SortedBag.of(),new Random());

        Assertions.assertTrue(g.playerState(g.currentPlayerId().next()).cards().size() == 4);
        Assertions.assertEquals(102,g.cardState().totalSize());
        Assertions.assertEquals(97,g.cardState().deckSize());
        Assertions.assertTrue(PlayerId.ALL.contains(g.currentPlayerId()));
    }

    @Test
    void checkTopTickets(){
        GameState g = GameState.initial(SortedBag.of(ChMap.tickets().subList(0,3)),new Random());

        Assertions.assertEquals(2,g.topTickets(2).size());
        Assertions.assertThrows(IllegalArgumentException.class, ()-> g.topTickets(-1));
        Assertions.assertThrows(IllegalArgumentException.class, ()-> g.topTickets(g.ticketsCount()+1));
        Assertions.assertTrue(g.topTickets(2).contains(ChMap.tickets().get(0)));//not always true
    }
    @Test
    void checkWithoutTopCard(){
        GameState g = GameState.initial(SortedBag.of(ChMap.tickets().subList(0,3)),new Random());
        Assertions.assertFalse(g.topCard().equals(g.withoutTopCard().topCard()));
    }
    @Test
    void checkWithMoreDiscardedCards(){
        GameState g = GameState.initial(SortedBag.of(ChMap.tickets().subList(0,3)),new Random());
        Assertions.assertTrue(3 == g.withMoreDiscardedCards(SortedBag.of(2,Card.BLACK,1,Card.LOCOMOTIVE)).cardState().discardsSize());
    }
    @Test
    void checkWithCardsDeckRecreatedIfNeeded(){
        GameState g = GameState.initial(SortedBag.of(ChMap.tickets().subList(0,3)),new Random());
        Assertions.assertEquals(g,g.withCardsDeckRecreatedIfNeeded(new Random()));
        GameState g2 = GameState.initial(SortedBag.of(ChMap.tickets().subList(0,10)),new Random());

        while(!g2.cardState().isDeckEmpty()){
           g2 =  g2.withoutTopCard();
        }
        GameState g3 = g2.withMoreDiscardedCards(SortedBag.of());
        g2 = g2.withMoreDiscardedCards(SortedBag.of(2,Card.BLACK,1,Card.LOCOMOTIVE));
        Assertions.assertEquals(3,g2.withCardsDeckRecreatedIfNeeded(new Random()).cardState().deckSize());
        Assertions.assertEquals(0,g3.withCardsDeckRecreatedIfNeeded(new Random()).cardState().deckSize());
    }

    @Test
    void checkWithInitiallyChosenTickets(){
        GameState g = GameState.initial(SortedBag.of(ChMap.tickets().subList(0,3)),new Random());
        Assertions.assertEquals(SortedBag.of(ChMap.tickets().subList(3,20)),
                g.withInitiallyChosenTickets(PlayerId.PLAYER_1,SortedBag.of(ChMap.tickets().subList(3,20))).playerState(PlayerId.PLAYER_1).tickets());
        GameState g2 = g.withInitiallyChosenTickets(PlayerId.PLAYER_1,SortedBag.of(ChMap.tickets().subList(3,20)));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> g2.withInitiallyChosenTickets(PlayerId.PLAYER_1,SortedBag.of(ChMap.tickets().subList(3,20))));
    }

    @Test
    void checkWithChosenAdditionalTickets(){
        //TODO :
    }

    @Test
    void checkWithDrawnFaceUpCard(){
        GameState g = GameState.initial(SortedBag.of(ChMap.tickets().subList(0,3)),new Random());

        Assertions.assertEquals(g.topCard(),g.withDrawnFaceUpCard(3).cardState().faceUpCard(3));
        Assertions.assertFalse(g.topCard().equals(g.withDrawnFaceUpCard(3).topCard()));
        Assertions.assertTrue(g.withDrawnFaceUpCard(3).currentPlayerState().cards().contains(g.cardState().faceUpCard(3)));
    }
    @Test
    void checkWithBlindlyDrawnCard(){
        GameState g = GameState.initial(SortedBag.of(ChMap.tickets().subList(0,3)),new Random());
        Assertions.assertTrue(g.withBlindlyDrawnCard().currentPlayerState().cards().contains(g.topCard()));
        Assertions.assertFalse(g.topCard().equals(g.withBlindlyDrawnCard().topCard()));
    }

    @Test
    void checkWithClaimedRoute(){
        GameState g = GameState.initial(SortedBag.of(ChMap.tickets().subList(0,3)),new Random());
        //TODO : 

    }

}
