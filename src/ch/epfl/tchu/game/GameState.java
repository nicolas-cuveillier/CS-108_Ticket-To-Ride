package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 *
 * implements the notion of GameState in its more complete form, specialised from PublicGameState
 */
public final class GameState extends PublicGameState {

    private final SortedBag<Ticket> tickets;
    private final Map<PlayerId, PlayerState> playerState;
    private final CardState cardState;
    private final PlayerId lastPlayer;

    private GameState(PlayerId currentPlayerId, SortedBag<Ticket> tickets, Map<PlayerId, PlayerState> playerState, CardState cardState, PlayerId lastPlayer) {

        super(tickets.size(), new PublicCardState(cardState.faceUpCards(), cardState.deckSize(), cardState.discardsSize())
                , currentPlayerId, Map.copyOf(playerState), lastPlayer);

        this.tickets = tickets;             //
        this.playerState = playerState;     //copy?
        this.cardState = cardState;         //
        this.lastPlayer = lastPlayer;
    }

    /**
     * static method which compute the complete GameState with initial tickets
     * @param tickets SortedBag of tickets that a initially present in the game
     * @param rng Random used to shuffle cards and Tickets
     * @return a initial GameState
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        Map<PlayerId, PlayerState> playerState = new EnumMap<>(PlayerId.class);
        playerState.put(PlayerId.PLAYER_1, PlayerState.initial(SortedBag.of(Constants.ALL_CARDS.toList().subList(0, 4))));//change
        playerState.put(PlayerId.PLAYER_2, PlayerState.initial(SortedBag.of(Constants.ALL_CARDS.toList().subList(4, 8))));

        SortedBag<Card> cards = Constants.ALL_CARDS.difference(SortedBag.of(Constants.ALL_CARDS.toList().subList(8, Constants.ALL_CARDS.size())));

        Collections.shuffle(cards.toList(), rng);//useful?
        Collections.shuffle(tickets.toList(), rng);

        CardState cardState = CardState.of(Deck.of(cards, rng));
        return new GameState(PlayerId.ALL.get(rng.nextInt(2)), tickets, playerState, cardState, null);
    }
    /**
     * {@inheritDoc}
     * @return the complete part of the playerId's PlayerState
     */
    @Override
    public PlayerState playerState(PlayerId playerId) {
        return new PlayerState(playerState.get(playerId).tickets(), playerState.get(playerId).cards(), playerState.get(playerId).routes());
    }
    /**
     * {@inheritDoc}
     * @return the complete part of the currentPlayer's  PlayerState
     */
    @Override
    public PlayerState currentPlayerState() {
        return playerState(currentPlayerId());
    }

    /**
     * getter for the count top ticket(s) of all tickets
     * @param count (int)
     * @return a SortedBag of Ticket
     */
    public SortedBag<Ticket> topTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= tickets.size());
        return SortedBag.of(tickets.toList().subList(0, count));
    }

    /**
     * @param count (int)
     * @return the same GameState without the count top tickets
     */
    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= tickets.size());
        return new GameState(currentPlayerId(), tickets.difference(topTickets(count)), playerState, cardState, lastPlayer);
    }

    /**
     * getter for the top deck card
     * @return (Card)
     */
    public Card topCard() {
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return cardState.topDeckCard();
    }

    /**
     * @return the same game without the first card of the deck of Card
     */
    public GameState withoutTopCard() {
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return new GameState(currentPlayerId(), tickets, playerState, cardState.withoutTopDeckCard(), lastPlayer);
    }

    /**
     * @param discardedCards cards that will be added to the discard
     * @return the same game with this SortedBag of cards added to the discard
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(currentPlayerId(), tickets, playerState, cardState.withMoreDiscardedCards(discardedCards), lastPlayer);
    }

    /**
     * @param rng (Random)
     * @return the original GameState if the deck isn't empty or compute the same GameState but with Deck cards
     * recreated from the discard if not
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        return cardState.isDeckEmpty() ? new GameState(currentPlayerId(), tickets, playerState, cardState.withDeckRecreatedFromDiscards(rng), lastPlayer) : this;
    }

    /**
     * modify the playerId's PlayerState so chosenTickets are added to its tickets
     * @param playerId the player that will receive the tickets
     * @param chosenTickets tickets that are chosen by the player
     * @return the same GameState
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(playerState.get(playerId).tickets().isEmpty());

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(this.playerState);
        playerState.put(playerId, playerState(playerId).withAddedTickets(chosenTickets));

        return new GameState(currentPlayerId(), tickets, playerState, cardState, lastPlayer);
    }

    /**
     * @param drawnTickets tickets drawn by the player
     * @param chosenTickets tickets chosen by the player
     * @return the same GameState where the chosen tickets have been take out from all tickets and where chosenTickets
     * have been added to the currentPlayer tickets
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(this.playerState);
        playerState.put(currentPlayerId(), currentPlayerState().withAddedTickets(chosenTickets));

        return new GameState(currentPlayerId(), tickets.difference(chosenTickets), playerState, cardState, lastPlayer);
    }

    /**
     * add the slot-th card in the faceUpCard to the current player's deck of cards
     * @param slot (int)
     * @return the same GameState where the slot-th card in the faceUpCard is replace by the topDeckCard
     */
    public GameState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(canDrawCards());

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(this.playerState);
        playerState.put(currentPlayerId(), currentPlayerState().withAddedCard(cardState.faceUpCard(slot)));

        return new GameState(currentPlayerId(), tickets, playerState, cardState.withDrawnFaceUpCard(slot), lastPlayer);
    }

    /**
     * add the top deck card to the current player's deck of cards
     * @return the same GameState where the top card of the deck card has been removed
     */
    public GameState withBlindlyDrawnCard() {
        Preconditions.checkArgument(canDrawCards());

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(this.playerState);
        playerState.put(currentPlayerId(), currentPlayerState().withAddedCard(cardState.topDeckCard()));

        return new GameState(currentPlayerId(), tickets, playerState, cardState.withoutTopDeckCard(), lastPlayer);
    }

    /**
     * compute a new GameState
     * @param route route that the currentPlayer claimed
     * @param cards cards that will be dropped into the discards
     * @return the same GameState where the current player has seized the given route using the given cards.
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(this.playerState);
        playerState.put(currentPlayerId(), currentPlayerState().withClaimedRoute(route, cards));

        return new GameState(currentPlayerId(), tickets, playerState, cardState.withMoreDiscardedCards(cards), lastPlayer);
    }

    /**
     * @return true iff the last player is still unknown and the current player has less than two car
     */
    public boolean lastTurnBegins() {
        return lastPlayer() == null && currentPlayerState().carCount() <= 2;
    }

    /**
     * compute the game State for the next turn given if it will be the last turn or not. The currentPlayer becomes
     * the last player if it is the last turn
     * @return the same GameState except that the other player is now the current player.
     */
    public GameState forNextTurn() {
        return (lastTurnBegins() ? new GameState(currentPlayerId().next(), tickets, playerState, cardState, currentPlayerId())
                : new GameState(currentPlayerId().next(), tickets, playerState, cardState, lastPlayer));
    }
}
