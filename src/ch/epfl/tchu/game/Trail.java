package ch.epfl.tchu.game;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        this.station1 = station1;
        this.station2 = station2;
        this.length = routes.stream()
                    .mapToInt(i -> i.length())
                    .sum();
    }

    /**
     * Computes the longest or one of the longest Trail for a given List of Route
     *
     * @param routes the list of all Route(s) to make the Trail
     * @return (Trail) the longest trail given all Route(s)
     */
//TODO : clean/factor the method
    public static Trail longest(List<Route> routes) {
        if (routes.isEmpty()) {
            return new Trail(List.of(), null, null);
        }
        List<Trail> cs;
        Trail longestTrail = null;
        int length = 0;

        cs = getSingleRoutes(routes);

        while (!cs.isEmpty()) {
            List<Trail> cs2 = new ArrayList<>();

            for (Trail c : cs) {

                for (Route r : routes) {
                    if (!c.routes().contains(r)) {
                        if ((c.station2().id() == r.station1().id()) && (Objects.equals(c.station2().name(), r.station1().name()))) {
                            List<Route> routeList = new ArrayList<>(c.routes());
                            routeList.add(r);
                            Trail t = new Trail(routeList, c.station1(), r.station2());
                            cs2.add(t);

                            compare(length,c,longestTrail);

                        } else if ((c.station2().id() == r.station2().id()) && (Objects.equals(c.station2().name(), r.station2().name()))) {
                            List<Route> routeList = new ArrayList<>(c.routes());
                            routeList.add(r);
                            Trail t = new Trail(routeList, c.station1(), r.station1());
                            cs2.add(t);

                            compare(length,c,longestTrail);

                        } else {
                            if (length < c.length()) {
                                length = c.length();
                                longestTrail = c;
                            }
                        }
                    } else if (c.routes().containsAll(routes)) {
                        if (length < c.length()) {
                            length = c.length();
                            longestTrail = c;
                        }
                    }
                }
            }
            cs = cs2;
        }
        return longestTrail;
    }

    private static void compare(int length1, Trail c, Trail longestTrail){
        if (length1 < c.length()) {
            length1 = c.length();
            longestTrail = c;
        }
    }


    private static List<Trail> getSingleRoutes(List<Route> routes) {
        List<Trail> trails = new ArrayList<>();
        for (Route r : routes) {
            Trail t1 = new Trail(List.of(r), r.station1(), r.station2());

            trails.add(t1);
            Trail t2 = new Trail(List.of(r), r.station2(), r.station1());
            trails.add(t2);
        }

        return trails;
    }

    /**
     * {@inheritDoc}
     *
     * @return textual representation of a trail
     */
    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        int totalLength = 0;

        if (routes.size() != 0) {
            if (this.station1() == routes.get(0).station1()) {
                totalLength = routes.stream()
                        .mapToInt(i -> i.length())
                        .sum();

                for (int i = 0; i < routes.size(); i++) {
                    text.append(" - ")
                            .append(routes.get(i).station1().name());
                    if (i == 0) text = new StringBuilder(routes.get(i).station1().name());
                }

                text.append(" - ").append(routes.get(routes.size() - 1).station2().name());
            } else if (this.station1() == routes.get(routes.size() - 1).station2()) {
                totalLength = routes.stream()
                        .mapToInt(i -> i.length())
                        .sum();
                for (int i = routes.size() - 1; i >= 0; i--) {
                    text.append(" - ")
                            .append(routes.get(i).station2().name());

                    if (i == routes.size() - 1) text = new StringBuilder(routes.get(i).station2().name());
                }

                text.append(" - ")
                        .append(routes.get(0).station1().name());
            }
            text.append(" (")
                    .append(totalLength)
                    .append(")");
        } else {
            text = new StringBuilder("Empty Trail");
        }

        return text.toString();
    }

    /**
     * Getter for the private field length
     *
     * @return (int) the length
     */
    public int length() {
        return length;
    }

    /**
     * Getter for the private field routes
     *
     * @return (List < Route >) the list of all Route(s)
     */
    public List<Route> routes() {
        return Collections.unmodifiableList(routes);
    }

    /**
     * Getter for the first Station
     *
     * @return (Station) the first Station
     */
    public Station station1() {
        return station1;
    }

    /**
     * Getter for the last Station
     *
     * @return (Station) the second Station
     */
    public Station station2() {
        return station2;
    }


}
