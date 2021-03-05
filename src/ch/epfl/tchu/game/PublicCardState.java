package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Objects;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public class PublicCardState {
    private List<Card> faceUpCards;
    private int deckSize;
    private int discardsSize;

    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(faceUpCards.size() == 5 && deckSize >= 0 && discardsSize >= 0);
        this.faceUpCards = faceUpCards;
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
    }

    public int totalSize() {
        return faceUpCards.size() + discardsSize + deckSize;
    }

    public List<Card> faceUpCards() {
        return faceUpCards;
    }

    public Card faceUpCard(int slot) {
        return faceUpCards.get(Objects.checkIndex(slot, faceUpCards.size()));
    }

    public int deckSize() {
        return deckSize;
    }

    public boolean isDeckEmpty() {
        return (deckSize == 0);
    }

    public int discardsSize() {
        return discardsSize;
    }

}
