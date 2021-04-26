package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import javafx.beans.property.ReadOnlyDoubleProperty;

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
}
