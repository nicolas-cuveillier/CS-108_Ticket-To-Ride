package ch.epfl.tchu.game;

/**<h1>StationConnectivity</h1>
 * Interfaces the connection between stations of the game.
 * 
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public interface StationConnectivity {
    /**
     * Tests whether the stations are connected or not
     *
     * @param st1 the first Station
     * @param st2 the second Station
     * @return (boolean) return true if the two station are connected with a player's Route or if it is the same station
     */
    boolean connected(Station st1, Station st2);
}
