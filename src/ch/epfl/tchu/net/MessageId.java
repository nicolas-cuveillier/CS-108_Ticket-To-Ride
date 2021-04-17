package ch.epfl.tchu.net;

import java.util.List;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 *
 * message that can be sent to the server :
 * {@link #INIT_PLAYERS},
 * {@link #RECEIVE_INFO},
 * {@link #UPDATE_STATE},
 * {@link #SET_INITIAL_TICKETS},
 * {@link #CHOOSE_INITIAL_TICKETS},
 * {@link #NEXT_TURN},
 * {@link #CHOOSE_TICKETS},
 * {@link #ROUTE},
 * {@link #CARDS},
 * {@link #CHOOSE_ADDITIONAL_CARDS}.
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

    /**
     * an unmodifiable list of all messages
     */
    public static final List<MessageId> ALL = List.of(MessageId.values());

    /**
     * the number of possible messages
     */
    public static final int COUNT = ALL.size();

}
