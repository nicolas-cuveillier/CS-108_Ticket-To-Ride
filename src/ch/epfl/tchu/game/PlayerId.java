package ch.epfl.tchu.game;

import java.util.List;

/**<h1>PlayerId</h1>
 * Player's IDs used in the game.
 * {@link #PLAYER_1},
 * {@link #PLAYER_2}.
 * 
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public enum PlayerId {
    PLAYER_1,
    PLAYER_2;
    /**
     * An unmodifiable list of all players.
     */
    public static final List<PlayerId> ALL = List.of(PlayerId.values());
    /**
     * Number of possible players.
     */
    public static final int COUNT = ALL.size();

    /**
     * Return the other player's id, given a player's id.
     *
     * @return (PlayerId) the other PlayerId.
     */
    public PlayerId next() {
        return (this.ordinal() == (ALL.size() - 1)) ? ALL.get(0) : ALL.get(this.ordinal() + 1);
    }
}
