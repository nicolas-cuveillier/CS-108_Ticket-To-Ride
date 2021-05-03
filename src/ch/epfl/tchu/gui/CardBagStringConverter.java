package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import javafx.util.StringConverter;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

    @Override
    public String toString(SortedBag<Card> cards) {
        return Info.cardText(cards);
    }

    @Override
    public SortedBag<Card> fromString(String string) {
        throw new UnsupportedOperationException();
    }
}
