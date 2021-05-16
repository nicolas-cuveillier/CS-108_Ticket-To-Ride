package ch.epfl.tchu.game;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implements the notion of Trail in the game.
 * 
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public final class Trail {

    private final int length;
    private final List<Route> routes;
    private final Station station1;
    private final Station station2;
    private final static String TRAIL_WITH_NO_ROUTES = "Empty Trail";


    private Trail(List<Route> routes, Station station1, Station station2) {
        this.routes = List.copyOf(routes);
        this.station1 = station1;
        this.station2 = station2;
        this.length = routes.stream()
                .mapToInt(Route::length)
                .sum();
    }

    /**
     * Computes the longest or one of the longest Trail for a given List of Route.
     *
     * @param routes the list of all Route(s) to make the Trail
     * @return (Trail) the longest trail given all Route(s)
     */
    public static Trail longest(List<Route> routes) {

        if (routes.isEmpty())
            return new Trail(List.of(), null, null);

        List<Trail> cs;
        Trail longestTrail = null;
        int length = 0;

        cs = getSingleRoutes(routes);

        while (!cs.isEmpty()) {
            List<Trail> cs2 = new ArrayList<>();

            for (Trail c : cs) {

                if (longestTrail != null)
                    length = longestTrail.length;

                //go through all routes and search possible candidate to continue the trail
                routes.stream()
                        .filter(route -> !c.routes().contains(route))
                        .forEach(route -> {
                            if ((c.station2().id() == route.station1().id()) && (Objects.equals(c.station2().name(), route.station1().name()))) {
                                addTrailBuildWithRouteAccordingToSt2(c, cs2, route);
                            } else if ((c.station2().id() == route.station2().id()) && (Objects.equals(c.station2().name(), route.station2().name()))) {
                                addTrailBuildWithRouteAccordingToSt1(c, cs2, route);
                            }
                        });

                longestTrail = length < c.length() ? c : longestTrail;
            }
            cs = cs2;
        }
        return longestTrail;
    }

    private static void addTrailBuildWithRouteAccordingToSt1(Trail c, List<Trail> cs2, Route toAdd) {
        List<Route> routeList = new ArrayList<>(c.routes());
        routeList.add(toAdd);
        Trail t = new Trail(routeList, c.station1(), toAdd.station1());
        cs2.add(t);
    }

    private static void addTrailBuildWithRouteAccordingToSt2(Trail c, List<Trail> cs2, Route toAdd) {
        List<Route> routeList = new ArrayList<>(c.routes());
        routeList.add(toAdd);
        Trail t = new Trail(routeList, c.station1(), toAdd.station2());
        cs2.add(t);
    }

    private static List<Trail> getSingleRoutes(List<Route> routes) {
        final List<Trail> trails = new ArrayList<>();

        routes.forEach(r -> trails.add(new Trail(List.of(r), r.station1(), r.station2())));
        routes.forEach(r -> trails.add(new Trail(List.of(r), r.station2(), r.station1())));
        return trails;
    }

    /**
     * @return textual representation of a trail
     */
    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();

        int totalLength = routes.stream()
                .mapToInt(Route::length)
                .sum();

        if (routes.size() != 0) {
            text.append(routes.get(0).station1().name())
                    .append(routes.get(routes.size() - 1).station2())
                    .append(" (")
                    .append(totalLength)
                    .append(")");
        } else
            text.append(TRAIL_WITH_NO_ROUTES);

        return text.toString();
    }

    /**
     * Getter for the private field length.
     *
     * @return (int) the length
     */
    public int length() {
        return length;
    }

    /**
     * Getter for the private field routes.
     *
     * @return (List < Route >) the list of all Route(s)
     */
    public List<Route> routes() {
        return routes;
    }

    /**
     * Getter for the first Station.
     *
     * @return (Station) the first Station
     */
    public Station station1() {
        return station1;
    }

    /**
     * Getter for the last Station.
     *
     * @return (Station) the second Station
     */
    public Station station2() {
        return station2;
    }


}
