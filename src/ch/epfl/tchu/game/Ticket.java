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
     * @param trips1
     */
    public Ticket(List<Trip> trips1) {

        Preconditions.checkArgument(!trips1.isEmpty());

        for (int i = 1; i < (trips1.size() - 1); i++) {
            Preconditions.checkArgument(trips1.get(i - 1).from().name().equalsIgnoreCase(trips1.get(i).from().name()));
        }

        this.trips = trips1;
        this.name = computeText(this.trips);

    }

    /**
     * Second constructor, for building a Ticket with only one trip
     *
     * @param from   (Station)
     * @param to     (Station)
     * @param points
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
     * @return (int) points
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
