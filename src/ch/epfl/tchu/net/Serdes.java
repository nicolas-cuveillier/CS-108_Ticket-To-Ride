package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Pattern;

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
    public static final Serde<List<String>> LIST_STRING_SERDES = Serde.listOf(STRING_SERDES, ',');
    public static final Serde<List<Card>> LIST_CARD_SERDES = Serde.listOf(CARD_SERDES, ',');
    public static final Serde<List<Route>> LIST_ROUTE_SERDES = Serde.listOf(ROUTE_SERDES, ',');
    public static final Serde<SortedBag<Card>> SORTEDBAG_CARD_SERDES = Serde.bagOf(CARD_SERDES, ',');
    public static final Serde<SortedBag<Ticket>> SORTEDBAG_TICKET_SERDES = Serde.bagOf(TICKET_SERDES, ',');
    public static final Serde<List<SortedBag<Card>>> LIST_SORTEDBAG_CARD_SERDES = Serde.listOf(SORTEDBAG_CARD_SERDES, ';');
    public static final Serde<PublicCardState> PUBLIC_CARD_STATE_SERDES = Serde.of(publicCardStateStringFunctionSer(), publicCardStateStringFunctionDeSer());
    public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDES = Serde.of(publicPlayerStateStringFunctionSer(), publicPlayerStateStringFunctionDeSer());

    private static Function<PublicPlayerState, String> publicPlayerStateStringFunctionSer() {
        return publicPlayerState -> {
            StringJoiner joiner = new StringJoiner(";");
            joiner.add(INT_SERDES.serialize(publicPlayerState.ticketCount()));
            joiner.add(INT_SERDES.serialize(publicPlayerState.cardCount()));
            joiner.add(LIST_ROUTE_SERDES.serialize(publicPlayerState.routes()));
            return joiner.toString();
        };
    }

    private static Function<String, PublicPlayerState> publicPlayerStateStringFunctionDeSer() {
        return message -> {
            String[] t = message.split(Pattern.quote(";"), -1);
            return new PublicPlayerState(INT_SERDES.deserialize(t[0]), INT_SERDES.deserialize(t[1]), LIST_ROUTE_SERDES.deserialize(t[3]));
        };
    }

    private static Function<PublicCardState, String> publicCardStateStringFunctionSer() {
        return publicCardState -> {
            StringJoiner joiner = new StringJoiner(";");
            joiner.add(LIST_CARD_SERDES.serialize(publicCardState.faceUpCards()));
            joiner.add(INT_SERDES.serialize(publicCardState.deckSize()));
            joiner.add(INT_SERDES.serialize(publicCardState.discardsSize()));
            return joiner.toString();
        };
    }

    private static Function<String, PublicCardState> publicCardStateStringFunctionDeSer() {
        return message -> {
            String[] t = message.split(Pattern.quote(";"), -1);
            return new PublicCardState(LIST_CARD_SERDES.deserialize(t[0]), INT_SERDES.deserialize(t[1]), INT_SERDES.deserialize(t[2]));
        };
    }


}
