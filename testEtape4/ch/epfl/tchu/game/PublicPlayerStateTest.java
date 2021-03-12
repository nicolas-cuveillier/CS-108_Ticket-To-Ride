package ch.epfl.tchu.game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public class PublicPlayerStateTest {
    @Test
    void checkException(){
        Assertions.assertThrows(IllegalArgumentException.class,()-> new PublicPlayerState(-2,2, List.of()));
        Assertions.assertThrows(IllegalArgumentException.class,()-> new PublicPlayerState(2,-2, List.of()));
    }
    @Test
    void checkCarCount(){
       PublicPlayerState p = new PublicPlayerState(2,22,
               List.of(new Route("test",new Station(0,"From"),new Station(1,"To"),4, Route.Level.OVERGROUND,Color.BLACK)));
       Assertions.assertEquals(36,p.carCount());
    }
    @Test
    void checkClaimPoints(){}

}
