package ch.epfl.tchu.net;


import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * <h1>Serde</h1>
 * Implements generic methods to serialize and deserialize the components of the game.
 *
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public interface Serde<T> {

    /**
     * Serialize an object as a string of characters.
     *
     * @param obj the object of type T that will be serialize
     * @return a string representation of the object
     */
    String serialize(T obj);

    /**
     * Deserialize a string of characters in an object. Do the inverse of the serialize method.
     *
     * @param message the string which represent the the object of type T
     * @return an object of type T according to its serialized representation
     */
    T deserialize(String message);

    /**
     * Static method that build a Serde able to (de)serialize object of parameter < T > according to function.
     *
     * @param serializableFunc   the function that will serialize the object
     * @param deserializableFunc the function that will deserialize the object
     * @param <T>                the type of the object that will be (de)serialized
     * @return a Serde which is able to (de)serialize basic object according to the functions
     */
    static <T> Serde<T> of(Function<T, String> serializableFunc, Function<String, T> deserializableFunc) {
        return new Serde<>() {
            @Override
            public String serialize(T obj) {
                return serializableFunc.apply(obj);
            }

            @Override
            public T deserialize(String message) {
                return deserializableFunc.apply(message);
            }
        };
    }

    /**
     * Static method that build a Serde able to (de)serialize one object in the list of parameter < T >.
     *
     * @param list list of the objects that will potentially be (de)serialize
     * @param <T>  the type of the elements that compose the list
     * @return a Serde able to (de)serialize object from the list according to its index in it
     */
    static <T> Serde<T> oneOf(List<T> list) {
        List<T> copy = List.copyOf(list);
        return Serde.of(i -> Integer.toString(copy.indexOf(i)), s -> copy.get(Integer.parseInt(s)));
    }

    /**
     * Static method that build a Serde able to (de)serialize a whole list of parameter < T > according
     * to the way of (de)serialize each object.
     *
     * @param serde     Serde that can (de)serialize object of type T
     * @param separator character that will separate the different elements of the list
     * @param <T>       the type of the elements that can be (de)serialize by the Serde
     * @return a Serde able to (de)serialize a whole list of objects at once
     */
    static <T> Serde<List<T>> listOf(Serde<T> serde, char separator) {
        String s = Character.toString(separator);
        return new Serde<>() {
            @Override
            public String serialize(List<T> obj) {
                StringJoiner joiner = new StringJoiner(s);
                obj.forEach(i -> joiner.add(serde.serialize(i)));
                return joiner.toString();
            }

            @Override
            public List<T> deserialize(String message) {
                String[] t = message.split(Pattern.quote(s), -1);
                List<T> list = new ArrayList<>();
                if (message.equals("")) return list;
                Arrays.asList(t).forEach(i -> list.add(serde.deserialize(i)));
                return list;
            }
        };
    }

    /**
     * Static method that build a Serde able to (de)serialize a whole SortedBag of parameter < T > according
     * to the way of (de)serialize each object and actually using the way to (de)serialize a list.
     *
     * @param serde     Serde that can (de)serialize object of type T
     * @param separator character that will separate the different elements of the list
     * @param <T>       the type of the elements that can be (de)serialize by the Serde
     * @return a Serde able to (de)serialize a whole SortedBag of objects at once
     */
    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, char separator) {
        Serde<List<T>> serdeList = Serde.listOf(serde, separator);
        return new Serde<>() {
            @Override
            public String serialize(SortedBag<T> obj) {
                return serdeList.serialize(obj.toList());
            }

            @Override
            public SortedBag<T> deserialize(String message) {
                return SortedBag.of(serdeList.deserialize(message));
            }
        };
    }
}
