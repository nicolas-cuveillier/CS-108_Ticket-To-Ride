package ch.epfl.tchu.game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public class PublicGameStateTest {
    @Test
    void checkConstructor(){
        int ticketsCountNeg = -1;
        int ticketsCountOk = 3;
        PublicCardState cardState = new PublicCardState(List.of(Card.BLACK,Card.BLACK,Card.BLACK,Card.BLACK,Card.BLACK),2,3);
        Map<PlayerId, PublicPlayerState> playerState = Map.of(PlayerId.PLAYER_1,new PublicPlayerState(2,2,List.of()),
                PlayerId.PLAYER_2,new PublicPlayerState(2,3,List.of()));

        Assertions.assertThrows(IllegalArgumentException.class, () ->
            new PublicGameState(ticketsCountNeg,cardState,PlayerId.PLAYER_1,playerState,null));
        //Assertions.assertThrows(IllegalArgumentException.class, () ->
                //new PublicGameState(ticketsCountOk,cardState,PlayerId.PLAYER_1,playerStateToBig,null));
    }
    @Test
    void checkCanDrawCards(){

    }
}
