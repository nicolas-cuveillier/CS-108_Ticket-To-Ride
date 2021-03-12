package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public enum PlayerId {
    PLAYER_1,
    PLAYER_2;

    public static final List<PlayerId> ALL = List.of(PlayerId.values());

    public static final int COUNT = ALL.size();

    public PlayerId next(){
        return (this.equals(PLAYER_1) ? PLAYER_2 : PLAYER_1);
    }
}
