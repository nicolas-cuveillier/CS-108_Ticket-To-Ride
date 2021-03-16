package ch.epfl.tchu.game;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 * <p>
 * implement the notion of connecitvity between station
 */
public interface StationConnectivity {
    /**
     * @param st1 (Station)
     * @param st2 (Station)
     * @return (boolean) return true iff the two station are connected with a player's Route
     */
    boolean connected(Station st1, Station st2);
}
