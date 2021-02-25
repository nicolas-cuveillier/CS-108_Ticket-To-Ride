package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 * <p>
 * Card that can be used in the tchu game
 * {@link #BLACK}
 * {@link #VIOLET}
 * {@link #BLUE
 * {@link #GREEN}
 * {@link #YELLOW}
 * {@link #ORANGE}
 * {@link #RED}
 * {@link #WHITE}
 * {@link #LOCOMOTIVE}
 */
public enum Card {

    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null);

    /**
     * constructor, here for building the card with a color attach
     *
     * @param c (Color)
     */
    Card(Color c) {
        this.color = c;
    }

    private Color color;
    /**
     * List of all cards
     */
    public static final List<Card> ALL = List.of(Card.values());
    /**
     * number of possible cards
     */
    public static final int COUNT = ALL.size();
    /**
     * List of all cards except the locomotive
     */
    public static final List<Card> CARS = List.of(Card.BLACK, Card.VIOLET, Card.BLUE, Card.GREEN, Card.YELLOW, Card.ORANGE, Card.RED, Card.WHITE);

    /**
     * @param color (Color)
     * @return the card according to its linked color
     */
    public static Card of(Color color) {
        return CARS.get(color.ordinal());
    }

    /**
     * @return (Color) the card's color
     */
    public Color color() {
        return this.color;
    }
}
