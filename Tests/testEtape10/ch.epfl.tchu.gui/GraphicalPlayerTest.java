package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

/**
 * @author Nicolas Cuveillier
 */
public class GraphicalPlayerTest  extends Application {

    private void setState(GraphicalPlayer player) {
        PlayerState p1State =
                new PlayerState(SortedBag.of(ChMap.tickets().subList(0, 3)),
                        SortedBag.of(1, Card.WHITE, 3, Card.RED),
                        ChMap.routes().subList(0, 3));

        List<Route> routes = new ArrayList<>( ChMap.routes().subList(3, 6));
        routes.add(ChMap.routes().get(ChMap.routes().size()-1));
        PublicPlayerState p2State =
                new PublicPlayerState(0, 0,routes);

        Map<PlayerId, PublicPlayerState> pubPlayerStates =
                Map.of(PLAYER_1, p1State, PLAYER_2, p2State);
        PublicCardState cardState =
                new PublicCardState(Card.ALL.subList(0, 5), 110 - 2 * 4 - 5, 0);
        PublicGameState publicGameState =
                new PublicGameState(36, cardState, PLAYER_1, pubPlayerStates, null);

        player.setState(publicGameState, p1State);
    }

    @Override
    public void start(Stage primaryStage) {
        Map<PlayerId, String> playerNames =
                Map.of(PLAYER_1, "Ada", PLAYER_2, "Charles");
        GraphicalPlayer p = new GraphicalPlayer(PLAYER_1, playerNames);
        setState(p);

        ActionHandler.DrawTicketsHandler drawTicketsH =
                () -> p.receiveInfo("Je tire des billets !");
        ActionHandler.DrawCardHandler drawCardH =
                s -> p.receiveInfo(String.format("Je tire une carte de %s !", s));
        ActionHandler.ClaimRouteHandler claimRouteH =
                (r, cs) -> {
                    String rn = r.station1() + " - " + r.station2();
                    p.receiveInfo(String.format("Je m'empare de %s avec %s", rn, cs));
                };

        p.startTurn(drawTicketsH, drawCardH, claimRouteH);
    }
}

