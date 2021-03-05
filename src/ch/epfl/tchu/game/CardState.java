package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public final class CardState extends PublicCardState {
    private final Deck<Card> deck;
    private final SortedBag<Card> discard;

    private CardState(List<Card> faceUpCards, int deckSize, int discardsSize, Deck<Card> deck, SortedBag<Card> discard) {
        super(faceUpCards, deckSize, discardsSize);
        this.deck = deck;
        this.discard = discard;
    }

    public static CardState of(Deck<Card> deck) {
        List<Card> faceUpCards = deck.topCards(5).toList();
        SortedBag.Builder<Card> discardBuilder = new SortedBag.Builder<>();

        return new CardState(faceUpCards, deck.size() - 5, 0, deck.withoutTopCards(5), discardBuilder.build());
    }

    public CardState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(!deck.isEmpty());
        Objects.checkIndex(slot, faceUpCards().size());

        //discard
        List<Card> discardCards = discard.toList();
        discardCards.add(faceUpCard(slot));
        SortedBag.Builder<Card> discardBuilder = new SortedBag.Builder<>();
        for (Card c : discardCards) {
            discardBuilder.add(c);
        }
        //faceUpCards
        faceUpCards().remove(slot);
        faceUpCards().add(slot, deck.topCard());
        List<Card> newFaceUpCards = List.copyOf(faceUpCards());
        return new CardState(newFaceUpCards, deck.withoutTopCard().size(), discardsSize() + 1, deck.withoutTopCard(), discardBuilder.build());
    }

    public Card topDeckCard() {
        Preconditions.checkArgument(!deck.isEmpty());
        return deck.topCard();
    }

    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!deck.isEmpty());
        return new CardState(faceUpCards(), deck.withoutTopCard().size(), discardsSize(), deck.withoutTopCard(), discard);
    }

    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(deck.isEmpty());

        SortedBag.Builder<Card> discardBuilder = new SortedBag.Builder<>();

        return new CardState(faceUpCards(), discard.size(), 0, Deck.of(discard, rng), discardBuilder.build());
    }

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

