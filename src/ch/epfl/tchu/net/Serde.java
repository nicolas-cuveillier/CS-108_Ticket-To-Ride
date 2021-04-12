package ch.epfl.tchu.net;


import ch.epfl.tchu.SortedBag;

import java.util.*;
import java.util.function.Function;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public interface Serde<T> {

    String serialize(T obj);

    T deserialize(String message);

    static <T> Serde<T> of(Function<T, String> serializableFunc, Function<String, T> deserializableFunc){
        //TODO : change Object to something better
        return new Serde<>(){

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

    static <T> Serde<T> oneOf(List<T> list){
        //TODO : check
        return new Serde<>() {
            @Override
            public String serialize(T obj) {
                return Integer.toString(list.indexOf(obj));
            }

            @Override
            public T deserialize(String message) {
                return list.get(Integer.getInteger(message));
            }
        };
    }

//extends collection?
    static <T> Serde<List<T>> listOf(Serde<T> serde, String separator){
        //TODO : implement method
        return new Serde<>() {

            @Override
            public String serialize(List<T> obj) {
                StringJoiner joiner = new StringJoiner(separator);
                obj.forEach(i -> joiner.add(Integer.toString(obj.indexOf(i))));
                return joiner.toString();
            }

            @Override
            public List<T> deserialize(String message) {
                String[] t = message.split(separator,-1);
                List<T> list = new ArrayList<>();

                for (int i = 0; i < t.length; i++) {
                   //deserialise chaque sous element
                }
                return list;
            }
        };
    }

    static <T extends Comparable<T>> Serde<SortedBag<T>> oneOf(Serde<T> serde, String separator){
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
