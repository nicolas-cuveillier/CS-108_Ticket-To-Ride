package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Ticket;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
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
    public static Node createHandView(ObservableGameState observableGameState) {
        HBox view = new HBox();
        view.getStylesheets().addAll("decks.css", "colors.css");

        ListView<Ticket> ticketsView = new ListView<>();
        ticketsView.setId("tickets");
        HBox hand = new HBox();
        hand.setId("hand-pane");
        view.getChildren().addAll(ticketsView, hand);

        for (Card card : Card.ALL) {
            StackPane stackPane = new StackPane();

            if (card.name().equals("LOCOMOTIVE")) {
                stackPane.getStyleClass().addAll("NEUTRAL", "card");
            } else
                stackPane.getStyleClass().addAll(card.color().name(), "card");

            Rectangle outside = new Rectangle(60, 90);
            outside.getStyleClass().add("outside");
            Rectangle inside = new Rectangle(40, 70);
            inside.getStyleClass().addAll("filled", "inside");
            Rectangle trainImage = new Rectangle(40, 70);
            trainImage.getStyleClass().add("train-image");
            Text count = new Text();
            count.getStyleClass().add("count");

            stackPane.getChildren().addAll(outside, inside, ticketsView, count);
            view.getChildren().add(stackPane);
        }

        return view;
    }

    //deux propriétés : la première contient celui gérant le tirage de billets, la seconde contient celui gérant le tirage de cartes.
    public static Node createCardsView(ObservableGameState observableGameState) {
        VBox view = new VBox();
        view.getStylesheets().addAll("decks.css", "colors.css");
        view.setId("card-pane");

        Button ticketsButton = new Button();
        ticketsButton.getStyleClass().add("gauged");
        Button cardsButton = new Button();
        cardsButton.getStyleClass().add("gauged");

        Rectangle background = new Rectangle(50, 5);
        background.getStyleClass().add("background");
        Rectangle foreground = new Rectangle(50, 5);
        foreground.getStyleClass().add("foreground");
        Group group = new Group();
        group.getChildren().addAll(background,foreground);

        ticketsButton.setGraphic(group);
        cardsButton.setGraphic(group);

        for (int i = 0; i < 5; i++) {//TODO change

            StackPane ofCard = new StackPane();
            ofCard.getStyleClass().addAll("NEUTRAL", "card"); 

            Rectangle outside = new Rectangle(60, 90);
            outside.getStyleClass().add("outside");
            Rectangle inside = new Rectangle(40, 70);
            inside.getStyleClass().addAll("filled", "inside");
            Rectangle trainImage = new Rectangle(40, 70);
            trainImage.getStyleClass().add("train-image");

            ofCard.getChildren().addAll(outside, inside, trainImage);
            view.getChildren().add(ofCard);
        }
        view.getChildren().addAll(ticketsButton, cardsButton);

        return view;
    }
}
