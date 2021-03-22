package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 * <p>
 * Simulate a Station from the tchu game
 */
public final class Station {

    private final int id;
    private final String name;

    /**
     * principal constructor for building a Station with an unique id and its name
     *
     * @param id   (int) unique id of the Station
     * @param name (String) name of the Station
     * @throws IllegalArgumentException if Id is negative
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);
        this.id = id;
        this.name = name;
    }

    /**
     * getter for the station's id
     *
     * @return (int) the id
     */
    public int id() {
        return id;
    }

    /**
     * getter for the station's name
     *
     * @return (String) the name
     */
    public String name() {
        return name;
    }

    /**
     * {@inheritDoc}
     *
     * @return the name
     */
    @Override
    public String toString() {
        return name;
    }

}
