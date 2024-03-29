package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**<h1>PublicGameState</h1>
 * Represents the public part of the Game's state. Can only return public states of the objects.
 * 
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public class PublicGameState {

    private final static int MINIMUM_CARD_FOR_DRAWING = 5;
    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;

    /**
     * Constructor for a PublicGameState, defines the game state with the public part of it's attribute.
     *
     * @param ticketsCount    the number of tickets
     * @param cardState       the public state of the cards
     * @param currentPlayerId the current player
     * @param playerState     the public player's state for each player
     * @param lastPlayer      the last player of the game, can be null
     * @throws IllegalArgumentException if ticketsCount is negative
     *                                  if there is more than two player
     * @throws NullPointerException     if cardState or currentPlayerId is null
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        Preconditions.checkArgument(ticketsCount >= 0);
        Preconditions.checkArgument(playerState.size() == PlayerId.COUNT);

        this.ticketsCount = ticketsCount;
        this.cardState = Objects.requireNonNull(cardState);
        this.currentPlayerId = Objects.requireNonNull(currentPlayerId);
        this.playerState = Map.copyOf(playerState);
        this.lastPlayer = lastPlayer;
    }


    /**
     * Tests whether it is possible to draw tickets or not.
     *
     * @return true iff there are still tickets left
     */
    public boolean canDrawTickets() {
        return ticketsCount != 0;
    }

    /**
     * Tests whether it is possible to draw cards from the deck or not.
     *
     * @return true iff the cards overall are more or equal to five, the minimum cards to fulfill all emplacement
     */
    public boolean canDrawCards() {
        return (cardState.deckSize() + cardState.discardsSize() >= MINIMUM_CARD_FOR_DRAWING);
    }

    /**
     * Getter for the private field ticketsCount.
     *
     * @return the number of tickets
     */
    public int ticketsCount() {
        return ticketsCount;
    }

    /**
     * Getter for the PublicCardState computed with the private cardState property.
     *
     * @return the PublicCardState
     */
    public PublicCardState cardState() {
        return cardState;
    }


    /**
     * Getter for the private field currentPlayerId.
     *
     * @return a playerId
     */
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    /**
     * Getter for the PublicPlayerState computed using the specified PlayerId.
     *
     * @param playerId the player for which it will return its PublicPLayerState
     * @return a PublicPlayerState
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * Getter for the PublicPlayerState for the currentPlayer.
     *
     * @return a PublicPlayerState
     * @see #playerState(PlayerId)
     */
    public PublicPlayerState currentPlayerState() {
        return playerState(currentPlayerId);
    }

    /**
     * Getter for every Route claimed by the currentPlayer.
     *
     * @return all claimed Route
     */
    public List<Route> claimedRoutes() {
        final List<Route> claimedRoutes = new ArrayList<>(currentPlayerState().routes());
        claimedRoutes.addAll(playerState(currentPlayerId.next()).routes());
        return claimedRoutes;
    }

    /**
     * Getter for the Id of the last player.
     *
     * @return the lastPlayer PlayerId
     */
    public PlayerId lastPlayer() {
        return lastPlayer;
    }
}
