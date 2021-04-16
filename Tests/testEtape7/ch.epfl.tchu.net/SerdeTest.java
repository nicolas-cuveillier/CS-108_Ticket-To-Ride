package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import static ch.epfl.tchu.game.Card.*;
import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public class SerdeTest {

    //TODO : check every other constant in Serdes
    @Test
    void checkSerdesOfInt() {
        int i = 2021;
        Assertions.assertEquals("2021", Serdes.INT.serialize(i));
        Assertions.assertEquals(i, Serdes.INT.deserialize("2021"));
    }

    @Test
    void checkSerdesOfString() {
        String s = "that should work";
        Assertions.assertEquals(Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8)), Serdes.STRING.serialize(s));
        Assertions.assertEquals(s, Serdes.STRING.deserialize(Serdes.STRING.serialize(s)));
    }

    @Test
    void checkPlayer_ID() {
        PlayerId p = PLAYER_1;

        Assertions.assertEquals("0", Serdes.PLAYER_ID.serialize(p));
        Assertions.assertEquals(p, Serdes.PLAYER_ID.deserialize("0"));

    }

    @Test
    void checkTurnKind() {
        Player.TurnKind turnKind = Player.TurnKind.CLAIM_ROUTE;
        Player.TurnKind turnKind1 = Player.TurnKind.DRAW_CARDS;

        Assertions.assertEquals("1",Serdes.TURN_KIND.serialize(turnKind1));
        Assertions.assertEquals("2",Serdes.TURN_KIND.serialize(turnKind));
        Assertions.assertEquals(turnKind,Serdes.TURN_KIND.deserialize("2"));
        Assertions.assertEquals(turnKind1,Serdes.TURN_KIND.deserialize("1"));
    }

    @Test
    void checkCard(){
        Card c = LOCOMOTIVE;
        Assertions.assertEquals("8",Serdes.CARD.serialize(c));
        Assertions.assertEquals(c,Serdes.CARD.deserialize("8"));
    }

    @Test
    void checkRoute(){
        Route r = ChMap.routes().get(6);

        Assertions.assertEquals("6",Serdes.ROUTE.serialize(r));
        Assertions.assertEquals(r,Serdes.ROUTE.deserialize("6"));

    }
    @Test
    void checkTickets(){
        Ticket t =ChMap.tickets().get(15);

        Assertions.assertEquals("15",Serdes.TICKET.serialize(t));
        Assertions.assertEquals(t,Serdes.TICKET.deserialize("15"));

    }

    @Test
    void checkLString(){
        List<String> s = List.of("it","should","work");
        StringJoiner j = new StringJoiner(",");
        j.add(Base64.getEncoder().encodeToString(s.get(0).getBytes(StandardCharsets.UTF_8)));
        j.add(Base64.getEncoder().encodeToString(s.get(1).getBytes(StandardCharsets.UTF_8)));
        j.add(Base64.getEncoder().encodeToString(s.get(2).getBytes(StandardCharsets.UTF_8)));

        Assertions.assertEquals(j.toString(),Serdes.L_STRING.serialize(s));
        Assertions.assertEquals(s,Serdes.L_STRING.deserialize(j.toString()));
    }

    @Test
    void checkSBCard(){
        SortedBag<Card> sc = SortedBag.of(1,BLACK,1,BLUE);

        Assertions.assertEquals("0,2",Serdes.SB_CARD.serialize(sc));
        Assertions.assertEquals(sc,Serdes.SB_CARD.deserialize("0,2"));
    }

    @Test
    void checkLSBCards(){
        List<SortedBag<Card>> lsb = List.of(SortedBag.of(1,BLACK,1,VIOLET),SortedBag.of(2,BLUE));

        Assertions.assertEquals("0,1;2,2",Serdes.L_SB_CARD.serialize(lsb));
        Assertions.assertEquals(lsb,Serdes.L_SB_CARD.deserialize("0,1;2,2"));
    }

    @Test
    void checkSerdesPublicCardState() {
        PublicCardState p = new PublicCardState(List.of(Card.BLACK, Card.VIOLET, Card.BLACK, Card.BLACK, Card.BLACK), 12, 11);
        Assertions.assertEquals("0,1,0,0,0;12;11", Serdes.SC_PUBLIC_CARD_STATE.serialize(p));
        Assertions.assertEquals(12, Serdes.SC_PUBLIC_CARD_STATE.deserialize("0,1,0,0,0;12;11").deckSize());
    }

    @Test
    void checkPPS(){
        PublicPlayerState publicPlayerState = new PublicPlayerState(2,3,List.of());

        Assertions.assertEquals("2;3;",Serdes.SC_PUBLIC_PLAYER_STATE.serialize(publicPlayerState));
        Assertions.assertEquals(publicPlayerState.routes(),Serdes.SC_PUBLIC_PLAYER_STATE.deserialize("2;3;").routes());
    }

    @Test
    void checkPS(){
        PlayerState p = new PlayerState(SortedBag.of(),SortedBag.of(),List.of());

        Assertions.assertEquals(";;",Serdes.SC_PLAYER_STATE.serialize(p));
        Assertions.assertEquals(p.cards(), Serdes.SC_PLAYER_STATE.deserialize(";;").cards());
    }


    @Test
    void checkSerdesPublicGameState() {
        List<Card> fu = List.of(RED, WHITE, BLUE, BLACK, RED);
        PublicCardState cs = new PublicCardState(fu, 30, 31);
        List<Route> rs1 = ChMap.routes().subList(0, 2);
        Map<PlayerId, PublicPlayerState> ps = Map.of(
                PLAYER_1, new PublicPlayerState(10, 11, rs1),
                PLAYER_2, new PublicPlayerState(20, 21, List.of()));
        PublicGameState gs =
                new PublicGameState(40, cs, PLAYER_2, ps, null);

        Assertions.assertEquals("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:", Serdes.SC_PUBLIC_GAME_STATE.serialize(gs));
        Assertions.assertEquals(null, Serdes.SC_PUBLIC_GAME_STATE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").lastPlayer());

    }


}
