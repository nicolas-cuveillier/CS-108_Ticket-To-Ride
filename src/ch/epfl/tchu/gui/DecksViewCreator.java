package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.property.ObjectProperty;
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
class DecksViewCreator {
    private DecksViewCreator(){}
    public static Node createHandView(ObservableGameState observableGameState) {
        HBox view = new HBox();
        view.getStylesheets().addAll("decks.css", "colors.css");

        ListView<Ticket> ticketsView = new ListView<>();
        ticketsView.setId("tickets");
        HBox hand = new HBox();
        hand.setId("hand-pane");


        for (Card card : Card.ALL) {
            StackPane stackPane = new StackPane();

            if (card.name().equals("LOCOMOTIVE")) stackPane.getStyleClass().addAll("NEUTRAL", "card");
            else stackPane.getStyleClass().addAll(card.color().name(), "card");

            Rectangle outside = new Rectangle(60, 90);
            outside.getStyleClass().add("outside");
            Rectangle inside = new Rectangle(40, 70);
            inside.getStyleClass().addAll("filled", "inside");
            Rectangle trainImage = new Rectangle(40, 70);
            trainImage.getStyleClass().add("train-image");
            Text count = new Text();
            count.getStyleClass().add("count");

            stackPane.getChildren().addAll(outside, inside, ticketsView, count, trainImage);
            hand.getChildren().add(stackPane);
        }
        view.getChildren().addAll(ticketsView, hand);
        return view;
    }

    public static Node createCardsView(ObservableGameState observableGameState,
                                       ObjectProperty<ActionHandler.DrawTicketsHandler> ticketsHandlerObjectProperty,
                                       ObjectProperty<ActionHandler.DrawCardHandler> cardHandlerObjectProperty) {
        VBox view = new VBox();
        view.getStylesheets().addAll("decks.css", "colors.css");
        view.setId("card-pane");
//TODO add "billets" et "Cartes"
        Button ticketsButton = new Button();
        ticketsButton.getStyleClass().add("gauged");
        Button cardsButton = new Button();
        cardsButton.getStyleClass().add("gauged");

        Rectangle background1 = new Rectangle(50, 5);
        background1.getStyleClass().add("background");
        Rectangle foreground1 = new Rectangle(50, 5);
        foreground1.getStyleClass().add("foreground");
        Group group = new Group();
        group.getChildren().addAll(background1, foreground1);

        Rectangle background2 = new Rectangle(50, 5);
        background2.getStyleClass().add("background");
        Rectangle foreground2 = new Rectangle(50, 5);
        foreground2.getStyleClass().add("foreground");
        Group group1 = new Group();
        group1.getChildren().addAll(background2, foreground2);

        ticketsButton.setGraphic(group);
        cardsButton.setGraphic(group1);

        view.getChildren().add(ticketsButton);
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
        view.getChildren().add(cardsButton);

        return view;
    }
}
