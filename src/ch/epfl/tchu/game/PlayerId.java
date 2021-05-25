package ch.epfl.tchu.game;

import ch.epfl.tchu.gui.Launcher;

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
    //todo javadoc
    public static final List<PlayerId> CURRENT_PLAYERS = (Launcher.PLAYER_NUMBER == 2) ? List.of(PLAYER_1, PLAYER_2) : ALL;
    public static final int COUNT_FOR_CURRENT_PLAYERS = CURRENT_PLAYERS.size();

    /**
     * Return the other player's id, given a player's id.
     *
     * @return (PlayerId) the other PlayerId.
     */
    //todo change ALL for currentPlayer
    public PlayerId next() {
        return (this.ordinal() == (CURRENT_PLAYERS.size() - 1)) ? CURRENT_PLAYERS.get(0) : CURRENT_PLAYERS.get(this.ordinal() + 1);
    }
}
