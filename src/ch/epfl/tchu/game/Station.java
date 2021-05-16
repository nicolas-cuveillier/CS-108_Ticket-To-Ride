package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Represents a Station of the tchu game.
 * 
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public final class Station {

    private final int id;
    private final String name;

    /**
     * Primary constructor for a Station with it's unique id and it's name.
     *
     * @param id   unique id of the Station
     * @param name name of the Station
     * @throws IllegalArgumentException if Id is negative
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);

        this.id = id;
        this.name = name;
    }

    /**
     * Getter for the Station's id.
     *
     * @return (int) the id
     */
    public int id() {
        return id;
    }

    /**
     * Getter for the Station's name.
     *
     * @return (String) the name
     */
    public String name() {
        return name;
    }

    /**
     * @return the station's name
     */
    @Override
    public String toString() {
        return name;
    }

}
