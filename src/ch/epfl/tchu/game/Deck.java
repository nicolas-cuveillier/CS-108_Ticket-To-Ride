package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public final class Deck<C extends Comparable<C>> {
    private final List<C> cards;

    private Deck(List<C> cards) {
        this.cards = cards;
    }

    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        List<C> listOf = cards.toList();
        Collections.shuffle(listOf, rng);
        return new Deck<>(listOf);
    }

    public int size() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public C topCard() {
        Preconditions.checkArgument(!cards.isEmpty());
        return cards.get(0);
    }

    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(!cards.isEmpty());
        return new Deck<>(cards.subList(1, cards.size()));
    }

    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= cards.size());
        SortedBag.Builder<C> builder = new SortedBag.Builder<>();

        for (int i = 0; i < count; i++) {
            builder.add(1, cards.get(i));
        }
        return builder.build();
    }

    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= cards.size());
        SortedBag.Builder<C> builder = new SortedBag.Builder<>();

        for (int i = count; i < cards.size(); i++) {
            builder.add(1, cards.get(i));
        }
        return new Deck<>(builder.build().toList());
    }

}
