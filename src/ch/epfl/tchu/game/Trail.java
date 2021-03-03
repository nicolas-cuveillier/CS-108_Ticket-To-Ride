package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Grégory Preisig & Nicolas Cuveillie
 * <p>
 * implement the notion of Trail in the game
 */
public final class Trail {

    private final int length;
    private final List<Route> routes;
    private final Station station1;
    private final Station station2;


    private Trail(List<Route> routes, Station station1, Station station2) {
        this.routes = routes;

        if (routes.size() == 0) {
            this.station1 = null;
            this.station2 = null;
            this.length = 0;
        } else {
            this.station1 = station1;
            this.station2 = station2;
            int length1 = 0;
            for (Route r : routes) {
                length1 += r.length();
            }
            this.length = length1;

        }

    }

    /**
     * compute the longest or one of the longest Trail for a given List of Route
     *
     * @param routes (List<Route>)
     * @return (Trail)
     */
    public static Trail longest(List<Route> routes) {
        //private vars
        List<Trail> cs = new ArrayList<>();
        Trail longestTrail = null;
        int length = 0;
        
        //filling the trail list cs with all the possible single route trails
        for (Route r : routes) {
            Trail t1 = new Trail(List.of(r), r.station1(), r.station2());

            cs.add(t1);
            Trail t2 = new Trail(List.of(r), r.station2(), r.station1());
           cs.add(t2);
        }

        while (!cs.isEmpty()) {
            List<Trail> cs2 = new ArrayList<>();

            for (Trail c : cs) {
                for (Route r : routes) {
                    if (!c.routes().contains(r)) {
                        if ((c.station2().id() == r.station1().id()) && (c.station2().name() == r.station1().name())) {
                            List<Route> routeList = new ArrayList<>(c.routes());
                            routeList.add(r);
                            Trail t = new Trail(routeList, c.station1(), r.station2());
                            cs2.add(t);

                            if (length < t.length()) {
                                length = t.length();
                                longestTrail = t;
                            }
                        } else if ((c.station2().id() == r.station2().id()) && (c.station2().name() == r.station2().name())) {
                            List<Route> routeList = new ArrayList<>(c.routes());
                            routeList.add(r);
                            Trail t = new Trail(routeList, c.station1(), r.station1());
                            cs2.add(t);

                            if (length < t.length()) {
                                length = t.length();
                                longestTrail = t;
                            }
                        }
                    }
                }
            }
            cs = cs2;
        }
        return longestTrail;
    }

    @Override
    public String toString() {
        String text = "";
        int totalLength = 0;

        if (routes.size() != 0) {
            text += routes.get(0).station1().name();
            totalLength += routes.get(0).length();

            for (Route r : routes.subList(1, routes.size())) {
                String name = r.station1().name();
                text += " - " + name;
                totalLength += r.length();
            }
            
            text += " - " + routes.get(routes.size() - 1).station2().name();
            text += " (" + totalLength + ")";
        } else {
            text = "Empty Trail";
        }

        return text;
    }

    /**
     * getter for Trail's length
     *
     * @return (int)
     */
    public int length() {
        return length;
    }

    /**
     * getter for Trail's routes
     *
     * @return (List < Route >)
     */
    public List<Route> routes() {
        return routes;
    }

    /**
     * getter for the Trail's first Station
     *
     * @return (Station)
     */
    public Station station1() {
        return station1;
    }

    /**
     * getter for the Trail's last Station
     *
     * @return (Station)
     */
    public Station station2() {
        return station2;
    }


}
