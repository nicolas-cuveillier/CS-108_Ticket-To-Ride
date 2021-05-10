package ch.epfl.tchu.net;

import java.util.List;

/**
 * Message that can be sent to the server :
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
 * 
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
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
     * An unmodifiable list of all messages
     */
    public static final List<MessageId> ALL = List.of(MessageId.values());

    /**
     * The number of possible messages
     */
    public static final int COUNT = ALL.size();

}
