package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public final class Trail {

    private final int length;
    private final List<Route> routes;
    private final Station station1;
    private final Station station2;

    //TODO : add st from,st to
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
            for (Route r : this.routes) {
                length1 += r.length();
            }
            this.length = length1;

        }

    }


    public static Trail longest(List<Route> routes) {
        List<Trail> cs = new ArrayList<>();
        Trail longestTrail = null;
        int length = 0;

        for (Route r : routes) {
            List<Route> r1 = List.of(r);
            cs.add(new Trail(r1, r.station1(), r.station2()));
            cs.add(new Trail(r1, r.station2(), r.station1()));
        }

        while (!cs.isEmpty()) {

            List<Trail> cs2 = new ArrayList<>();

            for (Trail c : cs) {
                for (Route r : routes) {
                    if (!c.routes().contains(r)) {

                        if (c.station2().equals(r.station1())) {
                            List<Route> routeList = new ArrayList<>(c.routes);
                            routeList.add(r);
                            Trail t = new Trail(routeList, c.station1, r.station2());
                            cs2.add(t);

                            if (length < t.length()) {
                                length = t.length();
                                longestTrail = t;
                            }
                        } else if (c.station2().equals(r.station2())) {
                            List<Route> routeList = new ArrayList<>(c.routes);
                            routeList.add(r);
                            Trail t = new Trail(routeList, c.station1, r.station1());
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
    }//to be completed

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

    public int length() {
        return length;
    }

    public List<Route> routes() {
        return routes;
    }

    public Station station1() {
        return station1;
    }

    public Station station2() {
        return station2;
    }


}
