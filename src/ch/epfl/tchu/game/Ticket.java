package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

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

        this.trips = List.copyOf(trips);
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
        StringBuilder text = new StringBuilder();

        text.append(trips.get(0).from().name() + " - ");

        if (trips.size() == 1) {
            s.add(String.format("%s (%s)", trips.get(0).to().name(), trips.get(0).points()));
            text.append(String.join("", s));
        } else {
            text.append("{");
            for (Trip t : trips) {
                s.add(String.format("%s (%s)", t.to().name(), t.points()));
            }
            text.append(String.join(", ", s));
            text.append("}");
        }
        return text.toString();
    }

    /**
     * Compute how many points will be earned with this ticket according to its connectivity
     *
     * @param connectivity (StationConnectivity)
     * @return (int) the points given the connectivity
     */
    public int points(StationConnectivity connectivity) {
        int maxPoint = 0;
        int minPoint = trips.get(0).points();

        List<Trip> connectedTrips = trips.stream()
                .filter(t -> connectivity.connected(t.from(),t.to()))
                .collect(Collectors.toList());

        for (Trip t : trips) {
            if(t.points() < minPoint){
                minPoint = t.points();
            }
        }

        if(connectedTrips.isEmpty()){
            return -minPoint;
        }

        for (Trip t : connectedTrips) {
            if(t.points() > maxPoint){
                maxPoint = t.points();
            }
        }
        return maxPoint;
    }

    /**
     * @return (string) the textual representation of a tickets
     */
    public String text() {
        return name;
    }
    /**
     * {@inheritDoc}
     *
     * @return the name
     */
    @Override
    public String toString() {
        return name;
    }
    /**
     * {@inheritDoc}
     * compare the name
     */
    @Override
    public int compareTo(Ticket that) {
        return this.name.compareTo(that.name);
    }
}
