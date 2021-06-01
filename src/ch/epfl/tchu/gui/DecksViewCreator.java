package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Ticket;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
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
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

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

        ListView<Ticket> ticketsView = new ListView<>(gameState.ticketsProperties());
        ticketsView.setId("tickets");

        HBox handBox = new HBox();
        handBox.setId("hand-pane");

        for (Card card : Card.ALL) {
            StackPane cardPane = new StackPane();

            cardPane.getStyleClass().addAll((card.name().equals("LOCOMOTIVE")) ? STYLE_NEUTRAL : card.color().name(), STYLE_CARD);

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

        //create button use to draw tickets
        Button ticketsButton = makeButtonFromGraphic(StringsFr.TICKETS, gameState.ticketsInDeckPercentProperty());
        ticketsButton.disableProperty().bind(ticketsHandlerProperty.isNull());
        ticketsButton.setOnMouseClicked(o -> ticketsHandlerProperty.get().onDrawTickets());
        view.getChildren().add(ticketsButton);

        //create a StackPane for each faceUpCard according to its color and attach listener to the faceUpCard property
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

        StackPane topCardPane = new StackPane();
        topCardPane.getStyleClass().add(STYLE_CARD);

        //create button use to draw cards and define action when mouse click
        Button cardsButton = makeButtonFromGraphic(StringsFr.CARDS, gameState.cardsInDeckPercentProperty());
        cardsButton.disableProperty().bind(cardHandlerProperty.isNull());
        cardsButton.setOnMouseClicked(o -> setOnMouseClickedForCardsButton(gameState, cardHandlerProperty, topCardPane, cardsButton));
        view.getChildren().add(cardsButton);

        //create the top deck card StackPane and hide it until cardsButton is pressed
        gameState.topDeckCardProperty().addListener((obs, oV, nV) -> {
            String styleClassName = (nV.color() != null) ? nV.color().name() : STYLE_NEUTRAL;

            if (topCardPane.getStyleClass().size() >= 2) topCardPane.getStyleClass().set(1, styleClassName);
            else topCardPane.getStyleClass().add(styleClassName);
        });

        //by default, top card isn't visible
        topCardPane.visibleProperty().setValue(false);
        makeCardPane(topCardPane);

        view.getChildren().add(topCardPane);
        return view;
    }

    /**
     * Static method that will handle the animation/transition when the top deck card is draw.
     */
    public static void setOnMouseClickedForCardsButton(ObservableGameState gameState,
                                                       ObjectProperty<ActionHandler.DrawCardHandler> cardHandlerProperty,
                                                       StackPane cardPane, Button btn) {

        btn.setMouseTransparent(true);
        cardPane.visibleProperty().setValue(true);

        //initialize the path and the pathTransition
        Path path = new Path();
        path.getElements().add(new MoveTo(0, 0));
        CubicCurveTo curveTo = new CubicCurveTo(0, 0, -315, -125, 0, 100);
        path.getElements().add(curveTo);
        PathTransition pathTransition = new PathTransition(Duration.millis(1700), path, cardPane);
        pathTransition.setInterpolator(Interpolator.EASE_OUT);

        //set the X position according to the Card
        switch (gameState.topDeckCardProperty().get()) {
            case RED:
                curveTo.setX(-440);
                break;
            case BLUE:
                curveTo.setX(-720);
                break;
            case BLACK:
                curveTo.setX(-865);
                break;
            case GREEN:
                curveTo.setX(-655);
                break;
            case WHITE:
                curveTo.setX(-370);
                break;
            case ORANGE:
                curveTo.setX(-510);
                break;
            case VIOLET:
                curveTo.setX(-795);
                break;
            case YELLOW:
                curveTo.setX(-570);
                break;
            case LOCOMOTIVE:
                curveTo.setX(-295);
                break;
            default:
                break;
        }
        pathTransition.play();
        pathTransition.setOnFinished(oe -> {
            cardPane.visibleProperty().setValue(false);
            btn.setMouseTransparent(false);
            cardHandlerProperty.get().onDrawCard(Constants.DECK_SLOT);
        });
    }

    private static Button makeButtonFromGraphic(String name, ReadOnlyIntegerProperty percentage) {
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
