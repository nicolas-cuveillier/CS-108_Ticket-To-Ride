package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 * <p>
 * implement the notion of route in the game
 */
public final class Route {
    /**
     * kind of route you find in the game
     * {@link #OVERGROUND}
     * {@link #UNDERGROUND}
     */
    public enum Level {
        OVERGROUND,
        UNDERGROUND;
    }

    private final String id;
    private final Station station1, station2;
    private final Level level;
    private final Color color;
    private final int length;

    /**
     * principal and only constructor for a Route
     *
     * @param id       (String)
     * @param station1 (Station)
     * @param station2 (Station)
     * @param length   (int)
     * @param level    (Level)
     * @param color    (Color)
     * @throws NullPointerException,IllegalArgumentException
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        Preconditions.checkArgument(!station1.equals(station2) && length <= Constants.MAX_ROUTE_LENGTH
                && length >= Constants.MIN_ROUTE_LENGTH);

        if (station1 == null || station2 == null || level == null) {
            throw new NullPointerException("Station | Level | both is null");
        }

        this.id = id;
        this.station1 = station1;
        this.station2 = station2;
        this.level = level;
        this.length = length;
        this.color = color;
    }

    /**
     * give a List of the two route's station
     *
     * @return (List < Station >)
     */
    public List<Station> stations() {
        return List.of(station1, station2);
    }

    /**
     * give the second station of a route according to the given one
     *
     * @param station (Station)
     * @return
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(station.id() == station1.id() || station.id() == station2.id());
        return (station.id() == station1.id() ? station2 : station1);
    }

    /**
     * compute the number of points given when taking this route
     *
     * @return (int)
     */
    public int claimPoints() {
        return Constants.ROUTE_CLAIM_POINTS.get(length);
    }

    /**
     * compute in a List of SortedBag of Cards all the possible card the player can choose to take the route
     *
     * @return (List < SortedBag < Card > >)
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        SortedBag.Builder sBuilder;
        List<SortedBag<Card>> possibleClaimCards = new ArrayList<>();

        if (color == null) {
            if (level.equals(Level.UNDERGROUND)) {
                for (int i = 0; i < length; i++) {
                    for (Color c : Color.ALL) {
                        sBuilder = new SortedBag.Builder();
                        sBuilder.add(length - i, Card.of(c)).add(i, Card.LOCOMOTIVE);
                        possibleClaimCards.add(sBuilder.build());
                    }
                }
                sBuilder = new SortedBag.Builder();
                sBuilder.add(length, Card.LOCOMOTIVE);
                possibleClaimCards.add(sBuilder.build());


            } else {
                for (Color c : Color.ALL) {
                    sBuilder = new SortedBag.Builder();
                    sBuilder.add(length, c);
                    possibleClaimCards.add(sBuilder.build());
                }
            }
        } else {
            if (level.equals(Level.UNDERGROUND)) {
                for (int i = 0; i <= length; i++) {
                    sBuilder = new SortedBag.Builder();
                    sBuilder.add(length - i, Card.of(color)).add(i, Card.LOCOMOTIVE);
                    possibleClaimCards.add(sBuilder.build());
                }
            } else {
                sBuilder = new SortedBag.Builder();
                sBuilder.add(length, Card.of(color));
                possibleClaimCards.add(sBuilder.build());
            }

        }
        return possibleClaimCards;
    }

    /**
     * compute the number of additional card a player must play to take a route according
     * to his claimCards and his drawnCards
     *
     * @param claimCards (SortedBag<Card>)
     * @param drawnCards (SortedBag<Card>)
     * @return (int)
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(drawnCards.size() == 3 && this.level == Level.UNDERGROUND);
        int additionalCard = 0;

        for (Card dc : drawnCards) {
            if (claimCards.contains(dc) || dc.color() == (Card.LOCOMOTIVE.color())) {
                ++additionalCard;
            }
        }
        return additionalCard;
    }

    /**
     * getter for route's id
     *
     * @return (String)
     */
    public String id() {
        return id;
    }

    /**
     * getter for first route's station
     *
     * @return (Station)
     */
    public Station station1() {
        return station1;
    }

    /**
     * getter for second route's station
     *
     * @return (Station)
     */
    public Station station2() {
        return station2;
    }

    /**
     * getter for the route's Level
     *
     * @return (Level)
     */
    public Level level() {
        return level;
    }

    /**
     * getter for route's Color
     *
     * @return (Color)
     */
    public Color color() {
        return color;
    }

    /**
     * getter for route's length
     *
     * @return (int)
     */
    public int length() {
        return length;
    }
}
