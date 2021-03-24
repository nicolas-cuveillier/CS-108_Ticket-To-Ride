package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 * <p>
 * represent the Card status which are not in the possession of players
 */
public final class CardState extends PublicCardState {
    private final Deck<Card> deck;
    private final SortedBag<Card> discard;

    private CardState(List<Card> faceUpCards, Deck<Card> deck, SortedBag<Card> discard) {
        super(faceUpCards, deck.size(), discard.size());
        this.deck = deck;
        this.discard = discard;
    }

    /**
     * build a card state with five face-up cards, a deck and a discard given a deck
     *
     * @param deck (Deck<Card>) deck of cards from which faceUpCards and deck will be build
     * @throws IllegalArgumentException if the deck contains less than 5 cards
     * @return (CardState) an complete part of CardState with five faceUpCards and no discards
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);

        List<Card> faceUpCards = deck.topCards(Constants.FACE_UP_CARDS_COUNT).toList();

        return new CardState(faceUpCards, deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT), SortedBag.of());
    }

    /**
     * compute a card state where the slot-th card in the face_up cards has been replace by the first card in the deck
     *
     * @param slot (int) the index of the card that will be taken and
     * @throws IllegalArgumentException if the deck is empty
     * @throws IndexOutOfBoundsException if slot the negative or superior than four
     * @return (CardState) a new CardState with the slot-th faceUpCard replaced by the top deck card
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(!deck.isEmpty());
        Objects.checkIndex(slot, faceUpCards().size());

        //faceUpCards
        List<Card> faceUpCards = new ArrayList<>(faceUpCards());
        faceUpCards.remove(slot);
        faceUpCards.add(slot, deck.topCard());
        return new CardState(faceUpCards, deck.withoutTopCard(), discard);
    }

    /**
     * return the first Card of the deck
     * @throws IllegalArgumentException if the deck is empty
     * @return (Card) the top deck card
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!deck.isEmpty());
        return deck.topCard();
    }

    /**
     * return the same CardState without the first card (top card)
     * @throws IllegalArgumentException if the deck is empty
     * @return (CardState) a CardState without its top deck card
     */
    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!deck.isEmpty());
        return new CardState(faceUpCards(), deck.withoutTopCard(), discard);
    }

    /**
     * compute a new CardState where the deck is reform from the shuffled discard
     *
     * @param rng (Random) used to shuffle the deck
     * @throws IllegalArgumentException if the deck isn't empty
     * @return (CardState) a new CardState where the deck is reform discard
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(deck.isEmpty());
        return new CardState(faceUpCards(), Deck.of(discard, rng), SortedBag.of());
    }

    /**
     * return the same CardState with Cards added to the discard
     *
     * @param additionalDiscards (SortedBag<Card>)
     * @return (CardState) a new CardState with more discard cards
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        return new CardState(faceUpCards(), deck, discard.union(additionalDiscards));
    }

}

