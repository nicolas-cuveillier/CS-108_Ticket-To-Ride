package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
abstract class MapViewCreator {
    //add ObservableGameState, ObjectProperty<ClaimRouteHandler>, CardChooser
    public static Node createMapView() {
        Pane pane = new Pane();
        pane.getStylesheets().add("map.css");
        pane.getStylesheets().add("colors.css");
        pane.getChildren().add(new ImageView());

        for (Route r : ChMap.routes()) {
            Group route = new Group();
            route.setId(r.station1().name() + "_" + r.station2().name() + "_" + r.length());
            route.getStyleClass().add("route");
            route.getStyleClass().add(r.level().name());
            route.getStyleClass().add("NEUTRAL");//to be changed

            for (int i = 0; i < r.length(); i++) {
                Group cas = new Group();
                cas.setId(route.getId() + "_" + i);

                Rectangle trackRectangle = new Rectangle(36, 12);
                trackRectangle.getStyleClass().add("track");
                trackRectangle.getStyleClass().add("filled");
                Group car = new Group();
                car.getStyleClass().add("car");

                Rectangle carRectangle = new Rectangle(36, 12);
                carRectangle.getStyleClass().add("filled");
                Circle circle1 = new Circle(12, 6,3);
                Circle circle2 = new Circle(24, 6,3);

                car.getChildren().addAll(carRectangle,circle1,circle2);
                cas.getChildren().addAll(trackRectangle,car);
                route.getChildren().add(cas);
            }

            pane.getChildren().add(route);
        }

        return pane;
    }

    /*@FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options,
                         ChooseCardsHandler handler);
    }*/

}
