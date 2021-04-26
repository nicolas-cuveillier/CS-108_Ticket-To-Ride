package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.ReadOnlyDoubleProperty;

import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public final class ObservableGameState {
    private PublicGameState publicGameState;
    private PlayerState playerState;

    public ObservableGameState(PlayerId playerId){

    }

    public void setState(PublicGameState gameState, PlayerState playerState){
    }

    //not sure it is good but needed for CardsViewCreator
    public PublicGameState getPublicGameState() {
        return publicGameState;
    }
}
