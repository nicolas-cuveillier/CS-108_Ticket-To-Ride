package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 * <p>
 * Color that can be used in the tchu game :
 * {@link #BLACK},
 * {@link #VIOLET},
 * {@link #BLUE},
 * {@link #GREEN},
 * {@link #YELLOW},
 * {@link #ORANGE},
 * {@link #RED},
 * {@link #WHITE}.
 */
public enum Color {
    BLACK,
    VIOLET,
    BLUE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    WHITE;
    /**
     * return an unmodifiable ist of all the possible colors
     */
    public static final List<Color> ALL = List.of(Color.values());
    /**
     * return the number of possible colors
     */
    public static final int COUNT = ALL.size();

}
