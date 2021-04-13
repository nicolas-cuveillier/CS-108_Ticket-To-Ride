package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 * <p>
 * represent the public part of the player's state
 */
public class PublicPlayerState {

    private final int ticketCount;
    private final int cardCount;
    private final List<Route> routes;
    private final int carCount;
    private final int claimPoints;

    /**
     * Constructor for PublicPlayerState, build it with a certain count of tickets and cards and a List of Route
     *
     * @param ticketCount the number of tickets
     * @param cardCount   the number of card(s)
     * @param routes      the Route(s) that the player claimed
     * @throws IllegalArgumentException if ticketsCount or cardCount are negative
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
        Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);

        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = new ArrayList<>(routes);

        final int length = routes.stream()
                .mapToInt(i -> i.length())
                .sum();
        carCount = (Constants.INITIAL_CAR_COUNT - length);

        final int buildPoints = routes.stream()
                .mapToInt(i -> i.claimPoints())
                .sum();
        claimPoints = buildPoints;
    }

    /**
     * getter for the number of tickets
     *
     * @return (int) the number of tickets
     */
    public int ticketCount() {
        return ticketCount;
    }

    /**
     * getter for the number of cards
     *
     * @return (int) the number of card(s)
     */
    public int cardCount() {
        return cardCount;
    }

    /**
     * Getter for the routes
     *
     * @return (List < Route >) the list of all player's Route(s)
     */
    public List<Route> routes() {
        return List.copyOf(routes);
    }

    /**
     * Getter for the number of car the player still has
     *
     * @return (int) the number of car of the player
     */
    public int carCount() {
        return carCount;
    }

    /**
     * Getter for the claim points according to the player Route
     *
     * @return (int) the number of points that the player received from claiming Route(s)
     */
    public int claimPoints() {
        return claimPoints;
    }
}
