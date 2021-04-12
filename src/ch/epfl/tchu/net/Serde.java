package ch.epfl.tchu.net;


import ch.epfl.tchu.SortedBag;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public interface Serde<T> {

    String serialize(T obj);

    T deserialize(String message);

    static <T> Serde<T> of(Function<T, String> serializableFunc, Function<String, T> deserializableFunc) {
        //TODO : change Object to something better
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

    static <T> Serde<T> oneOf(List<T> list) {
        return (Serde<T>) Serde.of(i -> Integer.toString(list.indexOf(i)), Integer::parseInt);
        //TODO : check
    }

    //extends collection?
    static <T> Serde<List<T>> listOf(Serde<T> serde, String separator) {
        //TODO : check
        return new Serde<>() {

            @Override
            public String serialize(List<T> obj) {
                StringJoiner joiner = new StringJoiner(separator);
                obj.forEach(i -> joiner.add(Integer.toString(obj.indexOf(i))));
                return joiner.toString();
            }

            @Override
            public List<T> deserialize(String message) {
                String[] t = message.split(Pattern.quote(separator), -1);
                List<T> list = new ArrayList<>();
                for (String s : t) {
                    list.add(serde.deserialize(s));
                }
                return list;
            }
        };
    }

    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, String separator) {
        //TODO : implement methods
        return new Serde<>() {

            @Override
            public String serialize(SortedBag<T> obj) {
                return null;
            }

            @Override
            public SortedBag<T> deserialize(String message) {
                return null;
            }
        };
    }

}
