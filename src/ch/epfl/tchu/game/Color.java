package ch.epfl.tchu.game;

import java.util.List;

public enum Color {
    BLACK,
    VIOLET,
    BLUE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    WHITE;

    public static List<Color> ALL = List.of(Color.values());
    public static int COUNT = ALL.size();

    }
