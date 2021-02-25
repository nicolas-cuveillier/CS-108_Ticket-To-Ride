package ch.epfl.tchu.game;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 * <p>
 * implement the notion of connecitvity between station
 */
public interface StationConnectivity {
    boolean connected(Station st1,Station st2);
}
