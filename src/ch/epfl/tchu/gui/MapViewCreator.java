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
 * <h1>MapViewCreator</h1>
 * None instantiable class that handle the creation of the map.
 *
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
final class MapViewCreator {
    private MapViewCreator() {
    }

    private static final String STYLE_COLORS = "colors.css";
    private static final String STYLE_MAP = "map.css";
    private static final String STYLE_ROUTE = "route";
    private static final String STYLE_CAR = "car";
    private static final String STYLE_FILLED = "filled";
    private static final int CIRCLE_CENTER_X_1 = 12;
    private static final int CIRCLE_CENTER_X_2 = 24;
    private static final int CIRCLE_CENTER_Y = 6;
    private static final int CIRCLE_RADIUS = 3;

    /**
     * Static method that will create a node containing all the different component of the tchu's map.
     *
     * @param gameState   an instance of ObservableGameState that gives to this method the properties of some components
     * @param claimRouteH property of the {@link ch.epfl.tchu.gui.ActionHandler.ClaimRouteHandler} that handle route claiming
     * @param cardChooser an instance of the functional interface that handle the choosing part of the route claiming
     * @return (Node) the Node of the main part of the Tchu's GUI that represent the map, routes and stations
     */
    public static Node createMapView(ObservableGameState gameState, ObjectProperty<ActionHandler.ClaimRouteHandler> claimRouteH,
                                     CardChooser cardChooser) {
        Pane view = new Pane();
        view.getStylesheets().addAll(STYLE_MAP, STYLE_COLORS);

        ImageView imageView = new ImageView();
        view.getChildren().add(imageView);

        //for each route of the game, create its representation with creating all sub pane according to position of all
        //individual tracks segment in file map.css
        for (Route route : ChMap.routes()) {
            Group routeGroup = new Group();
            routeGroup.setId(route.id());
            routeGroup.getStyleClass().addAll(STYLE_ROUTE, route.level().name(), (route.color() == null) ? "NEUTRAL" : route.color().name());

            //define all case of the route
            for (int i = 1; i <= route.length(); i++) {
                Group routeCase = new Group();
                routeCase.setId(routeGroup.getId() + "_" + i);
                makeCarGroup(routeCase);
                routeGroup.getChildren().add(routeCase);
            }

            //set properties of the group containing all the Route
            gameState.routeOwnerProperty(route).addListener((obj, oV, nV) -> {
                if (nV != null) routeGroup.getStyleClass().add(nV.name());
            });
            routeGroup.disableProperty().bind(claimRouteH.isNull().or(gameState.claimableRouteProperty(route).not()));
            routeGroup.setOnMouseClicked(o -> {
                List<SortedBag<Card>> possibleClaimCards = gameState.possibleClaimCards(route);

                if (possibleClaimCards.size() == 1) claimRouteH.get().onClaimRoute(route, possibleClaimCards.get(0));
                else {
                    ActionHandler.ChooseCardsHandler chooseCardsH = (chosenCards -> claimRouteH.get().onClaimRoute(route, chosenCards));
                    cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
                }
            });
            view.getChildren().add(routeGroup);
        }
        return view;
    }

    private static void makeCarGroup(Group routeCase) {
        Group carGroup = new Group();
        carGroup.getStyleClass().add(STYLE_CAR);

        Rectangle carRectangle = new Rectangle(36, 12);
        carRectangle.getStyleClass().add(STYLE_FILLED);
        Circle carCircle1 = new Circle(CIRCLE_CENTER_X_1, CIRCLE_CENTER_Y, CIRCLE_RADIUS);
        Circle carCircle2 = new Circle(CIRCLE_CENTER_X_2, CIRCLE_CENTER_Y, CIRCLE_RADIUS);

        Rectangle trackRectangle = new Rectangle(36, 12);
        trackRectangle.getStyleClass().addAll("track", STYLE_FILLED);

        carGroup.getChildren().addAll(carRectangle, carCircle1, carCircle2);
        routeCase.getChildren().addAll(trackRectangle, carGroup);
    }

    /**
     * Functional interface that implement the notion of Card chooser.
     */
    @FunctionalInterface
    interface CardChooser {
        /**
         * Intended to be called when the player has to choose the cards he wants to use. The options are given by the
         * options argument, while the action handler is intended to be used when he has made his choice.
         *
         * @param options a list of all possible set of possible card that can be used
         * @param handler an instance of the {@link ch.epfl.tchu.gui.ActionHandler.ChooseCardsHandler} that
         *                will handle the user action.
         */
        void chooseCards(List<SortedBag<Card>> options, ActionHandler.ChooseCardsHandler handler);
    }

}
