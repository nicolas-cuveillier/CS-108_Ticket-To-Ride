package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.TreeSet;

/**
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 * <p>
 * simulate a ticket from the tchu game.
 */
public final class Ticket implements Comparable<Ticket> {

    private final List<Trip> trips;
    private final String name;

    /**
     * Primary constructor for a Ticket, creating it with a List of trips.
     *
     * @param trips the list of all trips that form the Ticket
     * @throws IllegalArgumentException if the list of trips is empty
     *                                  if all the departure's Station of each trips have not the same name
     */
    public Ticket(List<Trip> trips) {
        Preconditions.checkArgument(!trips.isEmpty());

        for (int i = 1; i < (trips.size() - 1); i++)
            Preconditions.checkArgument(trips.get(i - 1).from().name().equalsIgnoreCase(trips.get(i).from().name()));

        this.trips = List.copyOf(trips);
        this.name = computeText(this.trips);

    }

    /**
     * Secondary constructor for a Ticket with only one Trip.
     *
     * @param from   the Station of departure
     * @param to     the arrival Station
     * @param points the number of points for the tickets
     */
    public Ticket(Station from, Station to, int points) {
        this(List.of(new Trip(from, to, points)));
    }

    private static String computeText(List<Trip> trips) {
        final TreeSet<String> s = new TreeSet<>();
        final StringBuilder text = new StringBuilder();

        text.append(trips.get(0).from().name())
                .append(" - ");

        if (trips.size() == 1) {
            s.add(String.format("%s (%s)", trips.get(0).to().name(), trips.get(0).points()));
            text.append(String.join("", s));
        } else {
            trips.forEach(trip -> s.add(String.format("%s (%s)", trip.to().name(), trip.points())));
            text.append("{")
                    .append(String.join(", ", s))
                    .append("}");
        }
        return text.toString();
    }

    /**
     * Computes how many points will be earned with the Ticket according to its connectivity.
     *
     * @param connectivity will describe the fact that two station are connected and will influence on the total points
     * @return (int) the points given the connectivity
     */
    public int points(StationConnectivity connectivity) {
        int maxPoint = 0;
        int minPoint = trips.get(0).points();

        for (Trip t : trips) {
            minPoint = Math.min(minPoint, t.points());
            if(connectivity.connected(t.from(), t.to()))
                maxPoint = Math.max(maxPoint,t.points());
        }

        return (maxPoint == 0) ? -minPoint : maxPoint;
    }

    /**
     * Getter for the private field name.
     *
     * @return (string) the textual representation of a tickets
     */
    public String text() {
        return name;
    }

    /**
     * {@inheritDoc}
     *
     * @return the tickets' name
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * {@inheritDoc}
     * <p></p>
     * Compare Tickets according to their name.
     */
    @Override
    public int compareTo(Ticket that) {
        return this.name.compareTo(that.name);
    }
}
