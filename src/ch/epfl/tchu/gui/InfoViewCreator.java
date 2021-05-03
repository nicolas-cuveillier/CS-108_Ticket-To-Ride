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
    //TODO put all constant in ch.epfl.tchu.game.Constants
    private final static String STYLE_COLORS = "colors.css";
    private final static String STYLE_INFO = "info.css";

    private InfoViewCreator() {
    }

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
