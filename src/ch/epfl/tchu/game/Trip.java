package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Trip {
    private final Station from;
    private final Station to;
    private final int points;


    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    public static List<Trip> all(List<Station> from, List<Station> to, int points){
        Preconditions.checkArgument(!from.isEmpty() || !to.isEmpty() || points > 0);

        ArrayList<Trip> allTrip = new ArrayList<>();

        for (Station sFrom : from) {
            for (Station sTo : to) {
                allTrip.add(new Trip(sFrom,sTo,points));
            }
        }

        return allTrip;
    }

    //la classe doit etre immuable donc il faut retourner des copies des Stations ?
    public Station getFrom() {
        return from;
    }

    public Station getTo() {
        return to;
    }

    public int getPoints() {
        return points;
    }

    public int points(StationConnectivity connectivity){
        return (connectivity.connected(from,to) ? points : -points);
    }
}
