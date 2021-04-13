package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public class DeckTest {
    @Test
    void checkOf() {
        SortedBag.Builder<Card> b = new SortedBag.Builder<>();
        b.add(Card.BLACK).add(Card.LOCOMOTIVE).add(2, Card.ORANGE);
        SortedBag<Card> cards = b.build();
        Deck<Card> deck = Deck.of(cards, TestRandomizer.newRandom());
        Assertions.assertFalse(Card.BLUE.equals(deck.topCard()));
    }

    @Test
    void checkWithoutTopCard() {
        SortedBag.Builder<Card> b = new SortedBag.Builder<>();
        b.add(Card.BLUE).add(Card.LOCOMOTIVE).add(2, Card.ORANGE);
        SortedBag<Card> cards = b.build();
        Deck<Card> deck = Deck.of(cards, TestRandomizer.newRandom());
        Deck<Card> deckWithOutTopCard = deck.withoutTopCard();
        cards = deckWithOutTopCard.topCards(3);

        SortedBag.Builder<Card> b2 = new SortedBag.Builder<>();
        b2.add(Card.LOCOMOTIVE).add(2, Card.ORANGE);
        SortedBag<Card> cards2 = b2.build();

        Assertions.assertFalse(deckWithOutTopCard.equals(deck.withoutTopCard()));
        Assertions.assertTrue(cards2.contains(cards));

    }

    @Test
    void checkWithoutTopCards() {
        SortedBag.Builder<Card> b = new SortedBag.Builder<>();
        b.add(Card.BLUE).add(Card.LOCOMOTIVE).add(2, Card.ORANGE);
        SortedBag<Card> cards = b.build();
        Deck<Card> deck = Deck.of(cards, TestRandomizer.newRandom());
        Deck<Card> deckWithOutTopCards = deck.withoutTopCards(2);
        cards = deckWithOutTopCards.topCards(2);

        SortedBag.Builder<Card> b2 = new SortedBag.Builder<>();
        b2.add(Card.LOCOMOTIVE).add(Card.ORANGE);
        SortedBag<Card> cards2 = b2.build();

        Assertions.assertFalse(deckWithOutTopCards.equals(deck.withoutTopCards(2)));
        Assertions.assertTrue(cards2.contains(cards));

    }

    @Test
    void checkException() {
        SortedBag.Builder<Card> b = new SortedBag.Builder<>();
        b.add(Card.BLUE).add(Card.LOCOMOTIVE).add(2, Card.ORANGE);
        SortedBag<Card> cards = b.build();
        Deck<Card> deck = Deck.of(cards, TestRandomizer.newRandom());
        //empty deck
        SortedBag.Builder<Card> b2 = new SortedBag.Builder<>();
        SortedBag<Card> cards2 = b2.build();
        Deck<Card> deck2 = Deck.of(cards2, TestRandomizer.newRandom());

        Assertions.assertThrows(IllegalArgumentException.class, () -> deck.topCards(-2));
        Assertions.assertThrows(IllegalArgumentException.class, () -> deck.topCards(5));
        Assertions.assertThrows(IllegalArgumentException.class, () -> deck.withoutTopCards(5));
        Assertions.assertThrows(IllegalArgumentException.class, () -> deck.withoutTopCards(-5));
        Assertions.assertThrows(IllegalArgumentException.class, () -> deck2.withoutTopCard());
        Assertions.assertThrows(IllegalArgumentException.class, () -> deck2.topCard());

    }

    @Test
    void checkIsEmpty() {
        //empty deck
        SortedBag.Builder<Card> b2 = new SortedBag.Builder<>();
        SortedBag<Card> cards2 = b2.build();
        Deck<Card> deck2 = Deck.of(cards2, TestRandomizer.newRandom());

        Assertions.assertTrue(deck2.isEmpty());
        Assertions.assertEquals(0, deck2.size());
    }


}
