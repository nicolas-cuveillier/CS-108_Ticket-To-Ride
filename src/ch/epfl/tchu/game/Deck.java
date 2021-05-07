package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @param <C> the type of elements in the Deck
 * @author Grégory Preisig & Nicolas Cuveillier
 * <p>
 * Generic class to implement the notion of deck in the game
 */
public final class Deck<C extends Comparable<C>> {
    private final List<C> cards;

    private Deck(List<C> cards) {
        this.cards = new ArrayList<>(cards);
    }

    /**
     * Generic deck instance with shuffled cards
     *
     * @param cards a SortedBag of cards that will be in the Deck
     * @param rng   the Random used to shuffle
     * @param <C>   the Type of the cards
     * @return (Deck < C >) a new deck of the shuffled list of cards
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        List<C> listOf = cards.toList();
        Collections.shuffle(listOf, rng);
        return new Deck<>(listOf);
    }

    /**
     * Getter for the sortedBag's size
     *
     * @return the cards' size
     */
    public int size() {
        return cards.size();
    }

    /**
     * IsEmpty method
     *
     * @return true iff the sortedBag is empty
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Getter for the deck's top card
     *
     * @return the first object of the deck
     * @see #topCards(int)
     */
    public C topCard() {
        Preconditions.checkArgument(!cards.isEmpty());
        return cards.get(0);
    }

    /**
     * Getter for the deck without it's top card
     *
     * @return a new deck without the first object
     * @see #topCards(int)
     */
    public Deck<C> withoutTopCard() {
        return withoutTopCards(1);
    }

    /**
     * Getter for the deck's specified top cards
     *
     * @param count the number of topCard(s) that will be returned
     * @return a new SortedBag with the deck's specified top cards
     * @throws IllegalArgumentException if count is negative or superior than cards' size
     */
    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= cards.size());
        return SortedBag.of(cards.subList(0, count));
    }

    /**
     * Getter for the deck without the count top cards
     *
     * @param count the number of topCard(s) that will be removed
     * @return the deck with the number (count) of cards removed from it's top
     * @throws IllegalArgumentException if count is negative or superior than cards' size
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= cards.size());
        return new Deck<>(cards.subList(count, cards.size()));
    }

}
