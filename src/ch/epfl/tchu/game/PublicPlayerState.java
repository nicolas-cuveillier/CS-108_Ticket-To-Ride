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
    private int carCount;
    private int claimPoints;

    /**
     * single constructor for PublicPlayerState
     *
     * @param ticketCount (int)
     * @param cardCount   (int)
     * @param routes      (List<Route>)
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
        Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = routes;

    }

    /**
     * getter for the number of tickets
     *
     * @return (int)
     */
    public int ticketCount() {
        return ticketCount;
    }

    /**
     * getter for the number of cards
     *
     * @return (int)
     */
    public int cardCount() {
        return cardCount;
    }

    /**
     * getter for the routes
     *
     * @return (List < Route >)
     */
    public List<Route> routes() {
        return new ArrayList<>(routes);
    }

    /**
     * getter for the number of car the player still has
     *
     * @return (int)
     */
    public int carCount() {
        int length = 0;
        for (Route r : routes) {
            length += r.length();
        }
        carCount = (Constants.INITIAL_CAR_COUNT - length);
        return carCount;
    }

    /**
     * getter for the claim points according to the player Route
     *
     * @return (int)
     */
    public int claimPoints() {
        int buildPoints = 0;
        for (Route r : routes) {
            buildPoints += r.claimPoints();
        }
        claimPoints = buildPoints;
        return claimPoints;
    }
}
