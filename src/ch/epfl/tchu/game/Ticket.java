package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.TreeSet;

public final class Ticket implements Comparable<Ticket> {

    private final List<Trip> trips;
    private final String name;

    public Ticket(List<Trip> trips1) {

        Preconditions.checkArgument(!trips1.isEmpty());

        for (int i = 1; i < (trips1.size() - 1); i++) {
            Preconditions.checkArgument(trips1.get(i - 1).from().name().equalsIgnoreCase(trips1.get(i).from().name()));
        }

        this.trips = trips1;
        this.name = computeText(this.trips);

    }

    public Ticket(Station from, Station to, int points) {
        this(List.of(new Trip(from, to, points)));
    }

    private static String computeText(List<Trip> trips) {
        TreeSet<String> s = new TreeSet<>();
        String text = "";

        text += trips.get(0).from().name() + " - ";

        if(trips.size() == 1 ) {
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

    public int points(StationConnectivity connectivity) {
        int p = 0;

        for (Trip t : trips){
            p += t.points(connectivity);
            System.out.println(p);
        }
        return p;
    }

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
