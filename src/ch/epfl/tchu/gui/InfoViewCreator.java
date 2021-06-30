package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;
import java.util.Map;

/**
 * <h1>InfoViewCreator:</h1>
 * None instantiable class that handles the info panel display within the GUI.
 *
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
final class InfoViewCreator {
    private final static String STYLE_COLORS = "colors.css";
    private final static String STYLE_INFO = "info.css";
    private final static int CIRCLE_RADIUS = 5;

    private InfoViewCreator() {
    }

    /**
     * Static method that will create a node containing all the different component of the left part of the tchu's GUI
     * like public information about players (cars left, points, cards left..) and the last five in-game information.
     *
     * @param player      the id of the actual interface owner
     * @param playersName a map containing all the playerId linked to their name
     * @param gameState   an instance of ObservableGameState from which information will be computed and displayed
     * @param inGameInfo  an Observable list of length five that will contain the five or less information concerning
     *                    the gameState
     * @return the node of the left part of the tchu's gui that contains public information on the players and the
     * five last information concerning actions
     */
    public static Node createInfoView(PlayerId player, Map<PlayerId, String> playersName,
                                      ObservableGameState gameState, ObservableList<Text> inGameInfo) {
        VBox infoView = new VBox();
        infoView.getStylesheets().addAll(STYLE_INFO, STYLE_COLORS);

        VBox playersStatView = new VBox();
        playersStatView.setId("player-stats");

        //create the two player's stat view, putting the owner of the gui in first and binding all info of the text with
        //with the corresponding properties
        for (PlayerId pId : List.of(player, player.next())) {
            TextFlow text = new TextFlow();
            text.getStyleClass().add(pId.name());

            Circle coloredCircle = new Circle(CIRCLE_RADIUS);
            coloredCircle.getStyleClass().add("filled");

            //make the text and update it
            Text playerInfo = makePlayerInfoText(playersName.get(pId),
                    gameState.ticketsCountProperty(pId), gameState.cardsCountProperty(pId),
                    gameState.carsCountProperty(pId), gameState.pointsCountProperty(pId));

            text.getChildren().addAll(coloredCircle, playerInfo);
            playersStatView.getChildren().add(text);
        }

        Separator separator = new Separator(Orientation.HORIZONTAL);

        //game info
        TextFlow inGameInfoText = new TextFlow();
        inGameInfoText.setId("game-info");
        inGameInfoText.getChildren().addAll(inGameInfo);
        Bindings.bindContent(inGameInfoText.getChildren(), inGameInfo);

        infoView.getChildren().addAll(playersStatView, separator, inGameInfoText);
        return infoView;
    }

    /**
     * Create an instance of Text according to the {@link StringsFr#PLAYER_STATS} string and bind each element according
     * to the property pass to the method.
     */
    private static Text makePlayerInfoText(String playerName, ReadOnlyIntegerProperty ticketsCount,
                                           ReadOnlyIntegerProperty cardsCount, ReadOnlyIntegerProperty carsCount,
                                           ReadOnlyIntegerProperty pointsCount) {

        Text playerInfo = new Text(String.format(StringsFr.PLAYER_STATS, playerName,
                ticketsCount, cardsCount, carsCount, pointsCount));
        playerInfo.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS, playerName,
                ticketsCount, cardsCount, carsCount, pointsCount));

        return playerInfo;
    }
}
