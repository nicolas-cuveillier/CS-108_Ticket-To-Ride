package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
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
 * <h1>DecksViewCreator</h1>
 * None instantiable class that handles the creation and UI of the different decks in the game.
 *
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
final class DecksViewCreator {
    private DecksViewCreator() {
    }

    private static final String STYLE_CARD = "card";
    private static final String STYLE_DECK = "decks.css";
    private static final String STYLE_COLORS = "colors.css";
    private static final String STYLE_NEUTRAL = "NEUTRAL";
    private static final String STYLE_GAUGED = "gauged";

    /**
     * Static method that will create a node containing all the different component of the bottom part of the tchu's GUI
     * like the personal player's cards, the tickets view. Handle also, interaction between the GUI and the action of a
     * human player.
     *
     * @param gameState an instance of ObservableGameState that gives to this method the properties of some components
     * @return (Node) the Node of the third part of the Tchu's GUI that represent the player's cards and the list of its
     * tickets
     */
    public static Node createHandView(ObservableGameState gameState) {
        HBox view = new HBox();
        view.getStylesheets().addAll(STYLE_DECK, STYLE_COLORS);

        //create the scroll down menu for the player's tickets
        ListView<Ticket> ticketsView = new ListView<>(gameState.ticketsProperties());
        ticketsView.setId("tickets");

        HBox handBox = new HBox();
        handBox.setId("hand-pane");

        //create a StackPane for each Card according to its color and count in the player's hand
        for (Card card : Card.ALL) {
            StackPane cardPane = new StackPane();

            cardPane.getStyleClass().addAll((card == Card.LOCOMOTIVE) ? STYLE_NEUTRAL : card.color().name(), STYLE_CARD);

            ReadOnlyIntegerProperty count = gameState.cardProperty(card);
            cardPane.visibleProperty().bind(Bindings.greaterThan(count, 0));

            makeCardPaneWithCount(cardPane, count);
            handBox.getChildren().add(cardPane);
        }

        view.getChildren().addAll(ticketsView, handBox);
        return view;
    }


    /**
     * Static method that will create a node containing all the different component of the right part of the tchu's GUI
     * like the deck and the face-up cards. Handle also, interaction between the GUI and the action of a human player.
     *
     * @param gameState              an instance of ObservableGameState that gives to this method the properties of some components
     * @param ticketsHandlerProperty a property of {@link ch.epfl.tchu.gui.ActionHandler.DrawTicketsHandler} that will
     *                               handle the drawing of tickets
     * @param cardHandlerProperty    a property of {@link ch.epfl.tchu.gui.ActionHandler.DrawCardHandler} that will handle
     *                               the drawing of cards
     * @return (Node) the Node of the second part of the Tchu's GUI that represent the deck of cards, deck of tickets
     * and the face-up cards
     */
    public static Node createCardsView(ObservableGameState gameState,
                                       ObjectProperty<ActionHandler.DrawTicketsHandler> ticketsHandlerProperty,
                                       ObjectProperty<ActionHandler.DrawCardHandler> cardHandlerProperty) {
        VBox view = new VBox();
        view.getStylesheets().addAll(STYLE_DECK, STYLE_COLORS);
        view.setId("card-pane");

        //create the button that will be use for drawing tickets
        Button ticketsButton = makeButton(StringsFr.TICKETS, gameState.ticketsInDeckPercentProperty());
        ticketsButton.disableProperty().bind(ticketsHandlerProperty.isNull());
        ticketsButton.setOnMouseClicked(o -> ticketsHandlerProperty.get().onDrawTickets());

        //create the button that will be use for drawing the top deck card
        Button cardsButton = makeButton(StringsFr.CARDS, gameState.cardsInDeckPercentProperty());
        cardsButton.disableProperty().bind(cardHandlerProperty.isNull());
        cardsButton.setOnMouseClicked(o -> cardHandlerProperty.get().onDrawCard(Constants.DECK_SLOT));

        view.getChildren().add(ticketsButton);

        //create a StackPane for each faceUpCard according to its color
        //and add a listener to it to update this color through the game
        for (Integer index : Constants.FACE_UP_CARD_SLOTS) {
            StackPane cardPane = new StackPane();
            cardPane.getStyleClass().add(STYLE_CARD);

            gameState.faceUpCardProperty(index).addListener((o, oV, nV) -> {
                String styleClassName = (nV.color() != null) ? nV.color().name() : STYLE_NEUTRAL;
                if (cardPane.getStyleClass().size() >= 2) cardPane.getStyleClass().set(1, styleClassName);
                else cardPane.getStyleClass().add(styleClassName);
            });
            cardPane.disableProperty().bind(cardHandlerProperty.isNull());
            cardPane.setOnMouseClicked(o -> cardHandlerProperty.get().onDrawCard(index));

            makeCardPane(cardPane);
            view.getChildren().add(cardPane);
        }

        view.getChildren().add(cardsButton);
        return view;
    }

    private static Button makeButton(String name, ReadOnlyIntegerProperty percentage) {
        Button button = new Button(name);
        button.getStyleClass().add(STYLE_GAUGED);
        button.setGraphic(getGraphicButtonGroup(percentage));
        return button;
    }

    private static Group getGraphicButtonGroup(ReadOnlyIntegerProperty percent) {
        Rectangle background = new Rectangle(50, 5);
        background.getStyleClass().add("background");

        Rectangle foreground = new Rectangle(50, 5);
        foreground.getStyleClass().add("foreground");
        foreground.widthProperty().bind(percent.multiply(50).divide(100));

        Group group = new Group();
        group.getChildren().addAll(background, foreground);
        return group;
    }

    private static void makeCardPane(StackPane pane) {
        Rectangle outsideRect = new Rectangle(60, 90);
        outsideRect.getStyleClass().add("outside");

        Rectangle insideRect = new Rectangle(40, 70);
        insideRect.getStyleClass().addAll("filled", "inside");

        Rectangle trainImage = new Rectangle(40, 70);
        trainImage.getStyleClass().add("train-image");

        pane.getChildren().addAll(outsideRect, insideRect, trainImage);
    }

    private static void makeCardPaneWithCount(StackPane pane, ReadOnlyIntegerProperty count) {
        makeCardPane(pane);
        Text textCount = new Text();
        textCount.getStyleClass().add("count");
        textCount.textProperty().bind(Bindings.convert(count));
        textCount.visibleProperty().bind(Bindings.greaterThan(count, 1));
        pane.getChildren().add(textCount);
    }

}
