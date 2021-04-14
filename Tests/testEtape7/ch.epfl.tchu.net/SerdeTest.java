package ch.epfl.tchu.net;

import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.Card.*;
import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public class SerdeTest {

    //TODO : check every other constant in Serdes
    @Test
    void checkSerdesOfInt(){
        int i = 2021;
        Assertions.assertEquals("2021",Serdes.INT.serialize(i));
        Assertions.assertEquals(i,Serdes.INT.deserialize("2021"));
    }
    @Test
    void checkSerdesOfString(){
        String s = "that should work";
        Assertions.assertEquals(Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8)),Serdes.STRING.serialize(s));
        Assertions.assertEquals(s,Serdes.STRING.deserialize(Serdes.STRING.serialize(s)));
    }

    @Test
    void checkSerdesPublicCardState(){
        PublicCardState p = new PublicCardState(List.of(Card.BLACK,Card.VIOLET,Card.BLACK,Card.BLACK,Card.BLACK),12,11);
        Assertions.assertEquals("0,1,0,0,0;12;11",Serdes.SC_PUBLIC_CARD_STATE.serialize(p));
        Assertions.assertEquals(12,Serdes.SC_PUBLIC_CARD_STATE.deserialize("0,1,0,0,0;12;11").deckSize());
    }
    @Test
    void checkSerdesPublicGameState(){
        List<Card> fu = List.of(RED, WHITE, BLUE, BLACK, RED);
        PublicCardState cs = new PublicCardState(fu, 30, 31);
        List<Route> rs1 = ChMap.routes().subList(0, 2);
        Map<PlayerId, PublicPlayerState> ps = Map.of(
                PLAYER_1, new PublicPlayerState(10, 11, rs1),
                PLAYER_2, new PublicPlayerState(20, 21, List.of()));
        PublicGameState gs =
                new PublicGameState(40, cs, PLAYER_2, ps, null);

        Assertions.assertEquals("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:",Serdes.SC_PUBLIC_GAME_STATE.serialize(gs));
        Assertions.assertEquals(null,Serdes.SC_PUBLIC_GAME_STATE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").lastPlayer());


    }


}
