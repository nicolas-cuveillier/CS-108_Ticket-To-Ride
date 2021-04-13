package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
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

    //Single Objects
    public static final Serde<Integer> INT = Serde.of(i -> Integer.toString(i), Integer::parseInt);
    public static final Serde<String> STRING = Serde.of(i -> Base64.getEncoder().encodeToString(i.getBytes(StandardCharsets.UTF_8)), i -> new String(Base64.getDecoder().decode(i), StandardCharsets.UTF_8));
    public static final Serde<PlayerId> PLAYER_ID = Serde.oneOf(PlayerId.ALL);
    public static final Serde<Player.TurnKind> TURN_KIND = Serde.oneOf(Player.TurnKind.ALL);
    public static final Serde<Card> CARD = Serde.oneOf(Card.ALL);
    public static final Serde<Route> ROUTE = Serde.oneOf(ChMap.routes());
    public static final Serde<Ticket> TICKET = Serde.oneOf(ChMap.tickets());
    
    //Collections
    public static final Serde<List<String>> L_STRING = Serde.listOf(STRING, ',');
    public static final Serde<List<Card>> L_CARD = Serde.listOf(CARD, ',');
    public static final Serde<List<Route>> L_ROUTE = Serde.listOf(ROUTE, ',');
    public static final Serde<SortedBag<Card>> SB_CARD = Serde.bagOf(CARD, ',');
    public static final Serde<SortedBag<Ticket>> SB_TICKET = Serde.bagOf(TICKET, ',');
    public static final Serde<List<SortedBag<Card>>> L_SB_CARD = Serde.listOf(SB_CARD, ';');
    
    //Serializable Classes
    public static final Serde<PublicCardState> SC_PUBLIC_CARD_STATE = Serde.of(publicCardStateStringFunctionSer(), publicCardStateStringFunctionDeSer());
    public static final Serde<PublicPlayerState> SC_PUBLIC_PLAYER_STATE = Serde.of(publicPlayerStateStringFunctionSer(), publicPlayerStateStringFunctionDeSer());
    //public static final Serde<PlayerState> SC_PLAYER_STATE = Serde.of();
    //public static final Serde<PublicGameState> SC_PUBLIC_GAME_STATE = Serde.of();

    private static Function<PublicPlayerState, String> publicPlayerStateStringFunctionSer() {
        return publicPlayerState -> {
            StringJoiner joiner = new StringJoiner(";");
            joiner.add(INT.serialize(publicPlayerState.ticketCount()));
            joiner.add(INT.serialize(publicPlayerState.cardCount()));
            joiner.add(L_ROUTE.serialize(publicPlayerState.routes()));
            return joiner.toString();
        };
    }

    private static Function<String, PublicPlayerState> publicPlayerStateStringFunctionDeSer() {
        return message -> {
            String[] t = message.split(Pattern.quote(";"), -1);
            return new PublicPlayerState(INT.deserialize(t[0]), INT.deserialize(t[1]), L_ROUTE.deserialize(t[3]));
        };
    }

    private static Function<PublicCardState, String> publicCardStateStringFunctionSer() {
        return publicCardState -> {
            StringJoiner joiner = new StringJoiner(";");
            joiner.add(L_CARD.serialize(publicCardState.faceUpCards()));
            joiner.add(INT.serialize(publicCardState.deckSize()));
            joiner.add(INT.serialize(publicCardState.discardsSize()));
            return joiner.toString();
        };
    }

    private static Function<String, PublicCardState> publicCardStateStringFunctionDeSer() {
        return message -> {
            String[] t = message.split(Pattern.quote(";"), -1);
            return new PublicCardState(L_CARD.deserialize(t[0]), INT.deserialize(t[1]), INT.deserialize(t[2]));
        };
    }
}
