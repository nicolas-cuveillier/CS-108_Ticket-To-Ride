package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import javafx.util.StringConverter;

/**<h1>cardBagStringConverter</h1>
 * Implements a StringConverter for a SortedBag of cards.
 * 
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public final class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

    /**
     * Compute the textual representation of sortedBag of cards, namely, the human way to read it.
     *
     * @param cards the sortedBag of cards for which the textual representation will be computed
     * @return a String which is the textual representation of sortedBag of cards
     */
    @Override
    public String toString(SortedBag<Card> cards) {
        return Info.cardText(cards);
    }

    /**
     * @param string the textual representation of a sortedBag of cards
     * @return nothing
     * @throws UnsupportedOperationException that will always be thrown because this method won't be use in the game
     */
    @Override
    public SortedBag<Card> fromString(String string) {
        throw new UnsupportedOperationException();
    }
}
