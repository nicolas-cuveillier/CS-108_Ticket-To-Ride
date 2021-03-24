package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 * <p>
 * implements the public part of the notion of game state
 */
public class PublicGameState {

    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;

    /**
     * Only constructor for a Public Game State, define the game state with the public part of its attribute
     *
     * @param ticketsCount    the number of tickets
     * @param cardState       the public state of the cards
     * @param currentPlayerId the current player
     * @param playerState     the public player's state for each player
     * @param lastPlayer      the last player of the game, can be null
     * @throws IllegalArgumentException if ticketsCount is negative
     *                                  if there is more than two player
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        Preconditions.checkArgument(ticketsCount >= 0);
        Preconditions.checkArgument(playerState.size() == 2);

        if (cardState == null || currentPlayerId == null) {
            throw new NullPointerException();
        }

        this.ticketsCount = ticketsCount;
        this.cardState = cardState;
        this.currentPlayerId = currentPlayerId;
        this.playerState = Map.copyOf(playerState);
        this.lastPlayer = lastPlayer;
    }

    /**
     * getter for ticketsCount
     *
     * @return the number of tickets
     */
    public int ticketsCount() {
        return ticketsCount;
    }

    /**
     * @return true iff there are still tickets left
     */
    public boolean canDrawTickets() {
        return ticketsCount != 0;
    }

    /**
     * getter for the PublicCardState attribute
     *
     * @return the PublicCardState
     */
    public PublicCardState cardState() {
        return new PublicCardState(cardState.faceUpCards(), cardState.deckSize(), cardState.discardsSize());
    }

    /**
     * @return true iff the cards overall are more or equal to five
     */
    public boolean canDrawCards() {
        return (cardState.deckSize() + cardState.discardsSize() >= 5);
    }

    /**
     * getter for the currentPlayerId
     *
     * @return a playerId
     */
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    /**
     * getter for the PublicPlayerState of the PlayerId
     *
     * @param playerId the player for which it will return its PublicPLayerState
     * @return a PublicPlayerState
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return new PublicPlayerState(playerState.get(playerId).ticketCount(), playerState.get(playerId).cardCount(), playerState.get(playerId).routes());
    }

    /**
     * getter for the PublicPlayerState of currentPlayer
     *
     * @return a PublicPlayerState
     * @see #playerState(PlayerId)
     */
    public PublicPlayerState currentPlayerState() {
        return playerState(currentPlayerId);
    }

    /**
     * getter for the overall Route claimed by one or the other player
     *
     * @return all claimed Route
     */
    public List<Route> claimedRoutes() {
        List<Route> claimedRoutes = new ArrayList<>(currentPlayerState().routes());
        claimedRoutes.addAll(playerState(currentPlayerId.next()).routes());
        return claimedRoutes;
    }

    /**
     * getter for the Id of the last player
     *
     * @return a PlayerId
     */
    public PlayerId lastPlayer() {
        return lastPlayer;
    }
}
