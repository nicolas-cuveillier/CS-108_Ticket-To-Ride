package ch.epfl.tchu.game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public class PublicCardStateTest {
    
    private static final Card topDeckCard = (Card) echo("topDeckCard",Card.ALL.get(new Random().nextInt(Card.ALL.size())));
    
    private static Object echo(String label, Object o) {
        System.out.println(label + ": " + o.toString());
        return o;
    }
    
    @Test
    void checkException(){
       Assertions.assertThrows(IllegalArgumentException.class,
               ()->new PublicCardState(List.of(Card.BLUE),1,0, topDeckCard));
       Assertions.assertThrows(IllegalArgumentException.class,
               ()->new PublicCardState(List.of(Card.BLUE,Card.BLUE,Card.LOCOMOTIVE,Card.WHITE,Card.GREEN),5,-2, topDeckCard));
    }

    @Test
    void checkFaceUpCards(){
        PublicCardState p = new PublicCardState(List.of(Card.BLUE,Card.BLUE,Card.LOCOMOTIVE,Card.WHITE,Card.GREEN),5,2, topDeckCard);
        Assertions.assertEquals(List.of(Card.BLUE,Card.BLUE,Card.LOCOMOTIVE,Card.WHITE,Card.GREEN),p.faceUpCards());
    }
    @Test
    void checkFaceUpCard(){
        PublicCardState p = new PublicCardState(List.of(Card.BLUE,Card.BLUE,Card.LOCOMOTIVE,Card.WHITE,Card.GREEN),5,2, topDeckCard);
        Assertions.assertEquals(Card.LOCOMOTIVE,p.faceUpCard(2));
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                ()->p.faceUpCard(5));
    }
}
