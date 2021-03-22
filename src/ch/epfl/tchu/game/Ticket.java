package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.TreeSet;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 * <p>
 * Simulate a ticket from the tchu game
 */
public final class Ticket implements Comparable<Ticket> {

    private final List<Trip> trips;
    private final String name;

    /**
     * Principal constructor for a Ticket, building it with a List of trips
     *
     * @param trips the list of all trips that form the Ticket
     * @throws IllegalArgumentException if the list of trips is empty
     *                                  if all the departure's Station of each trips have not the same name
     */
    public Ticket(List<Trip> trips) {

        Preconditions.checkArgument(!trips.isEmpty());

        for (int i = 1; i < (trips.size() - 1); i++) {
            Preconditions.checkArgument(trips.get(i - 1).from().name().equalsIgnoreCase(trips.get(i).from().name()));
        }

        this.trips = trips;
        this.name = computeText(this.trips);

    }

    /**
     * Second constructor, for building a Ticket with only one trip
     *
     * @param from   the Station of departure
     * @param to     the arrival Station
     * @param points the number of points for the tickets
     */
    public Ticket(Station from, Station to, int points) {
        this(List.of(new Trip(from, to, points)));
    }

    private static String computeText(List<Trip> trips) {
        TreeSet<String> s = new TreeSet<>();
        String text = "";

        text += trips.get(0).from().name() + " - ";

        if (trips.size() == 1) {
            s.add(String.format("%s (%s)", trips.get(0).to().name(), trips.get(0).points()));
            text += String.join("", s);
        } else {
            text += "{";
            for (Trip t : trips) {
                s.add(String.format("%s (%s)", t.to().name(), t.points()));
            }
            text += String.join(", ", s);
            text += "}";
        }
        return text;
    }

    /**
     * Compute how many points will be earned with this ticket according to its connectivity
     *
     * @param connectivity (StationConnectivity)
     * @return (int) the points given the connectivity
     */
    public int points(StationConnectivity connectivity) {
        int p = 0;
        int maxPoint = 0;
        boolean reached = false;

        if (trips.size() == 1) {
            p += trips.get(0).points(connectivity);
        } else {

            for (Trip t : trips) {
                if (connectivity.connected(t.from(), t.to())) {
                    if (t.points(connectivity) > maxPoint) {
                        maxPoint = t.points(connectivity);
                        reached = true;
                    }
                }
            }

            if (reached) {
                p += maxPoint;
            } else {
                p += -5;
            }
        }
        return p;
    }

    /**
     * @return (string) the textual representation of a tickets
     */
    public String text() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Ticket that) {
        return this.name.compareTo(that.name);
    }
}
