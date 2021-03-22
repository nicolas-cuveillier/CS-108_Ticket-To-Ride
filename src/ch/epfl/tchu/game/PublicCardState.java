package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
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
     * @param faceUpCards  (List<Card>)
     * @param deckSize     (int)
     * @param discardsSize (int)
     * @throws IllegalArgumentException if there aren't five face up cards
     *                                  if deck and discard are negative
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT && deckSize >= 0 && discardsSize >= 0);
        this.faceUpCards = new ArrayList<>(faceUpCards);
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
    }

    /**
     * give the size of all cards
     *
     * @return (int) the total number of cards
     */
    public int totalSize() {
        return faceUpCards.size() + discardsSize + deckSize;
    }

    /**
     * getter for the face-up cards
     *
     * @return (List < Card >) a list of the faceUpCards
     */
    public List<Card> faceUpCards() {
        return Collections.unmodifiableList(faceUpCards);
    }

    /**
     * getter for the face-up card at the index slot
     *
     * @return (List < Card >) the slot-th faceUpCards
     */
    public Card faceUpCard(int slot) {
        return faceUpCards.get(Objects.checkIndex(slot, faceUpCards.size()));
    }

    /**
     * getter for the deck's size
     *
     * @return (int) the deck's size
     */
    public int deckSize() {
        return deckSize;
    }

    /**
     * return true iff the deck contains no cards
     *
     * @return (boolean) true iff the deck is empty
     */
    public boolean isDeckEmpty() {
        return (deckSize == 0);
    }

    /**
     * getter for the discard's size
     *
     * @return (int) the discard's size
     */
    public int discardsSize() {
        return discardsSize;
    }

}
