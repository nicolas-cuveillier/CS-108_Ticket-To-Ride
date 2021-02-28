package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public final class Trail {

    private final int length;
    private final List<Route> routes;
    private final Station station1;
    private final Station station2;

    private Trail(List<Route> routes) {
        this.routes = routes;

        if (routes.size() == 0) {
            this.station1 = null;
            this.station2 = null;
            this.length = 0;
        } else {
            this.station1 = routes.get(0).station1();
            this.station2 = routes.get(routes.size() - 1).station2();
            int length1 = 0;
            for (Route r : this.routes) {
                length1 += r.length();
            }
            this.length = length1;

        }

    }

    public static Trail longest(List<Route> routes) {
        return null;
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