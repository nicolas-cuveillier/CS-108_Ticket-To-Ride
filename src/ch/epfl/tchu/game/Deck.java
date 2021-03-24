package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
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
        this.cards = new ArrayList<>(cards);
    }

    /**
     * build a new deck with shuffled cards
     *
     * @param cards a SortedBag of cards that will be in the Deck
     * @param rng   the Random used to shuffle
     * @param <C>   the Type of the cards
     * @return (Deck<C>) a new deck of the shuffled list of cards
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        List<C> listOf = new ArrayList<>(cards.toList());
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
     * @see #topCards(int)
     * @return the first object of the deck
     */
    public C topCard() {
        return topCards(1).get(0);
    }

    /**
     * give a new deck without top card
     *
     * @see #topCards(int)
     * @return a new deck without the first object
     */
    public Deck<C> withoutTopCard() {
        return withoutTopCards(1);
    }

    /**
     * compute a new deck with only the count-th cards in it
     *
     * @param count the number of topCard(s) that will be returned
     * @throws IllegalArgumentException if count is negative or superior than cards' size
     * @return a new SortedBag with only a part of the cards
     */
    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= cards.size());
        SortedBag.Builder<C> builder = new SortedBag.Builder<>();

        for (int i = 0; i < count; i++) {
            builder.add(cards.get(i));
        }
        return builder.build();
    }

    /**
     * compute a new deck without certain cards (count)
     *
     * @param count the number of topCard(s) that will be removed
     * @throws IllegalArgumentException if count is negative or superior than cards' size
     * @return a new deck without a certain number(count) of cards
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= cards.size());
        List<C> listOfCards = new ArrayList<>(cards.size() - count);
        for (int i = count; i < cards.size(); i++) {
            listOfCards.add(cards.get(i));
        }
        return new Deck<>(listOfCards);
    }

}
