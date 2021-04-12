package ch.epfl.tchu.net;

import java.util.List;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public enum MessageId {
    INIT_PLAYERS,
    RECEIVE_INFO,
    UPDATE_STATE,
    SET_INITIAL_TICKETS,
    CHOOSE_INITIAL_TICKETS,
    NEXT_TURN,
    CHOOSE_TICKETS,
    DRAW_SLOT,
    ROUTE,
    CARDS,
    CHOOSE_ADDITIONAL_CARDS;

            //utile ?
    /**
     * return an unmodifiable list of all messages
     */
    public static final List<MessageId> ALL = List.of(MessageId.values());

    /**
     * return the number of possible messages
     */
    public static final int COUNT = ALL.size();

}
