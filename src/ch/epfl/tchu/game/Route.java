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
        UNDERGROUND
    }

    private final String id;
    private final Station station1, station2;
    private final Level level;
    private final Color color;
    private final int length;

    /**
     * Constructor for a Route
     *
     * @param id       name of the Route
     * @param station1 first Station (from)
     * @param station2 second Station (To)
     * @param length   size of the Route
     * @param level    make the difference between Overground and Underground Route
     * @param color    color of the Route, define which card will be needed to claim it
     * @throws NullPointerException     if station are null,level is null or id is null
     * @throws IllegalArgumentException if length is more than 6 and less than 1, if the two station are the same
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        Preconditions.checkArgument(!station1.equals(station2) && length <= Constants.MAX_ROUTE_LENGTH
                && length >= Constants.MIN_ROUTE_LENGTH);

        if (station1 == null || station2 == null || level == null || id == null) {
            throw new NullPointerException("Station | Level | both is|are null");
        }

        this.id = id;
        this.station1 = station1;
        this.station2 = station2;
        this.level = level;
        this.length = length;
        this.color = color;
    }

    /**
     * Getter for the two route's station, given as a list
     *
     * @return (List < Station >) the list of the two Station
     */
    public List<Station> stations() {
        return List.of(station1, station2);
    }

    /**
     * Getter for the second station of a route according to the given one
     *
     * @param station one of the two Station of the Route
     * @return (Station) the other Station than the one given
     * @throws IllegalArgumentException if the given station isn't one of the Route's Station
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(station.equals(station1) || station.equals(station2));
        return (station.equals(station1) ? station2 : station1);
    }

    /**
     * Computes the number of points awarded when taking this Route
     *
     * @return (int) the points awarded according to the Route's length
     */
    public int claimPoints() {
        return Constants.ROUTE_CLAIM_POINTS.get(length);
    }

    /**
     * Computes from a List of SortedBag of Cards all the possible cards the player can choose to take the route
     *
     * @return (List < SortedBag < Card > >) a List of SortedBag of all possible Cards to claim the Route
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        List<SortedBag<Card>> possibleClaimCards = new ArrayList<>();

        if (color == null) {
            if (level.equals(Level.UNDERGROUND)) {
                unColorUnderGroundPossibleClaimCards(possibleClaimCards);
            } else {
                unColorOverGroundPossibleClaimCards(possibleClaimCards);
            }
        } else {
            if (level.equals(Level.UNDERGROUND)) {
                colorUndergroundPossibleClaimCards(possibleClaimCards);
            } else {
                colorOvergroundPossibleClaimCards(possibleClaimCards);
            }

        }
        return possibleClaimCards;
    }

    private void unColorUnderGroundPossibleClaimCards(List<SortedBag<Card>> possibleClaimCards) {

        for (int i = 0; i < length; i++) {
            int finalI = i;
            Color.ALL.stream().forEach(color1 -> possibleClaimCards.add(SortedBag.of(length - finalI,Card.of(color1), finalI,Card.LOCOMOTIVE)));
        }
        possibleClaimCards.add(SortedBag.of(length,Card.LOCOMOTIVE));
    }

    private void unColorOverGroundPossibleClaimCards(List<SortedBag<Card>> possibleClaimCards) {
        Color.ALL.stream().forEach(color1 -> possibleClaimCards.add(SortedBag.of(length,Card.of(color1))));
    }

    private void colorUndergroundPossibleClaimCards(List<SortedBag<Card>> possibleClaimCards) {
        SortedBag.Builder<Card> sBuilder;
        for (int i = 0; i <= length; i++) {
            sBuilder = new SortedBag.Builder<>();
            sBuilder.add(length - i, Card.of(color)).add(i, Card.LOCOMOTIVE);
            possibleClaimCards.add(sBuilder.build());
        }
    }

    private void colorOvergroundPossibleClaimCards(List<SortedBag<Card>> possibleClaimCards) {
        possibleClaimCards.add(SortedBag.of(length,Card.of(color)));
    }

    /**
     * Computes the number of additional card(s) a player must play to take a Route according
     * to his claimCards and his drawnCards
     *
     * @param claimCards the cards used to claim the Route
     * @param drawnCards the drawn cards by the player
     *
     * @throws IllegalArgumentException if there are more than three drawn cards
     *                                  if the Route is not Underground
     * @return (int) the number of additional card(s) the player must play to take the Route
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS && this.level == Level.UNDERGROUND);
        int additionalCard = 0;

        for (Card dc : drawnCards) {
            if (claimCards.contains(dc) || dc.color() == (Card.LOCOMOTIVE.color())) {
                ++additionalCard;
            }
        }
        return additionalCard;
    }

    /**
     * Getter for Route's id
     *
     * @return (String) the id
     */
    public String id() {
        return id;
    }

    /**
     * Getter for first Route's station
     *
     * @return (Station) the first Station
     */
    public Station station1() {
        return station1;
    }

    /**
     * Getter for second Route's station
     *
     * @return (Station) the second Station
     */
    public Station station2() {
        return station2;
    }

    /**
     * Getter for the Route's Level
     *
     * @return (Level) the level
     */
    public Level level() {
        return level;
    }

    /**
     * Getter for Route's Color
     *
     * @return (Color) the color
     */
    public Color color() {
        return color;
    }

    /**
     * Getter for Route's length
     *
     * @return (int) the length
     */
    public int length() {
        return length;
    }
}
