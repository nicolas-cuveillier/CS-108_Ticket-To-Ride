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
        return new Serde<>() {
            @Override
            public String serialize(T obj) {
                return Integer.toString(list.indexOf(obj));
            }

            @Override
            public T deserialize(String message) {
                return list.get(Integer.parseInt(message));
            }
        };
        //TODO : check
    }

    //extends collection?
    static <T> Serde<List<T>> listOf(Serde<T> serde, char separator) {
        //TODO : check
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
                for (String s : t) {
                    list.add(serde.deserialize(s));
                }
                return list;
            }
        };
    }

    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, char separator) {
        //TODO : implement methods
        String s = Character.toString(separator);
        return new Serde<>() {

            @Override
            public String serialize(SortedBag<T> obj) {
                StringJoiner joiner = new StringJoiner(s);
                obj.forEach(i -> joiner.add(serde.serialize(i)));
                return joiner.toString();
            }

            @Override
            public SortedBag<T> deserialize(String message) {
                String[] t = message.split(Pattern.quote(s), -1);
                SortedBag.Builder<T> builder = new SortedBag.Builder<>();
                for (String s : t) {
                    builder.add(serde.deserialize(s));
                }
                return builder.build();
            }
        };
    }

}
