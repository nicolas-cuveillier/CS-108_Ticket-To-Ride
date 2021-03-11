package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

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

    private CardState(List<Card> faceUpCards, int deckSize, int discardsSize, Deck<Card> deck, SortedBag<Card> discard) {
        super(faceUpCards, deckSize, discardsSize);
        this.deck = deck;
        this.discard = discard;
    }

    /**
     * build a card state with five face-up cards, a deck and a discard given a deck
     *
     * @param deck (Deck<Card>)
     * @return (CardState)
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= 5);

        List<Card> faceUpCards = deck.topCards(5).toList();

        SortedBag.Builder<Card> discardBuilder = new SortedBag.Builder<>();
        deck = deck.withoutTopCards(5);

        return new CardState(faceUpCards, deck.size() , 0, deck, discardBuilder.build());
    }

    /**
     * compute a card state where the slot-th card in the face_up cards has been replace by the first card in the deck
     *
     * @param slot (int)
     * @return (CardState)
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(!deck.isEmpty());
        Objects.checkIndex(slot, faceUpCards().size());

        //faceUpCards
        faceUpCards().remove(slot);
        faceUpCards().add(slot, deck.topCard());
        List<Card> newFaceUpCards = List.copyOf(faceUpCards());
        return new CardState(newFaceUpCards, deck.withoutTopCard().size(), discardsSize() + 1, deck.withoutTopCard(), discard);
    }

    /**
     * return the first Card of the deck
     *
     * @return (Card)
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!deck.isEmpty());
        return deck.topCard();
    }

    /**
     * return the same CardState without the first card (top card)
     *
     * @return (CardState)
     */
    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!deck.isEmpty());
        return new CardState(faceUpCards(), deck.withoutTopCard().size(), discardsSize(), deck.withoutTopCard(), discard);
    }

    /**
     * compute a new CardState where the deck is reform from the shuffled discard
     *
     * @param rng (Random)
     * @return (CardState)
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(deck.isEmpty());

        SortedBag.Builder<Card> discardBuilder = new SortedBag.Builder<>();

        return new CardState(faceUpCards(), discard.size(), 0, Deck.of(discard, rng), discardBuilder.build());
    }

    /**
     * return the same CardState with Cards added to the discard
     *
     * @param additionalDiscards (SortedBag<Card>)
     * @return (CardState)
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        SortedBag.Builder<Card> discardBuilder = new SortedBag.Builder<>();
        List<Card> discardList = discard.toList();
        List<Card> additionalDiscardsList = additionalDiscards.toList();
        discardList.addAll(additionalDiscardsList);
        for (Card c : discardList) {
            discardBuilder.add(c);
        }
        SortedBag<Card> moreDiscard = discardBuilder.build();
        return new CardState(faceUpCards(), deck.size(), moreDiscard.size(), deck, moreDiscard);
    }

}

