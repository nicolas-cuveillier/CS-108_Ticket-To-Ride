package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 * <p>
 * implements the notion of GameState in its more complete form, specialised from PublicGameState.
 */
public final class GameState extends PublicGameState {

    private final static int MINIMUM_CAR_FOR_LAST_TURN_BEGIN = 2;
    private final Deck<Ticket> tickets;
    private final Map<PlayerId, PlayerState> playerState;
    private final CardState cardState;

    private GameState(PlayerId currentPlayerId, Deck<Ticket> tickets, Map<PlayerId, PlayerState> playerState, CardState cardState, PlayerId lastPlayer) {
        super(tickets.size(), new PublicCardState(cardState.faceUpCards(), cardState.deckSize(),
                        cardState.discardsSize()), currentPlayerId, Map.copyOf(playerState), lastPlayer);

        this.tickets = Objects.requireNonNull(tickets);
        this.playerState = Map.copyOf(playerState);
        this.cardState = Objects.requireNonNull(cardState);
    }

    /**
     * Static method which computes the complete GameState with initial tickets.
     *
     * @param tickets SortedBag of tickets that a initially present in the game
     * @param rng     Random used to shuffle cards and Tickets
     * @return the initial GameState
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        final Map<PlayerId, PlayerState> playerState = new EnumMap<>(PlayerId.class);
        final SortedBag<Card> cards = Constants.ALL_CARDS;
        Deck<Card> deck = Deck.of(cards, rng);

        SortedBag.Builder<Card> playerCards;
        for (PlayerId playerId: PlayerId.ALL) {
            playerCards = new SortedBag.Builder<>();
            for (int i = 0; i < Constants.INITIAL_CARDS_COUNT; i++) {
                playerCards.add(deck.topCard());
                deck = deck.withoutTopCard();
            }
            playerState.put(playerId, PlayerState.initial(playerCards.build()));
        }
        CardState cardState = CardState.of(deck);

        return new GameState(PlayerId.ALL.get(rng.nextInt(PlayerId.COUNT)), Deck.of(tickets, rng), playerState, cardState, null);
    }

    /**
     * {@inheritDoc}
     *
     * @return the complete part of the playerId's PlayerState
     */
    @Override
    public PlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * {@inheritDoc}
     *
     * @return the complete part of the currentPlayer's  PlayerState
     * @see #playerState(PlayerId)
     */
    @Override
    public PlayerState currentPlayerState() {
        return playerState(currentPlayerId());
    }

    /**
     * Getter for the specified count of top ticket(s) from all tickets.
     *
     * @param count the number of top tickets needed
     * @return (SortedBag<Ticket>) the SortedBag of the top count Tickets
     * @throws IllegalArgumentException if counts is negative or superior than tickets' size
     */
    public SortedBag<Ticket> topTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= tickets.size());
        return tickets.topCards(count);
    }

    /**
     * Computes a new GameState without the first count tickets of the deck of tickets.
     *
     * @param count the number of top tickets to remove
     * @return the same GameState without the count top tickets
     * @throws IllegalArgumentException if counts is negative or superior than tickets' size
     */
    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= tickets.size());
        return new GameState(currentPlayerId(), tickets.withoutTopCards(count), playerState, cardState, lastPlayer());
    }

    /**
     * Getter for the top deck card.
     *
     * @return the first card of the deck of cards
     * @throws IllegalArgumentException if the deck of cards is empty
     */
    public Card topCard() {
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return cardState.topDeckCard();
    }

    /**
     * Computes a new GameState without the first card of the deck of cards.
     *
     * @return the same game without the first card of the deck of Card
     * @throws IllegalArgumentException if the deck of cards is empty
     */
    public GameState withoutTopCard() {
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return new GameState(currentPlayerId(), tickets, playerState, cardState.withoutTopDeckCard(), lastPlayer());
    }

    /**
     * Computes a new GameState with more discard's cards.
     *
     * @param discardedCards cards that will be added to the discard
     * @return the same game with this SortedBag of cards added to the discard
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(currentPlayerId(), tickets, playerState, cardState.withMoreDiscardedCards(discardedCards), lastPlayer());
    }

    /**
     * Computes a new GameState where the cards' deck has been recreated from the discard.
     *
     * @param rng Random used to shuffle the deck
     * @return the original GameState if the deck isn't empty or compute the same GameState but with Deck cards
     * recreated from the discard if not
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        return cardState.isDeckEmpty() ?
                new GameState(currentPlayerId(), tickets, playerState, cardState.withDeckRecreatedFromDiscards(rng), lastPlayer()) : this;
    }

    /**
     * Modifies the specified player's PlayerState so chosenTickets are added to its tickets.
     *
     * @param playerId      the player that will receive the tickets
     * @param chosenTickets tickets that are chosen by the player
     * @return the same GameState
     * @throws IllegalArgumentException if the player as already some tickets
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(playerState.get(playerId).tickets().isEmpty());

        final Map<PlayerId, PlayerState> playerState = new EnumMap<>(this.playerState);
        playerState.put(playerId, playerState(playerId).withAddedTickets(chosenTickets));

        return new GameState(currentPlayerId(), tickets, playerState, cardState, lastPlayer());
    }

    /**
     * Computes a new GameState where the current player took chosenTickets.
     *
     * @param drawnTickets  tickets drawn by the player
     * @param chosenTickets tickets chosen by the player
     * @return the same GameState where the chosen tickets have been take out from all tickets and where chosenTickets
     * have been added to the currentPlayer tickets
     * @throws IllegalArgumentException if drawnTickets doesn't contains the chosenTickets
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));

        final Map<PlayerId, PlayerState> playerState = new EnumMap<>(this.playerState);
        playerState.put(currentPlayerId(), currentPlayerState().withAddedTickets(chosenTickets));

        return new GameState(currentPlayerId(), tickets.withoutTopCards(drawnTickets.size()), playerState, cardState, lastPlayer());
    }

    /**
     * Adds the card in the faceUpCard at the specified slot to the current player's cards.
     *
     * @param slot (int) the index of the face up card
     * @return the same GameState where the card in the faceUpCard at the specified slot is replaced by the topDeckCard
     * @throws IllegalArgumentException if it's not possible to draw cards
     * @see #canDrawCards()
     */
    public GameState withDrawnFaceUpCard(int slot) {

        final Map<PlayerId, PlayerState> playerState = new EnumMap<>(this.playerState);
        playerState.put(currentPlayerId(), currentPlayerState().withAddedCard(cardState.faceUpCard(slot)));

        return new GameState(currentPlayerId(), tickets, playerState, cardState.withDrawnFaceUpCard(slot), lastPlayer());
    }

    /**
     * Adds the top deck card to the current player's cards.
     *
     * @return the same GameState where the top card of the deck card has been removed
     * @throws IllegalArgumentException if it's not possible to draw cards
     * @see #canDrawCards()
     */
    public GameState withBlindlyDrawnCard() {

        final Map<PlayerId, PlayerState> playerState = new EnumMap<>(this.playerState);
        playerState.put(currentPlayerId(), currentPlayerState().withAddedCard(cardState.topDeckCard()));

        return new GameState(currentPlayerId(), tickets, playerState, cardState.withoutTopDeckCard(), lastPlayer());
    }

    /**
     * Computes a new GameState with a Route claimed by the current player.
     *
     * @param route route which the current player claimed
     * @param cards cards that will be discarded to claim the route
     * @return the same GameState where the current player has seized the given route using the given cards.
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {

        final Map<PlayerId, PlayerState> playerState = new EnumMap<>(this.playerState);
        playerState.put(currentPlayerId(), currentPlayerState().withClaimedRoute(route, cards));

        return new GameState(currentPlayerId(), tickets, playerState, cardState.withMoreDiscardedCards(cards), lastPlayer());
    }

    /**
     * Tests if the last turn begins.
     *
     * @return true iff the last player is still unknown and the current player has less than two car
     */
    public boolean lastTurnBegins() {
        return lastPlayer() == null && currentPlayerState().carCount() <= MINIMUM_CAR_FOR_LAST_TURN_BEGIN;
    }

    /**
     * Computes the GameState for the next turn given if it will be the last turn or not. The currentPlayer becomes
     * the last player if it is the last turn.
     *
     * @return the same GameState except that the other player is now the current player.
     */
    public GameState forNextTurn() {
        return (lastTurnBegins() ? new GameState(currentPlayerId().next(), tickets, playerState, cardState, currentPlayerId())
                : new GameState(currentPlayerId().next(), tickets, playerState, cardState, lastPlayer()));
    }
}
