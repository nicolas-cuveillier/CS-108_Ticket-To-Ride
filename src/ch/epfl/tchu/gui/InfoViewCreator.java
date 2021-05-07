package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
final class InfoViewCreator {
    //TODO clarify class
    private final static String STYLE_COLORS = "colors.css";
    private final static String STYLE_INFO = "info.css";

    private InfoViewCreator() {
    }

    /**
     * static method that will create a node containing all the different component of the left part of the tchu's GUI
     * like public information about players (cars left, points, cards left..) and the last five in-game information.
     *
     * @param player the id of the actual interface owner
     * @param playersName a map containing all the playerId linked to their name
     * @param gameState an instance of ObservableGameState from which information will be computed and displayed
     * @param inGameInfo an Observable list of length five that will contain the five or less information concerning
     * the gameState
     * @return the node of the left part of the tchu's gui that contains public information on the players and the
     * five last information concerning actions.
     */
    public static Node createInfoView(PlayerId player, Map<PlayerId, String> playersName,
                                      ObservableGameState gameState, ObservableList<Text> inGameInfo) {
        VBox infoView = new VBox();
        infoView.getStylesheets().addAll(STYLE_INFO, STYLE_COLORS);

        Separator separator = new Separator(Orientation.HORIZONTAL);
        VBox playersStatView = new VBox();
        playersStatView.setId("player-stats");

        //players info
        for (PlayerId pId : PlayerId.ALL) {
            TextFlow text = new TextFlow();
            text.getStyleClass().add(pId.name());
            Circle coloredCircle = new Circle(5);
            coloredCircle.getStyleClass().add("filled");

            Text playerInfo = new Text(String.format(StringsFr.PLAYER_STATS, playersName.get(pId), gameState.ticketsCount(pId)
                    , gameState.cardsCount(pId), gameState.carsCount(pId), gameState.pointsCount(pId)));

            playerInfo.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS, playersName.get(pId), gameState.ticketsCount(pId)
                    , gameState.cardsCount(pId), gameState.carsCount(pId), gameState.pointsCount(pId)));

            text.getChildren().addAll(coloredCircle, playerInfo);
            playersStatView.getChildren().add(text);
        }

        //game info
        TextFlow inGameInfoText = new TextFlow();
        inGameInfoText.setId("game-info");
        inGameInfoText.getChildren().addAll(inGameInfo);
        Bindings.bindContent(inGameInfoText.getChildren(), inGameInfo);

        infoView.getChildren().addAll(playersStatView, separator,inGameInfoText);
        return infoView;
    }
}
