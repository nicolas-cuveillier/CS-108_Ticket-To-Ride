package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
 *
 * none instanciable class that handle the creation of the differents deck of the game
 */
class DecksViewCreator {  //TODO javadoc
    private DecksViewCreator() {
    }

    private static final String STYLE_CARD = "card";
    private static final String STYLE_DECK = "decks.css";
    private static final String STYLE_COLORS = "colors.css";

    /**
     * static method that will create a node containing all the different component of the bottom part of the tchu's GUI
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

        ListView<Ticket> ticketsView = new ListView<>(gameState.ticketsProperties());
        ticketsView.setId("tickets");

        HBox hand = new HBox();
        hand.setId("hand-pane");

        for (Card card : Card.ALL) {
            StackPane cardPane = new StackPane();

            ReadOnlyIntegerProperty count = gameState.cardProperty(card);
            cardPane.visibleProperty().bind(Bindings.greaterThan(count, 0));

            if (card.name().equals("LOCOMOTIVE")) cardPane.getStyleClass().addAll("NEUTRAL", STYLE_CARD);
            else cardPane.getStyleClass().addAll(card.color().name(), STYLE_CARD);

            addCardPaneChildren(cardPane, count);
            hand.getChildren().add(cardPane);
        }
        view.getChildren().addAll(ticketsView, hand);
        return view;
    }

    private static void addCardPaneChildren(StackPane pane, ReadOnlyIntegerProperty count) {
        Rectangle outsideRect = new Rectangle(60, 90);
        outsideRect.getStyleClass().add("outside");
        Rectangle insideRect = new Rectangle(40, 70);
        insideRect.getStyleClass().addAll("filled", "inside");
        Rectangle trainImage = new Rectangle(40, 70);
        trainImage.getStyleClass().add("train-image");
        Text textCount = new Text();//TODO le nombre se display pas sur la carte
        textCount.getStyleClass().add("count");
        textCount.textProperty().bind(Bindings.convert(count));
        textCount.visibleProperty().bind(Bindings.greaterThan(count, 1));
        pane.getChildren().addAll(outsideRect, insideRect, trainImage,textCount);
    }

    /**
     * static method that will create a node containing all the different component of the right part of the tchu's GUI
     * like the to deck and the face-up cards. Handle also, interaction between the GUI and the action of a human player.
     *
     * @param gameState an instance of ObservableGameState that gives to this method the properties of some components
     * @param ticketsHandlerProperty a property of {@link ch.epfl.tchu.gui.ActionHandler.DrawTicketsHandler} that will
     *                               handle the drawing of tickets
     * @param cardHandlerProperty a property of {@link ch.epfl.tchu.gui.ActionHandler.DrawCardHandler} that will handle
     *                            the drawing of cards
     * @return (Node) the Node of the second part of the Tchu's GUI that represent the deck of cards, deck of tickets
     * and the face-up cards
     */
    public static Node createCardsView(ObservableGameState gameState,
                                       ObjectProperty<ActionHandler.DrawTicketsHandler> ticketsHandlerProperty,
                                       ObjectProperty<ActionHandler.DrawCardHandler> cardHandlerProperty) {
        VBox view = new VBox();
        view.getStylesheets().addAll(STYLE_DECK, STYLE_COLORS);
        view.setId("card-pane");

        Button ticketsButton = new Button(StringsFr.TICKETS);
        ticketsButton.getStyleClass().add("gauged");

        Button cardsButton = new Button(StringsFr.CARDS);
        cardsButton.getStyleClass().add("gauged");

        ticketsButton.setGraphic(getGraphicButtonGroup(gameState.ticketsInDeckPercent()));
        cardsButton.setGraphic(getGraphicButtonGroup(gameState.cardsInDeckPercent()));

        ticketsButton.disableProperty().bind(ticketsHandlerProperty.isNull());
        ticketsButton.setOnMouseClicked(o -> ticketsHandlerProperty.get().onDrawTickets());

        cardsButton.disableProperty().bind(cardHandlerProperty.isNull());
        cardsButton.setOnMouseClicked(o -> cardHandlerProperty.get().onDrawCard(Constants.DECK_SLOT));

        view.getChildren().add(ticketsButton);

        for (int i = 0; i < 5; i++) {
            StackPane cardPane = new StackPane();
            cardPane.getStyleClass().addAll(STYLE_CARD);

            ReadOnlyObjectProperty<Card> card = gameState.faceUpCard(i);
            card.addListener((o, oV, nV) -> {
                if (oV != nV) cardPane.getStyleClass().add(nV.color().name());
            });

            int index = i;
            cardPane.setOnMouseClicked(o -> {
                if(cardHandlerProperty.isNotNull().get())
                    cardHandlerProperty.get().onDrawCard(index);
            });

            addFaceUpCardPaneChildren(cardPane);
            view.getChildren().add(cardPane);
        }
        view.getChildren().add(cardsButton);
        return view;
    }

    private static Group getGraphicButtonGroup(ReadOnlyIntegerProperty percent) {
        Rectangle background = new Rectangle(50, 5);
        background.getStyleClass().add("background");
        Rectangle foreground = new Rectangle(50, 5);
        foreground.getStyleClass().add("foreground");
        Group group = new Group();
        group.getChildren().addAll(background, foreground);
        foreground.widthProperty().bind(percent.multiply(50).divide(100));
        return group;
    }

    private static void addFaceUpCardPaneChildren(StackPane cardPane) {
        Rectangle outside = new Rectangle(60, 90);
        outside.getStyleClass().add("outside");
        Rectangle inside = new Rectangle(40, 70);
        inside.getStyleClass().addAll("filled", "inside");
        Rectangle trainImage = new Rectangle(40, 70);
        trainImage.getStyleClass().add("train-image");

        cardPane.getChildren().addAll(outside, inside, trainImage);
    }

}
