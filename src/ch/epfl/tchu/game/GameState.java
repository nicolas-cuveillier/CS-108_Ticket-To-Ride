package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public final class GameState extends PublicGameState {

    private final SortedBag<Ticket> tickets;
    private final Map<PlayerId, PlayerState> playerState;
    private final CardState cardState;
    private final PlayerId lastPlayer;

    private GameState(PlayerId currentPlayerId, SortedBag<Ticket> tickets, Map<PlayerId, PlayerState> playerState, CardState cardState, PlayerId lastPlayer) {

        super(tickets.size(), new PublicCardState(cardState.faceUpCards(), cardState.deckSize(), cardState.discardsSize())
                , currentPlayerId, Map.copyOf(playerState), lastPlayer);

        this.tickets = tickets;
        this.playerState = playerState;
        this.cardState = cardState;
        this.lastPlayer = lastPlayer;
    }

    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        SortedBag<Card> cards = Constants.ALL_CARDS.difference(SortedBag.of(Constants.ALL_CARDS.toList().subList(0, 8)));

        Collections.shuffle(cards.toList(), rng);
        Collections.shuffle(tickets.toList(), rng);

        CardState cardState = CardState.of(Deck.of(cards, rng));

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(PlayerId.class);
        playerState.put(PlayerId.PLAYER_1, PlayerState.initial(SortedBag.of(Constants.ALL_CARDS.toList().subList(0, 4))));
        playerState.put(PlayerId.PLAYER_2, PlayerState.initial(SortedBag.of(Constants.ALL_CARDS.toList().subList(4, 8))));

        return new GameState(PlayerId.ALL.get(rng.nextInt(2)), tickets, playerState, cardState, null);
    }

    @Override
    public PlayerState playerState(PlayerId playerId) {
        return new PlayerState(playerState.get(playerId).tickets(), playerState.get(playerId).cards(), playerState.get(playerId).routes());
    }

    @Override
    public PlayerState currentPlayerState() {
        return playerState(currentPlayerId());
    }

    public SortedBag<Ticket> topTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= tickets.size());
        return SortedBag.of(tickets.toList().subList(0, count));
    }

    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= tickets.size());
        return new GameState(currentPlayerId(), tickets.difference(topTickets(count)), playerState, cardState, null);
    }

    public Card topCard() {
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return cardState.topDeckCard();
    }

    public GameState withoutTopCard() {
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return new GameState(currentPlayerId(), tickets, playerState, cardState.withoutTopDeckCard(), null);
    }

    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(currentPlayerId(), tickets, playerState, cardState.withMoreDiscardedCards(discardedCards), null);
    }

    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        if (cardState.isDeckEmpty()) {
            new GameState(currentPlayerId(), tickets, playerState, cardState.withDeckRecreatedFromDiscards(rng), null);
            return null;
        } else {
            return this;
        }
    }

    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(playerState.get(playerId).tickets().isEmpty());

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(this.playerState);
        playerState.put(playerId, playerState(playerId).withAddedTickets(chosenTickets));

        return new GameState(currentPlayerId(), tickets, playerState, cardState, null);
    }

    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(this.playerState);
        playerState.put(currentPlayerId(), currentPlayerState().withAddedTickets(chosenTickets));

        return new GameState(currentPlayerId(), tickets.difference(chosenTickets), playerState, cardState, null);
    }

    public GameState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(canDrawCards());

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(this.playerState);
        playerState.put(currentPlayerId(), currentPlayerState().withAddedCard(cardState.faceUpCard(slot)));
        cardState.withDrawnFaceUpCard(slot);

        return new GameState(currentPlayerId(), tickets, playerState, cardState.withDrawnFaceUpCard(slot), null);
    }

    public GameState withBlindlyDrawnCard() {
        Preconditions.checkArgument(canDrawCards());

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(this.playerState);
        playerState.put(currentPlayerId(), currentPlayerState().withAddedCard(cardState.topDeckCard()));

        return new GameState(currentPlayerId(), tickets, playerState, cardState.withoutTopDeckCard(), null);
    }

    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(this.playerState);
        playerState.put(currentPlayerId(), currentPlayerState().withClaimedRoute(route, cards));

        return new GameState(currentPlayerId(), tickets, playerState, cardState.withMoreDiscardedCards(cards), null);
    }

    public boolean lastTurnBegins() {
        return lastPlayer() == null && currentPlayerState().carCount() <= 2;
    }

    public GameState forNextTurn() {
        return (lastTurnBegins() ? new GameState(currentPlayerId().next(), tickets, playerState, cardState, currentPlayerId())
                : new GameState(currentPlayerId().next(), tickets, playerState, cardState, null));
    }
}
