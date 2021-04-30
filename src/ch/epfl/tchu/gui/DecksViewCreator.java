package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
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
class DecksViewCreator {  //TODO javadoc + variables' name
    private DecksViewCreator() {
    }

    public static Node createHandView(ObservableGameState gameState) {
        HBox view = new HBox();
        view.getStylesheets().addAll("decks.css", "colors.css");

        ListView<Ticket> ticketsView = new ListView<>();
        ticketsView.setId("tickets");
        HBox hand = new HBox();
        hand.setId("hand-pane");


        for (Card card : Card.ALL) {
            StackPane stackPane = new StackPane();

            ReadOnlyIntegerProperty count = gameState.cardProperty(card);
            stackPane.visibleProperty().bind(Bindings.greaterThan(count, 0));

            if (card.name().equals("LOCOMOTIVE")) stackPane.getStyleClass().addAll("NEUTRAL", "card");
            else stackPane.getStyleClass().addAll(card.color().name(), "card");

            Rectangle outside = new Rectangle(60, 90);
            outside.getStyleClass().add("outside");
            Rectangle inside = new Rectangle(40, 70);
            inside.getStyleClass().addAll("filled", "inside");
            Rectangle trainImage = new Rectangle(40, 70);
            trainImage.getStyleClass().add("train-image");
            Text textCount = new Text();
            textCount.getStyleClass().add("count");
            textCount.textProperty().bind(Bindings.convert(count));
            textCount.visibleProperty().bind(Bindings.greaterThan(count, 1));

            stackPane.getChildren().addAll(outside, inside, ticketsView, textCount, trainImage);
            hand.getChildren().add(stackPane);
        }
        view.getChildren().addAll(ticketsView, hand);
        return view;
    }

    public static Node createCardsView(ObservableGameState gameState,
                                       ObjectProperty<ActionHandler.DrawTicketsHandler> ticketsHandlerObjectProperty,
                                       ObjectProperty<ActionHandler.DrawCardHandler> cardHandlerObjectProperty) {
        VBox view = new VBox();
        view.getStylesheets().addAll("decks.css", "colors.css");
        view.setId("card-pane");

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

        ticketsButton.disabledProperty().addListener(o -> ticketsHandlerObjectProperty.isNotNull());
        cardsButton.disabledProperty().addListener(o -> cardHandlerObjectProperty.isNull());

        ReadOnlyIntegerProperty pctTicketsProperty = gameState.ticketsInDeckPercent();
        foreground1.widthProperty().bind(pctTicketsProperty.multiply(50).divide(100));
        ReadOnlyIntegerProperty pctCardsProperty = gameState.ticketsInDeckPercent();
        foreground2.widthProperty().bind(pctCardsProperty.multiply(50).divide(100));

        ticketsButton.setOnMouseClicked(o -> ticketsHandlerObjectProperty.get().onDrawTickets());
        cardsButton.setOnMouseClicked(o -> cardHandlerObjectProperty.get().onDrawCard(-1));

        view.getChildren().add(ticketsButton);

        for (int i = 0; i < 5; i++) {
            StackPane ofCard = new StackPane();
            ofCard.getStyleClass().addAll("card");

            ReadOnlyObjectProperty<Card> card = gameState.faceUpCard(i);
            card.addListener((o, oV, nV) -> {
                if (oV != nV) ofCard.getStyleClass().add(nV.color().name());
            });

            int index = i;
            ofCard.setOnMouseClicked(o -> cardHandlerObjectProperty.get().onDrawCard(index));

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
