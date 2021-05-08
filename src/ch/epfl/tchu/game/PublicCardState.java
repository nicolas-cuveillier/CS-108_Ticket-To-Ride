package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Objects;

/**
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 * <p>
 * represent the Card status which are known by players
 */
public class PublicCardState {
    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;

    /**
     * Constructor for a PublicCardState with face-up cards, the size of the deck and the discard
     *
     * @param faceUpCards  the list of cards that can be seen by players
     * @param deckSize     the size of the deck
     * @param discardsSize the size of the discard
     * @throws IllegalArgumentException if there aren't five face up cards
     *                                  if deck and discard are negative
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT && deckSize >= 0 && discardsSize >= 0);

        this.faceUpCards = List.copyOf(faceUpCards);
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
    }

    /**
     * Getter for the number of cards in play minus the cards heach player has in hand
     *
     * @return (int) the total number of cards
     */
    public int totalSize() {
        return faceUpCards.size() + discardsSize + deckSize;
    }

    /**
     * Getter for the face-up cards
     *
     * @return (List < Card >) a list of the faceUpCards
     */
    public List<Card> faceUpCards() {
        return faceUpCards;
    }

    /**
     * Getter for the face-up card at the index slot
     *
     * @return (List < Card >) the slot-th faceUpCards
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public Card faceUpCard(int slot) {
        return faceUpCards.get(Objects.checkIndex(slot, faceUpCards.size()));
    }

    /**
     * Getter for the deck's size
     *
     * @return (int) the deck's size
     */
    public int deckSize() {
        return deckSize;
    }

    /**
     * Tests whether the deck is empty or not
     *
     * @return (boolean) true iff the deck is empty
     */
    public boolean isDeckEmpty() {
        return (deckSize == 0);
    }

    /**
     * Getter for the discard's size
     *
     * @return (int) the discard's size
     */
    public int discardsSize() {
        return discardsSize;
    }

}
