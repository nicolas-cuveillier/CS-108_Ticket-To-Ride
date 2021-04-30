package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
class MapViewCreator {
    private MapViewCreator() {
    }

    public static Node createMapView(ObservableGameState gameState, ObjectProperty<ActionHandler.ClaimRouteHandler> claimRouteH, CardChooser cardChooser) {
        Pane view = new Pane();
        view.getStylesheets().addAll("map.css","colors.css");

        ImageView imageView = new ImageView();
        view.getChildren().add(imageView);

        for (Route route : ChMap.routes()) {
            Group routeGroup = new Group();
            routeGroup.setId(route.id());
            routeGroup.getStyleClass().addAll("route", route.level().name());

            if (route.color() == null) routeGroup.getStyleClass().add("NEUTRAL");
            else routeGroup.getStyleClass().add(route.color().name());

            gameState.routeOwner(route).addListener((obj, oV, nV) -> {
                if (nV != null) routeGroup.getStyleClass().add(nV.name());
            });

            routeGroup.disableProperty().bind(claimRouteH.isNull().or(gameState.claimableRouteProperty(route).not()));

            for (int i = 1; i <= route.length(); i++) {
                Group routeCas = new Group();
                routeCas.setId(routeGroup.getId() + "_" + i);

                Group carGroup = new Group();
                carGroup.getStyleClass().add("car");

                Rectangle carRectangle = new Rectangle(36, 12);
                carRectangle.getStyleClass().add("filled");
                Circle carCircle1 = new Circle(12, 6, 3);
                Circle carCircle2 = new Circle(24, 6, 3);

                Rectangle trackRectangle = new Rectangle(36, 12);
                trackRectangle.getStyleClass().addAll("track", "filled");

                carGroup.getChildren().addAll(carRectangle, carCircle1, carCircle2);
                routeCas.getChildren().addAll(trackRectangle, carGroup);
                routeGroup.getChildren().add(routeCas);
            }

            routeGroup.setOnMouseClicked(o -> {
                List<SortedBag<Card>> possibleClaimCards = gameState.possibleClaimCards(route);

                if (possibleClaimCards.size() == 1) {
                    claimRouteH.get().onClaimRoute(route, possibleClaimCards.get(0));
                } else {
                    ActionHandler.ChooseCardsHandler chooseCardsH = (chosenCards -> claimRouteH.get().onClaimRoute(route, chosenCards));
                    cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
                }
            });

            view.getChildren().add(routeGroup);
        }

        return view;
    }

    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options,
                         ActionHandler.ChooseCardsHandler handler);
    }

}
