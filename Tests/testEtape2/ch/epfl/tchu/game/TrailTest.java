package ch.epfl.tchu.game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public class TrailTest {
    @Test
    void checkToStringWithNonTrivialTrail() {
        Route r1 = new Route("BER_LUC_1", new Station(16, "Lucerne"), new Station(3, "Berne"), 4, Route.Level.OVERGROUND, null);
        Route r2 = new Route("BER_NEU_1", new Station(3, "Berne"), new Station(19, "Neuchâtel"), 2, Route.Level.OVERGROUND, Color.RED);
        Route r3 = new Route("NEU_SOL_1", new Station(19, "Neuchâtel"), new Station(26, "Soleure"), 4, Route.Level.OVERGROUND, Color.GREEN);
        Route r4 = new Route("BER_SOL_1", new Station(26, "Soleure"), new Station(3, "Berne"), 2, Route.Level.OVERGROUND, Color.BLACK);
        Route r5 = new Route("BER_FRI_1", new Station(3, "Berne"), new Station(9, "Fribourg"), 1, Route.Level.OVERGROUND, Color.ORANGE);

        List<Route> routes = List.of(r1, r2, r3, r4, r5);
        List<Route> routeS2 = List.of(r1, r2);
        Trail t = new Trail(routes);
        Trail t1 = new Trail(routeS2);

        Assertions.assertEquals("Lucerne - Berne - Neuchâtel - Soleure - Berne - Fribourg (13)", t.toString());
        Assertions.assertEquals("Lucerne - Berne - Neuchâtel (6)", t1.toString());
    }

    @Test
    void checkToStringWithTrivialTrail() {
        List<Route> routes = List.of();
        Trail t = new Trail(routes);

        Assertions.assertEquals("Empty Trail", t.toString());
    }

    @Test
    void checkStation1WithEmptyTrail() {
        List<Route> routes = List.of();
        Trail t = new Trail(routes);
        Assertions.assertEquals(null, t.station1);
        Assertions.assertEquals(null, t.station2);
        Assertions.assertEquals(0, t.length);
    }

    @Test
    void checkStation1WithTwoRouteTrail() {
        List<Route> routes = List.of(new Route("test", new Station(0, "st1"), new Station(1, "st2"), 2, Route.Level.OVERGROUND, null), new Route("test1", new Station(0, "st3"), new Station(1, "st4"), 2, Route.Level.OVERGROUND, null));
        Trail t1 = new Trail(routes);
        Assertions.assertEquals("st1", t1.station1.name());
        Assertions.assertEquals("st4", t1.station2.name());
        Assertions.assertEquals(4, t1.length());
    }

    @Test
    void checkLongestWithNonTrivialTrail() {
        Route r1 = new Route("BER_LUC_1", new Station(16, "Lucerne"), new Station(3, "Berne"), 4, Route.Level.OVERGROUND, null);
        Route r2 = new Route("BER_NEU_1", new Station(3, "Berne"), new Station(19, "Neuchâtel"), 2, Route.Level.OVERGROUND, Color.RED);
        Route r3 = new Route("NEU_SOL_1", new Station(19, "Neuchâtel"), new Station(26, "Soleure"), 4, Route.Level.OVERGROUND, Color.GREEN);
        Route r4 = new Route("BER_SOL_1", new Station(26, "Soleure"), new Station(3, "Berne"), 2, Route.Level.OVERGROUND, Color.BLACK);
        Route r5 = new Route("BER_FRI_1", new Station(3, "Berne"), new Station(9, "Fribourg"), 1, Route.Level.OVERGROUND, Color.ORANGE);

        List<Route> routes = List.of(r1, r2, r3, r4, r5);
        Trail t = new Trail(routes);
        Assertions.assertEquals(t.toString(), ch.epfl.tchu.game.Trail.longest(routes).toString());
    }


    //copy of the trail class just for testing
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

    }
}
