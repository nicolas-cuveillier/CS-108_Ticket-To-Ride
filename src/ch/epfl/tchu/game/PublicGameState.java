package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public class PublicGameState {
    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;

    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        Preconditions.checkArgument(ticketsCount >= 0);
        Preconditions.checkArgument(playerState.size() == 2);
        if (cardState == null || currentPlayerId == null) {
            throw new NullPointerException();
        }
        this.ticketsCount = ticketsCount;
        this.cardState = cardState;                 //
        this.currentPlayerId = currentPlayerId;     //copy?
        this.playerState = playerState;             //
        this.lastPlayer = lastPlayer;               //
    }

    public int ticketsCount() {
        return ticketsCount;
    }

    public boolean canDrawTickets() {
        return ticketsCount == 0;
    }

    public PublicCardState cardState() {
        return new PublicCardState(cardState.faceUpCards(), cardState.deckSize(), cardState.discardsSize());
    }

    public boolean canDrawCards() {
        return (cardState.deckSize() + cardState.discardsSize() >= 5);
    }

    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    public PublicPlayerState playerState(PlayerId playerId) {
        return new PublicPlayerState(playerState.get(playerId).ticketCount(), playerState.get(playerId).cardCount(), playerState.get(playerId).routes());
    }

    public PublicPlayerState currentPlayerState() {
        return playerState(currentPlayerId);
    }

    public List<Route> claimedRoutes() {
        List<Route> claimedRoutes = new ArrayList<>(currentPlayerState().routes());
        claimedRoutes.addAll(playerState(currentPlayerId.next()).routes());
        return claimedRoutes;
    }

    public PlayerId lastPlayer() {
        return lastPlayer;
    }
}
