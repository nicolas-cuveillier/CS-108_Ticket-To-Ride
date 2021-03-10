package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 * <p>
 * generic class to implement the notion of deck in the game
 */
public final class Deck<C extends Comparable<C>> {
    private final List<C> cards;

    private Deck(List<C> cards) {
        this.cards = cards;
    }

    /**
     * build a new deck with shuffled cards
     *
     * @param cards (SortedBag<C>)
     * @param rng   (Random)
     * @param <C>   (<C>)
     * @return
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        List<C> listOf = cards.toList();
        Collections.shuffle(listOf, rng);
        return new Deck<>(listOf);
    }

    /**
     * getter for the sortedBag's size
     *
     * @return the cards' size
     */
    public int size() {
        return cards.size();
    }

    /**
     * @return true iff the sortedBag is empty
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * getter for the first object
     *
     * @return the first object of the deck
     */
    public C topCard() {
        Preconditions.checkArgument(!cards.isEmpty());
        return cards.get(0);
    }

    /**
     * give a new deck without top card
     *
     * @return a new deck without the first object
     */
    public Deck<C> withoutTopCard() {
        return withoutTopCards(1);
    }

    /**
     * compute a new deck with only the count-th cards in it
     *
     * @param count
     * @return a new SortedBag with only a part of the cards
     */
    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= cards.size());
        SortedBag.Builder<C> builder = new SortedBag.Builder<>();

        for (int i = 0; i < count; i++) {
            builder.add(1, cards.get(i));
        }
        return builder.build();
    }

    /**
     * compute a new deck without certain cards (count)
     *
     * @param count
     * @return a new deck without a certain number(count) of cards
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= cards.size());
        SortedBag.Builder<C> builder = new SortedBag.Builder<>();

        for (int i = count; i < cards.size(); i++) {
            builder.add(1, cards.get(i));
        }
        return new Deck<>(builder.build().toList());
    }

}
