package ch.epfl.tchu.game;

import java.util.List;

/**<h1>Color</h1>
 * Colors that can be used in the game :
 * {@link #BLACK},
 * {@link #VIOLET},
 * {@link #BLUE},
 * {@link #GREEN},
 * {@link #YELLOW},
 * {@link #ORANGE},
 * {@link #RED},
 * {@link #WHITE}.
 * 
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
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
     * An unmodifiable ist of all the possible colors.
     */
    public static final List<Color> ALL = List.of(values());
    /**
     * The number of possible colors.
     */
    public static final int COUNT = ALL.size();

}
