package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 * <p>
 * Simulate a trip from the tchu game
 */
public class Trip {
    private final Station from;
    private final Station to;
    private final int points;

    /**
     * Principal constructor of a Trip with the departure's station, arrival station and its value
     *
     * @param from   (Station)
     * @param to     (Station)
     * @param points (int)
     */
    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    /**
     * compute all possible trip, from one list of departure station to one list of arrival station
     *
     * @param from   (List<Station>)
     * @param to     (List<Station>)
     * @param points (int)
     * @return (List < Trip >) the list of all trips
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        Preconditions.checkArgument(!from.isEmpty() || !to.isEmpty() || points > 0);

        ArrayList<Trip> allTrip = new ArrayList<>();

        for (Station sFrom : from) {
            for (Station sTo : to) {
                allTrip.add(new Trip(sFrom, sTo, points));
            }
        }

        return allTrip;
    }

    /**
     * getter for the departure station
     *
     * @return (Station)
     */
    public Station from() {
        return from;
    }

    /**
     * getter for the arrival station
     *
     * @return (Station)
     */
    public Station to() {
        return to;
    }

    /**
     * getter for the number of points it worth
     *
     * @return (int)
     */
    public int points() {
        return points;
    }

    /**
     * getter for the number of points it worth according to its connectivity
     *
     * @param connectivity (StationConnectivity)
     * @return (int)
     */
    public int points(StationConnectivity connectivity) {
        return (connectivity.connected(from, to) ? points : -points);
    }
}
