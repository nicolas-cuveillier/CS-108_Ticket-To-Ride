package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Objects;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 * <p>
 * represent the Card status which are known by players
 */
public class PublicCardState {
    private List<Card> faceUpCards;
    private int deckSize;
    private int discardsSize;

    /**
     * Constructor for a PublicCardState with face-up cards, the size of the deck and the discard
     *
     * @param faceUpCards  (List<Card>)
     * @param deckSize     (int)
     * @param discardsSize (int)
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(faceUpCards.size() == 5 && deckSize >= 0 && discardsSize >= 0);
        this.faceUpCards = faceUpCards;
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
    }

    /**
     * give the size of all cards
     *
     * @return (int)
     */
    public int totalSize() {
        return faceUpCards.size() + discardsSize + deckSize;
    }

    /**
     * getter for the face-up cards
     *
     * @return (List < Card >)
     */
    public List<Card> faceUpCards() {
        return faceUpCards;
    }

    /**
     * getter for the face-up card at the index slot
     *
     * @return (List < Card >)
     */
    public Card faceUpCard(int slot) {
        return faceUpCards.get(Objects.checkIndex(slot, faceUpCards.size()));
    }

    /**
     * getter for the deck size
     *
     * @return (int)
     */
    public int deckSize() {
        return deckSize;
    }

    /**
     * return true iff the deck contains no cards
     *
     * @return (boolean)
     */
    public boolean isDeckEmpty() {
        return (deckSize == 0);
    }

    /**
     * getter for the discard size
     *
     * @return (int)
     */
    public int discardsSize() {
        return discardsSize;
    }

}
