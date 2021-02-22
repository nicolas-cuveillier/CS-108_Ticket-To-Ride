package ch.epfl.tchu.game;

import java.util.List;

public enum Card {

    BLACK(Color.BLACK),  //(wagon noir)
    VIOLET(Color.VIOLET),  //(wagon violet)
    BLUE(Color.BLUE),  //(wagon bleu)
    GREEN(Color.GREEN),  //(wagon vert)
    YELLOW(Color.YELLOW),  //(wagon jaune)
    ORANGE(Color.ORANGE),  //(wagon orange)
    RED(Color.RED),   //(wagon rouge)
    WHITE(Color.WHITE),  //(wagon blanc)
    LOCOMOTIVE(null);  //(locomotive)

    Card(Color color){
        this.color = color;
    }

    public Color color; //private?
    public static List<Card> ALL = List.of(Card.values());
    public static int COUNT = ALL.size();
    public final static List<Card> CARS = List.of(Card.BLACK,Card.VIOLET,Card.BLUE,Card.GREEN,Card.YELLOW,Card.ORANGE,Card.RED,Card.WHITE);

    public static Card of(Color color){
       return CARS.get(color.ordinal());
    }

    public Color color(){
        return null;// à completer
    }
}
