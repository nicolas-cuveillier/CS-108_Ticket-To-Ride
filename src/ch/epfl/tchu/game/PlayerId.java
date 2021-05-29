package ch.epfl.tchu.game;

import java.util.List;

/**
 * <h1>PlayerId</h1>
 * Player's IDs used in the game.
 * {@link #PLAYER_1},
 * {@link #PLAYER_2}.
 *
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public enum PlayerId {
    PLAYER_1,
    PLAYER_2,
    PLAYER_3,
    PLAYER_4,
    PLAYER_5;

    /**
     * An unmodifiable list of all players.
     */
    public static final List<PlayerId> ALL = List.of(PlayerId.values());

    /**
     * Number of possible players.
     */
    public static final int COUNT = ALL.size();

    /**
     * Number of player in the game
     */
    public static int NUMBER_OF_PLAYER;

    /**
     * Compute the list of the player that are actually playing the game.
     * @return a view of the {@link #ALL} list according to the actual number of human players.
     */
    public static List<PlayerId> CURRENT_PLAYERS() {
        return ALL.subList(0, NUMBER_OF_PLAYER);
    }

    /**
     * Return the other player's id, given a player's id.
     *
     * @return (PlayerId) the other PlayerId.
     */
    public PlayerId next() {
        return (this.ordinal() == (CURRENT_PLAYERS().size() - 1)) ? CURRENT_PLAYERS().get(0) : CURRENT_PLAYERS().get(this.ordinal() + 1);
    }

}
