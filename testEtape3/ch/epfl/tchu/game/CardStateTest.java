package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public class CardStateTest {

    private static Deck<Card> createNormalDeck() {
        SortedBag.Builder<Card> b = new SortedBag.Builder<>();
        b.add(Card.BLACK).add(Card.LOCOMOTIVE).add(2, Card.ORANGE).add(Card.WHITE).add(Card.BLUE);
        SortedBag<Card> cards = b.build();
        Deck<Card> deck = Deck.of(cards, TestRandomizer.newRandom());
        return deck;
    }

    private static Deck<Card> createNotNormalDeck() {
        SortedBag.Builder<Card> b2 = new SortedBag.Builder<>();
        b2.add(Card.LOCOMOTIVE).add(2, Card.ORANGE);
        SortedBag<Card> cards2 = b2.build();
        Deck<Card> deck2 = Deck.of(cards2, TestRandomizer.newRandom());
        return deck2;
    }

    @Test
    void checkOfMethod() {
        //deck1
        CardState c = CardState.of(createNormalDeck());
        //deck2
        Deck<Card> deck2 = createNotNormalDeck();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> CardState.of(deck2));
        Assertions.assertTrue(c.faceUpCards().containsAll(List.of(Card.BLACK, Card.LOCOMOTIVE, Card.ORANGE, Card.ORANGE, Card.BLUE)));
        Assertions.assertEquals(0, c.discardsSize());
        Assertions.assertEquals(1, c.deckSize());
    }

    @Test
    void checkWithDrawnFaceUpCard() {
        //deck1
        CardState c = CardState.of(createNormalDeck());
        //deck2
        SortedBag.Builder<Card> b2 = new SortedBag.Builder<>();
        b2.add(Card.BLACK).add(Card.LOCOMOTIVE).add(Card.ORANGE).add(Card.WHITE).add(Card.BLUE);
        SortedBag<Card> faceUpCards2 = b2.build();
        Deck<Card> deck2 = Deck.of(faceUpCards2, TestRandomizer.newRandom());
        CardState c2 = CardState.of(deck2);

        Assertions.assertEquals(faceUpCards2.toList(), c.withDrawnFaceUpCard(3).faceUpCards());
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> c.withDrawnFaceUpCard(-5));
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> c.withDrawnFaceUpCard(5));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> c2.withDrawnFaceUpCard(2));

    }

    @Test
    void checkTopDeckCard() {
        //deck1
        CardState c = CardState.of(createNormalDeck());
        //deck2
        SortedBag.Builder<Card> b2 = new SortedBag.Builder<>();
        b2.add(Card.BLACK).add(Card.LOCOMOTIVE).add(Card.ORANGE).add(Card.WHITE).add(Card.BLUE);
        SortedBag<Card> faceUpCards2 = b2.build();
        Deck<Card> deck2 = Deck.of(faceUpCards2, TestRandomizer.newRandom());
        CardState c2 = CardState.of(deck2);

        Assertions.assertEquals(Card.WHITE, c.topDeckCard());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> c2.topDeckCard());
    }

}
