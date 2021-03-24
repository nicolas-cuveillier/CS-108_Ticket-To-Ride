package ch.epfl.tchu.game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.Map;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public class PublicGameStateTest {
    private static PublicGameState normalPublicState(){
        int ticketsCountOk = 3;

        PublicCardState cardState = new PublicCardState(List.of(Card.BLACK,Card.BLACK,Card.BLACK,Card.BLACK,Card.BLACK),3,3);
        Map<PlayerId, PublicPlayerState> playerState = Map.of(PlayerId.PLAYER_1,new PublicPlayerState(2,2,List.of()),
                PlayerId.PLAYER_2,new PublicPlayerState(2,3,List.of()));
        PublicGameState p = new PublicGameState(ticketsCountOk,cardState,PlayerId.PLAYER_1,playerState,null);
        return p;
    }
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
        int ticketsCountOk = 3;

        PublicCardState cardState = new PublicCardState(List.of(Card.BLACK,Card.BLACK,Card.BLACK,Card.BLACK,Card.BLACK),1,3);
        Map<PlayerId, PublicPlayerState> playerState = Map.of(PlayerId.PLAYER_1,new PublicPlayerState(2,2,List.of()),
                PlayerId.PLAYER_2,new PublicPlayerState(2,3,List.of()));
        PublicGameState p = new PublicGameState(ticketsCountOk,cardState,PlayerId.PLAYER_1,playerState,null);

        Assertions.assertFalse(p.canDrawCards());
        Assertions.assertTrue(p.canDrawTickets());
    }
    @Test
    void checkCurrentPlayerId(){
        PublicGameState p = normalPublicState();
        Assertions.assertEquals(PlayerId.PLAYER_1,p.currentPlayerId());
    }
    @Test
    void checkPlayerState(){
        PublicGameState p = normalPublicState();
        Assertions.assertEquals(2,p.playerState(PlayerId.PLAYER_1).cardCount());
    }

    @Test
    void checkClaimedRoute(){

        int ticketsCountOk = 3;
        PublicCardState cardState = new PublicCardState(List.of(Card.BLACK,Card.BLACK,Card.BLACK,Card.BLACK,Card.BLACK),1,3);
        Map<PlayerId, PublicPlayerState> playerState = Map.of(PlayerId.PLAYER_1,new PublicPlayerState(2,2,ChMap.routes().subList(0,3)),
                PlayerId.PLAYER_2,new PublicPlayerState(2,3,ChMap.routes().subList(3,8)));
        PublicGameState p = new PublicGameState(ticketsCountOk,cardState,PlayerId.PLAYER_1,playerState,null);
        Assertions.assertEquals(ChMap.routes().subList(0,8),p.claimedRoutes());
    }

}
