package ch.epfl.tchu.game;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 * <p>
 * implement the notion of connecitvity between station
 */
public interface StationConnectivity {
    /**
     * Tests whether the stations are connected or not
     * 
     * @param st1 (Station) the first Station
     * @param st2 (Station) the second Station
     * @return (boolean) return true if the two station are connected with a player's Route or if it is the same station
     */
    boolean connected(Station st1, Station st2);
}
