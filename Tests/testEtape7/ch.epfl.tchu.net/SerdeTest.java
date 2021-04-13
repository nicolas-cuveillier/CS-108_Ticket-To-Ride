package ch.epfl.tchu.net;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.PublicCardState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public class SerdeTest {
    //TODO : check every other constant in Serdes
    @Test
    void checkSerdeOfInt(){
        int i = 2021;
        Assertions.assertEquals("2021",Serdes.INT.serialize(i));
        Assertions.assertEquals(2021,Serdes.INT.deserialize("2021"));
    }
    @Test
    void checkSerdePublicCardState(){
        PublicCardState p = new PublicCardState(List.of(Card.BLACK,Card.VIOLET,Card.BLACK,Card.BLACK,Card.BLACK),12,11);
        Assertions.assertEquals("0,1,0,0,0;12;11",Serdes.SC_PUBLIC_CARD_STATE.serialize(p));
        Assertions.assertEquals(12,Serdes.SC_PUBLIC_CARD_STATE.deserialize("0,1,0,0,0;12;11").deckSize());
    }


}
