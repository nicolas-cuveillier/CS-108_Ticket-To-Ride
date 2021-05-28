package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Pattern;


/**
 * <h1>Serdes</h1>
 * None instantiable class that defines every constant related to serialization and deserialization processes for the
 * elements of the game.
 *
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public final class Serdes {
    private final static char SEPARATOR_COMMA = ',';
    private final static char SEPARATOR_SEMI_COLON = ';';
    private final static char SEPARATOR_DOUBLE_POINTS = ':';

    private Serdes() {
    }

    //Single Objects
    /**
     * A Serde able to (de)serialize an Integer.
     */
    public static final Serde<Integer> INT = Serde.of(i -> Integer.toString(i), Integer::parseInt);
    /**
     * A Serde able to (de)serialize a String.
     */
    public static final Serde<String> STRING = Serde.of(i -> Base64.getEncoder().encodeToString(i.getBytes(StandardCharsets.UTF_8)), i -> new String(Base64.getDecoder().decode(i.getBytes(StandardCharsets.UTF_8))));
    /**
     * A Serde able to (de)serialize one element of the PlayerId enum.
     */
    public static final Serde<PlayerId> PLAYER_ID = Serde.oneOf(PlayerId.ALL);
    /**
     * A Serde able to (de)serialize one element of the TurnKind enum.
     */
    public static final Serde<Player.TurnKind> TURN_KIND = Serde.oneOf(Player.TurnKind.ALL);
    /**
     * A Serde able to (de)serialize one element of the Card enum.
     */
    public static final Serde<Card> CARD = Serde.oneOf(Card.ALL);
    /**
     * A Serde able to (de)serialize one element of the Routes that compose the Game.
     */
    public static final Serde<Route> ROUTE = Serde.oneOf(ChMap.routes());
    /**
     * A Serde able to (de)serialize one element of the Tickets that compose the Game.
     */
    public static final Serde<Ticket> TICKET = Serde.oneOf(ChMap.tickets());

    //Collections
    /**
     * A Serde able to (de)serialize a whole list of Strings.
     */
    public static final Serde<List<String>> L_STRING = Serde.listOf(STRING, SEPARATOR_COMMA);
    /**
     * A Serde able to (de)serialize a whole list of Cards.
     */
    public static final Serde<List<Card>> L_CARD = Serde.listOf(CARD, SEPARATOR_COMMA);
    /**
     * A Serde able to (de)serialize a whole list of Routes.
     */
    public static final Serde<List<Route>> L_ROUTE = Serde.listOf(ROUTE, SEPARATOR_COMMA);
    /**
     * A Serde able to (de)serialize a whole SortedBag of Cards.
     */
    public static final Serde<SortedBag<Card>> SB_CARD = Serde.bagOf(CARD, SEPARATOR_COMMA);
    /**
     * A Serde able to (de)serialize a whole SortedBag of Tickets.
     */
    public static final Serde<SortedBag<Ticket>> SB_TICKET = Serde.bagOf(TICKET, SEPARATOR_COMMA);
    /**
     * A Serde able to (de)serialize a whole list of SortedBag of Cards.
     */
    public static final Serde<List<SortedBag<Card>>> L_SB_CARD = Serde.listOf(SB_CARD, SEPARATOR_SEMI_COLON);

    //Serializable Classes
    /**
     * A Serde able to (de)serialize a Public Card State.
     */
    public static final Serde<PublicCardState> SC_PUBLIC_CARD_STATE = Serde.of(pcsSerFunction(), pcsDeSerFunction());
    /**
     * A Serde able to (de)serialize a Public Player State.
     */
    public static final Serde<PublicPlayerState> SC_PUBLIC_PLAYER_STATE = Serde.of(ppsSerFunction(), ppsDeSerFunction());
    /**
     * A Serde able to (de)serialize a Player State.
     */
    public static final Serde<PlayerState> SC_PLAYER_STATE = Serde.of(psSerFunction(), psDeSerFunction());
    /**
     * A Serde able to (de)serialize a Public Game State.
     */
    public static final Serde<PublicGameState> SC_PUBLIC_GAME_STATE = Serde.of(pgsSerFunction(), pgsDeSerFunction());

    //private methods
    /**
     * @return the function able to serialize a PublicGameState
     */
    private static Function<PublicGameState, String> pgsSerFunction() {
        return publicGameState -> {
            StringJoiner joiner = new StringJoiner(Character.toString(SEPARATOR_DOUBLE_POINTS));
            joiner.add(INT.serialize(publicGameState.ticketsCount()))
                    .add(SC_PUBLIC_CARD_STATE.serialize(publicGameState.cardState()))
                    .add(PLAYER_ID.serialize(publicGameState.currentPlayerId()))
                    .add(SC_PUBLIC_PLAYER_STATE.serialize(publicGameState.playerState(PlayerId.PLAYER_1)))
                    .add(SC_PUBLIC_PLAYER_STATE.serialize(publicGameState.playerState(PlayerId.PLAYER_2)))
                    .add((publicGameState.lastPlayer() == null) ? "" : PLAYER_ID.serialize(publicGameState.lastPlayer()));
            return joiner.toString();
        };
    }

    /**
     * @return the function able to deserialize a PublicGameState
     */
    private static Function<String, PublicGameState> pgsDeSerFunction() {
        return message -> {
            String[] t = message.split(Pattern.quote(Character.toString(SEPARATOR_DOUBLE_POINTS)), -1);
            return new PublicGameState(INT.deserialize(t[0]), SC_PUBLIC_CARD_STATE.deserialize(t[1]), PLAYER_ID.deserialize(t[2]),
                    Map.of(PlayerId.PLAYER_1, SC_PUBLIC_PLAYER_STATE.deserialize(t[3]), PlayerId.PLAYER_2,
                            SC_PUBLIC_PLAYER_STATE.deserialize(t[4])), (t[5].equals("")) ? null : PLAYER_ID.deserialize(t[5]));
        };

    }

    /**
     * @return the function able to serialize a PlayerState
     */
    private static Function<PlayerState, String> psSerFunction() {
        return playerState -> {
            StringJoiner joiner = new StringJoiner(Character.toString(SEPARATOR_SEMI_COLON));
            joiner.add(SB_TICKET.serialize(playerState.tickets()))
                    .add(SB_CARD.serialize(playerState.cards()))
                    .add(L_ROUTE.serialize(playerState.routes()));
            return joiner.toString();
        };
    }

    /**
     * @return the function able to deserialize a PlayerState
     */
    private static Function<String, PlayerState> psDeSerFunction() {
        return message -> {
            String[] t = message.split(Pattern.quote(Character.toString(SEPARATOR_SEMI_COLON)), -1);
            return new PlayerState((t[0].equals("")) ? SortedBag.of() : SB_TICKET.deserialize(t[0]), (t[1].equals("")) ? SortedBag.of() : SB_CARD.deserialize(t[1]), (t[2].equals("")) ? List.of() : L_ROUTE.deserialize(t[2]));
        };
    }

    /**
     * @return the function able to serialize a PublicPlayerState
     */
    private static Function<PublicPlayerState, String> ppsSerFunction() {
        return publicPlayerState -> {
            StringJoiner joiner = new StringJoiner(Character.toString(SEPARATOR_SEMI_COLON));
            joiner.add(INT.serialize(publicPlayerState.ticketCount()))
                    .add(INT.serialize(publicPlayerState.cardCount()))
                    .add((publicPlayerState.routes() == null) ? "" : L_ROUTE.serialize(publicPlayerState.routes()));
            return joiner.toString();
        };
    }

    /**
     * @return the function able to deserialize a PublicPlayerState
     */
    private static Function<String, PublicPlayerState> ppsDeSerFunction() {
        return message -> {
            String[] t = message.split(Pattern.quote(Character.toString(SEPARATOR_SEMI_COLON)), -1);
            return new PublicPlayerState(INT.deserialize(t[0]), INT.deserialize(t[1]), (t[2].equals("")) ? List.of() : L_ROUTE.deserialize(t[2]));
        };
    }

    /**
     * @return the function able to deserialize a PublicCardState
     */
    private static Function<PublicCardState, String> pcsSerFunction() {
        return publicCardState -> {
            StringJoiner joiner = new StringJoiner(Character.toString(SEPARATOR_SEMI_COLON));
            joiner.add(L_CARD.serialize(publicCardState.faceUpCards()))
                    .add(INT.serialize(publicCardState.deckSize()))
                    .add(INT.serialize(publicCardState.discardsSize()));
            return joiner.toString();
        };
    }

    /**
     * @return the function able to deserialize a PublicCardState
     */
    private static Function<String, PublicCardState> pcsDeSerFunction() {
        return message -> {
            String[] t = message.split(Pattern.quote(Character.toString(SEPARATOR_SEMI_COLON)), -1);
            return new PublicCardState(L_CARD.deserialize(t[0]), INT.deserialize(t[1]), INT.deserialize(t[2]));
        };
    }
}
