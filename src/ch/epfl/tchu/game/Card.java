package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 * <p>
 * Card that can be used in the tchu game :
 * {@link #BLACK},
 * {@link #VIOLET},
 * {@link #BLUE},
 * {@link #GREEN},
 * {@link #YELLOW},
 * {@link #ORANGE},
 * {@link #RED},
 * {@link #WHITE},
 * {@link #LOCOMOTIVE}.
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
     * Constructor instantiates a card with the given color
     *
     * @param color Color of the given card
     */
    Card(Color color) {
        this.color = color;
    }

    private Color color;

    /**
     * an unmodifiable list of all cards
     */
    public static final List<Card> ALL = List.of(Card.values());

    /**
     * the number of possible cards
     */
    public static final int COUNT = ALL.size();

    /**
     * an immutable list of all cards except the locomotive
     */
    public static final List<Card> CARS = List.of(Card.BLACK, Card.VIOLET, Card.BLUE, Card.GREEN, Card.YELLOW, Card.ORANGE, Card.RED, Card.WHITE);

    /**
     * Generic card instance which has the given color
     *
     * @param color the color of the card
     * @return the card according to it's linked color
     */
    public static Card of(Color color) {
        return CARS.get(color.ordinal());
    }

    /**
     * Getter for the private field color
     *
     * @return (Color) the card's color
     */
    public Color color() {
        return this.color;
    }
}
