package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import java.util.List;

public final class Ticket implements Comparable<Ticket> {

    private final List<Trip> trips;
    private final String name;

    public Ticket(List<Trip> trips1){

        Preconditions.checkArgument(trips1.isEmpty());

        for (int i = 1; i < (trips1.size()-1); i++) {
            Preconditions.checkArgument(trips1.get(i-1).from().name().equalsIgnoreCase(trips1.get(i).from().name()));
        }

        this.trips = trips1;
        this.name = computeText();

    }

    public Ticket(Station from, Station to, int points){
        this(List.of(new Trip(from, to, points)));
    }

    private static String computeText(){
        
        return "";//to be completed
    }

    public int points(StationConnectivity connectivity){
        return 0;//to be completed
    }

    public String text(){
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
