package ch.epfl.tchu.net;

import ch.epfl.tchu.game.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public class Serdes {

    private Serdes() {
    }

    public static final Serde<Integer> INT_SERDES = Serde.of(i -> Integer.toString(i), Integer::parseInt);
    public static final Serde<String> STRING_SERDES = Serde.of(i -> Base64.getEncoder().encodeToString(i.getBytes(StandardCharsets.UTF_8)), i -> new String(Base64.getDecoder().decode(i), StandardCharsets.UTF_8));
    public static final Serde<PlayerId> PLAYER_ID_SERDES = Serde.oneOf(PlayerId.ALL);
    public static final Serde<Player.TurnKind> TURN_KIND_SERDES = Serde.oneOf(Player.TurnKind.ALL);
    public static final Serde<Card> CARD_SERDES = Serde.oneOf(Card.ALL);
    public static final Serde<Route> ROUTE_SERDES = Serde.oneOf(ChMap.routes());
    public static final Serde<Ticket> TICKET_SERDES = Serde.oneOf(ChMap.tickets());
}
