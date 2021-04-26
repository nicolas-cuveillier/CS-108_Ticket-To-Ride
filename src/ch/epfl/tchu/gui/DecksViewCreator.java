package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Ticket;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
abstract class DecksViewCreator {
    public static Node createHandView(ObservableGameState observableGameState){
        HBox view = new HBox();
        view.getStylesheets().addAll("deck.css","colors.css");

        ListView<Ticket> ticketsView = new ListView<>();
        ticketsView.setId("tickets");
        HBox hand = new HBox();
        hand.setId("hand-pane");
        view.getChildren().addAll(ticketsView,hand);

        for (int i = 0; i < 9; i++) {
            StackPane stackPane = new StackPane();
            stackPane.getStyleClass().addAll("BLACK", "card");

            Rectangle outside = new Rectangle();
            outside.getStyleClass().add("outside");
            Rectangle inside = new Rectangle();
            inside.getStyleClass().addAll("filled","inside");
            Rectangle trainImage = new Rectangle();
            trainImage.getStyleClass().add("train-image");
            Text count = new Text();
            count.getStyleClass().add("count");

            stackPane.getChildren().addAll(outside,inside,ticketsView,count);
            view.getChildren().add(stackPane);
        }
        
        return view;
    }

    //deux propriétés : la première contient celui gérant le tirage de billets, la seconde contient celui gérant le tirage de cartes.
    public static Node createCardsView(ObservableGameState observableGameState){
        VBox view = new VBox();
        return view;
    }
}
