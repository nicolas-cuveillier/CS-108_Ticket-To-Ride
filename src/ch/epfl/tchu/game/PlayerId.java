package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 * <p>
 * Different Player in the game
 */
public enum PlayerId {
    PLAYER_1,
    PLAYER_2;
    /**
     * List of all players
     */
    public static final List<PlayerId> ALL = List.of(PlayerId.values());
    /**
     * number of possible players
     */
    public static final int COUNT = ALL.size();

    /**
     * return the other player given one player
     *
     * @return (PlayerId)
     */
    public PlayerId next() {
        return (this.equals(PLAYER_1) ? PLAYER_2 : PLAYER_1);
    }
}
