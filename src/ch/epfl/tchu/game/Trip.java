package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**<h1>Trip</h1>
 * Implements a Trip of the game.
 * 
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public final class Trip {
    private final Station from;
    private final Station to;
    private final int points;

    /**
     * Primary constructor of a Trip with the departure's station, arrival station and its value.
     *
     * @param from   the departure Station
     * @param to     the arrival Station
     * @param points the points for this Trip
     * @throws IllegalArgumentException if points is negative or equal to zero
     */
    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);

        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    /**
     * Computes every possible Trip, from the lists of departure and arrival Stations.
     *
     * @param from   the departure Station
     * @param to     the arrival Station
     * @param points the points for the list of all possible Trip between each Station
     * @return (List < Trip >) the list of all trips
     * @throws IllegalArgumentException if from or to is empty or if points are negative
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        Preconditions.checkArgument(!from.isEmpty() || !to.isEmpty() || points > 0);

        final ArrayList<Trip> allTrip = new ArrayList<>();
        from.forEach(f -> to.forEach(t -> allTrip.add(new Trip(f, t, points))));

        return allTrip;
    }

    /**
     * Getter for the departure station.
     *
     * @return (Station) the departure Station
     */
    public Station from() {
        return from;
    }

    /**
     * Getter for the arrival station.
     *
     * @return (Station) the arrival Station
     */
    public Station to() {
        return to;
    }

    /**
     * Getter for the number of points.
     *
     * @return (int) the number of point assigned to the trip
     */
    public int points() {
        return points;
    }

    /**
     * Getter for the number of points according to the connectivity.
     *
     * @param connectivity represent the connectivity between the two Stations
     * @return (int) the points if the two Stations are connected and minus the points if not
     */
    public int points(StationConnectivity connectivity) {
        return (connectivity.connected(from, to) ? points : -points);
    }
}
