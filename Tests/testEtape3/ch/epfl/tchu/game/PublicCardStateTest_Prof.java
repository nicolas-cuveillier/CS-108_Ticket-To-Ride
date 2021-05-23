package ch.epfl.tchu.game;

import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings(value = {"unused"})
class PublicCardStateTest_Prof {
    private static final List<Card> FACE_UP_CARDS =
            List.of(Card.BLUE, Card.BLACK, Card.ORANGE, Card.ORANGE, Card.RED);
    private static final Card topDeckCard = (Card) echo("topDeckCard",Card.ALL.get(new Random().nextInt(Card.ALL.size())));
    
    private static Object echo(String label, Object o) {
        System.out.println(label + ": " + o.toString());
        return o;
    }

    @Test
    void publicCardStateConstructorFailsWithInvalidNumberOfFaceUpCards() {
        for (int i = 0; i < 10; i++) {
            if (i == FACE_UP_CARDS.size())
                continue;

            var faceUpCards = new ArrayList<>(Collections.nCopies(i, Card.BLACK));
            assertThrows(IllegalArgumentException.class, () -> {
                new PublicCardState(faceUpCards, 0, 0, topDeckCard);
            });
        }
    }

    @Test
    void constructorFailsWithNegativeDeckOrDiscardsSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicCardState(FACE_UP_CARDS, -1, 0, topDeckCard);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicCardState(FACE_UP_CARDS, 0, -1, topDeckCard);
        });
    }

    @Test
    void constructorCopiesFaceUpCards() {
        var faceUpCards = new ArrayList<>(FACE_UP_CARDS);
        var cardState = new PublicCardState(faceUpCards, 0, 0, topDeckCard);
        faceUpCards.clear();
        assertEquals(FACE_UP_CARDS, cardState.faceUpCards());
    }

    @Test
    void faceUpCardsReturnsImmutableListOrCopy() {
        var cardState = new PublicCardState(FACE_UP_CARDS, 0, 0, topDeckCard);
        try {
            cardState.faceUpCards().clear();
        } catch (UnsupportedOperationException e) {
            // ignore
        }
        assertEquals(FACE_UP_CARDS, cardState.faceUpCards());
    }

    @Test
    void faceUpCardFailsWithInvalidSlotIndex() {
        var cardState = new PublicCardState(FACE_UP_CARDS, 0, 0, topDeckCard);
        for (int i = -20; i < 0; i++) {
            var slot = i;
            assertThrows(IndexOutOfBoundsException.class, () -> {
                cardState.faceUpCard(slot);
            });
        }
        for (int i = 6; i <= 20; i++) {
            var slot = i;
            assertThrows(IndexOutOfBoundsException.class, () -> {
                cardState.faceUpCard(slot);
            });
        }
    }

    @Test
    void faceUpCardReturnsCorrectCard() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            var cards = new ArrayList<>(Card.ALL);
            Collections.shuffle(cards, new Random(i * 2021L));
            var faceUpCards = List.copyOf(cards.subList(0, 5));
            var cardState = new PublicCardState(faceUpCards, 0, 0, topDeckCard);
            for (int j = 0; j < faceUpCards.size(); j++)
                assertEquals(faceUpCards.get(j), cardState.faceUpCard(j));
        }
    }

    @Test
    void deckSizeReturnsDeckSize() {
        for (int i = 0; i < 100; i++) {
            var cardState = new PublicCardState(FACE_UP_CARDS, i, i + 1, topDeckCard);
            assertEquals(i, cardState.deckSize());
        }
    }

    @Test
    void isDeckEmptyReturnsTrueOnlyWhenDeckEmpty() {
        assertTrue(new PublicCardState(FACE_UP_CARDS, 0, 1, topDeckCard).isDeckEmpty());
        for (int i = 0; i < 100; i++) {
            var cardState = new PublicCardState(FACE_UP_CARDS, i + 1, i, topDeckCard);
            assertFalse(cardState.isDeckEmpty());
        }
    }

    @Test
    void discardsSizeReturnsDiscardsSize() {
        for (int i = 0; i < 100; i++) {
            var cardState = new PublicCardState(FACE_UP_CARDS, i + 1, i, topDeckCard);
            assertEquals(i, cardState.discardsSize());
        }
    }
}