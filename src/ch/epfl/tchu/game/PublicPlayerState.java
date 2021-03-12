package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public class PublicPlayerState {

    private final int ticketCount;
    private final int cardCount;
    private final List<Route> routes;

    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
        Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = routes;
    }

    public int ticketCount(){
        return ticketCount;
    }
    public int cardCount(){
        return cardCount;
    }
    public List<Route> routes(){
        return routes;
    }
    public int carCount(){
        int length =0;
        for (Route r:routes) {
            length+= r.length();
        }
        return (Constants.INITIAL_CAR_COUNT - length);
    }
    public int claimPoints(){
        int buildPoints =0;
        for (Route r:routes) {
            buildPoints += r.claimPoints();
        }
        return buildPoints;
    }
}
