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
        Pane pane = new Pane();
        pane.getStylesheets().add("map.css");
        pane.getStylesheets().add("colors.css");
        ImageView imageView = new ImageView();
        pane.getChildren().add(imageView);

        for (Route r : ChMap.routes()) {
            Group routeGroup = new Group();
            routeGroup.setId(r.id());
            routeGroup.getStyleClass().add("routeGroup");
            routeGroup.getStyleClass().add(r.level().name());

            if (r.color() == null) routeGroup.getStyleClass().add("NEUTRAL");
            else routeGroup.getStyleClass().add(r.color().name());

            gameState.routeOwner(r).addListener((obj, oV, nV) -> {
                if (nV != null) routeGroup.getStyleClass().add(nV.name());
            });
            routeGroup.disableProperty().bind(claimRouteH.isNull().or(gameState.claimableRouteProperty(r).not()));

            for (int i = 1; i <= r.length(); i++) {
                Group cas = new Group();
                cas.setId(routeGroup.getId() + "_" + i);

                Rectangle trackRectangle = new Rectangle(36, 12);
                trackRectangle.getStyleClass().addAll("track", "filled");
                Group car = new Group();
                car.getStyleClass().add("car");

                Rectangle carRectangle = new Rectangle(36, 12);
                carRectangle.getStyleClass().add("filled");
                Circle circle1 = new Circle(12, 6, 3);
                Circle circle2 = new Circle(24, 6, 3);

                car.getChildren().addAll(carRectangle, circle1, circle2);
                cas.getChildren().addAll(trackRectangle, car);
                routeGroup.getChildren().add(cas);
            }

            routeGroup.setOnMouseClicked(o -> {
                List<SortedBag<Card>> possibleClaimCards = gameState.possibleClaimCards(r);

                if (possibleClaimCards.size() == 1) {
                    claimRouteH.get().onClaimRoute(r, possibleClaimCards.get(0));
                } else {
                    ActionHandler.ChooseCardsHandler chooseCardsH = (chosenCards -> claimRouteH.get().onClaimRoute(r, chosenCards));
                    cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
                }
            });

            pane.getChildren().add(routeGroup);
        }

        return pane;
    }

    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options,
                         ActionHandler.ChooseCardsHandler handler);
    }

}
