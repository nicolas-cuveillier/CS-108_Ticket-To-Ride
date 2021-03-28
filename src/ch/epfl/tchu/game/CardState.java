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
 * Represents the Card status which are not possessed by any of the players
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
     * Generic CardState instance with five face-up cards, a deck and an empty discard set using the given deck
     *
     * @param deck (Deck<Card>) deck of cards from which faceUpCards and deck will be built
     * @throws IllegalArgumentException if the deck contains less than 5 cards
     * @return (CardState) a complete part of CardState with five faceUpCards and an empty discard set
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);

        List<Card> faceUpCards = deck.topCards(Constants.FACE_UP_CARDS_COUNT).toList();

        return new CardState(faceUpCards, deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT), SortedBag.of());
    }

    /**
     * Computes a card state where the card in the faceUpCards at the given slot has been replaced by the first card in the deck
     *
     * @param slot (int) the index of the card's slot which will be replaced
     * @throws IllegalArgumentException if the deck is empty
     * @throws IndexOutOfBoundsException if slot the negative or superior than four
     * @return (CardState) a new CardState with the card in faceUpCards at the given slot was replaced by the top deck card
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
     * Returns the first Card of the deck
     * @throws IllegalArgumentException if the deck is empty
     * @return (Card) the top deck card
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!deck.isEmpty());
        return deck.topCard();
    }

    /**
     * Returns the CardState with the same deck minus the first card (top card)
     *
     * @throws IllegalArgumentException if the deck is empty
     * @return (CardState) a CardState without it's deck's top card
     */
    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!deck.isEmpty());
        return new CardState(faceUpCards(), deck.withoutTopCard(), discard);
    }

    /**
     * Computes a new CardState where the deck is formed using the shuffled discarded cards
     *
     * @param rng (Random) used to shuffle the deck
     * @throws IllegalArgumentException if the deck isn't empty
     * @return (CardState) a new CardState where the deck is the shuffled discard
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(deck.isEmpty());
        return new CardState(faceUpCards(), Deck.of(discard, rng), SortedBag.of());
    }

    /**
     * return the CardState with Cards added to the discard
     *
     * @param additionalDiscards (SortedBag<Card>)
     * @return (CardState) a new CardState with the discarded cards
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        return new CardState(faceUpCards(), deck, discard.union(additionalDiscards));
    }

}

