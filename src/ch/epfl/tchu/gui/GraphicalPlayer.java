package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static javafx.application.Platform.isFxApplicationThread;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public final class GraphicalPlayer {
    private final PlayerId player;
    private final Map<PlayerId, String> playersName;
    private final ObservableGameState gameState;
    private final ObservableList<Text> information;
    private final Stage mainView;
    private final ObjectProperty<ActionHandler.ClaimRouteHandler> claimRoute;
    private final ObjectProperty<ActionHandler.DrawTicketsHandler> drawTickets;
    private final ObjectProperty<ActionHandler.DrawCardHandler> drawCard;

    /**
     * Unique constructor for a GraphicalPlayer. Build a GraphicalPlayer with initializing its properties to default
     * value, building an ObservableGameState according to the player. Then, build all the different components pf the
     * tchu's gui (mapView, cardsView, handView, infoView) in one main BorderPane that will be the scene of the main
     * stage
     *
     * @param player the id of the player for which the GraphicalPlayer is instantiated
     * @param playersName a map containing the id of all players linked to their name
     */
    public GraphicalPlayer(PlayerId player, Map<PlayerId, String> playersName) {
        this.player = player;
        this.playersName = Map.copyOf(playersName);
        this.gameState = new ObservableGameState(player);
        this.information = FXCollections.observableArrayList();

        this.claimRoute = new SimpleObjectProperty<>();
        this.drawTickets = new SimpleObjectProperty<>();
        this.drawCard = new SimpleObjectProperty<>();

        Node mapView = MapViewCreator.createMapView(gameState, claimRoute, this::chooseClaimCards);
        Node cardsView = DecksViewCreator.createCardsView(gameState, drawTickets, drawCard);
        Node handView = DecksViewCreator.createHandView(gameState);
        Node infoView = InfoViewCreator.createInfoView(PLAYER_1, playersName, gameState, information);
        BorderPane borderPane =
                new BorderPane(mapView, null, cardsView, handView, infoView);

        this.mainView = new Stage();
        mainView.setScene(new Scene(borderPane));
        mainView.setTitle("tChu" + " \u2014 " + playersName.get(player));
        mainView.show();
    }

    /**
     * method that will update all properties of the actual game state that have changed during a player turn according to the new game state
     * and to the actual playerState. This method call the setState method from the ObservableGameState class
     *
     * @param newGameState the new PublicGameState from which properties will be updated
     * @param playerState the current playerState
     */
    public void setState(PublicGameState newGameState, PlayerState playerState) {
        assert isFxApplicationThread();
        gameState.setState(newGameState, playerState);
    }

    /**
     * add the message to the left hand side of the gui to display it and remove one if the number of messages exceed 5
     * @param message the message that will be display in the InfoView at the left of the screen
     */
    public void receiveInfo(String message) {
        assert isFxApplicationThread();
        if (information.size() == 5)
            information.remove(0);
        information.add(new Text(message));
    }

    public void startTurn(ActionHandler.DrawTicketsHandler drawTicketsH, ActionHandler.DrawCardHandler drawCardH, ActionHandler.ClaimRouteHandler claimRouteH) {
        assert isFxApplicationThread();
        drawTickets.setValue(!gameState.canDrawCards() ? null : () -> {
            clearProperties();
            drawTicketsH.onDrawTickets();
        });

        drawCard.setValue(!gameState.canDrawCards() ? null : index -> {
            clearProperties();
            drawCardH.onDrawCard(index);
        });

        claimRoute.setValue(((route, cards) -> {
            clearProperties();
            claimRouteH.onClaimRoute(route, cards);
        }));
    }

    public void chooseTickets(SortedBag<Ticket> options, ActionHandler.ChooseTicketsHandler chooseTicketsH) {
        assert isFxApplicationThread();
        //tickets selection window
        Stage initialTicketsSelectorStage = initialTicketsSelector(options, chooseTicketsH);
        initialTicketsSelectorStage.show();
    }

    public void drawCard(ActionHandler.DrawCardHandler drawCardH) {
        assert isFxApplicationThread();
        drawCard.setValue(slot -> {
            clearProperties();
            drawCardH.onDrawCard(slot);
        });
    }

    public void chooseClaimCards(List<SortedBag<Card>> options, ActionHandler.ChooseCardsHandler chooseCardsH) {
        assert isFxApplicationThread();
        //cards selection window
        Stage cardsSelectorStage = cardsSelector(options, chooseCardsH);
        cardsSelectorStage.show();
    }

    public void chooseAdditionalCards(List<SortedBag<Card>> additionalCards, ActionHandler.ChooseCardsHandler chooseCardsH) {
        assert isFxApplicationThread();
        //In Game cards selection window
        Stage additionalCardsSelectorStage = additionalCardsSelector(additionalCards, chooseCardsH);
        additionalCardsSelectorStage.show();
    }

    private void clearProperties() {
        drawCard.setValue(null);
        drawTickets.setValue(null);
        claimRoute.setValue(null);
    }

    private Stage additionalCardsSelector(List<SortedBag<Card>> options, ActionHandler.ChooseCardsHandler chooseCardsH) {
        Stage additionalCardsSelectorStage = new Stage(StageStyle.UTILITY);

        VBox additionalCardsSelectorBox = new VBox();

        TextFlow additionalCardsSelectorTextFlow = new TextFlow();
        Text additionalCardsSelectorText = new Text(StringsFr.CHOOSE_ADDITIONAL_CARDS);
        additionalCardsSelectorTextFlow.getChildren().add(additionalCardsSelectorText);

        ListView<SortedBag<Card>> additionalCardsSelectorListView = new ListView<>(FXCollections.observableArrayList(options));
        additionalCardsSelectorListView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        Button additionalCardsSelectorButton = new Button("Choisir");
        additionalCardsSelectorButton.setOnAction(e -> {
            additionalCardsSelectorStage.hide();
            chooseCardsH.onChooseCards(additionalCardsSelectorListView.getSelectionModel().getSelectedItem());
        });

        additionalCardsSelectorBox.getChildren()
                .addAll(additionalCardsSelectorTextFlow, additionalCardsSelectorButton, additionalCardsSelectorListView);

        Scene additionalCardsSelectorScene = new Scene(additionalCardsSelectorBox);
        additionalCardsSelectorScene.getStylesheets().add("chooser.css");
        additionalCardsSelectorStage.setScene(additionalCardsSelectorScene);
        additionalCardsSelectorStage.setTitle(StringsFr.CHOOSE_ADDITIONAL_CARDS);
        additionalCardsSelectorStage.initModality(Modality.WINDOW_MODAL);
        additionalCardsSelectorStage.initOwner(mainView);
        additionalCardsSelectorStage.setOnCloseRequest(Event::consume);
        return additionalCardsSelectorStage;
    }

    private Stage cardsSelector(List<SortedBag<Card>> options, ActionHandler.ChooseCardsHandler chooseCardsH) {
        Stage initialCardsSelectorStage = new Stage(StageStyle.UTILITY);

        TextFlow cardsSelectorTextFlow = new TextFlow();
        Text cardsSelectorText = new Text(StringsFr.CHOOSE_CARDS);
        cardsSelectorTextFlow.getChildren().add(cardsSelectorText);

        ListView<SortedBag<Card>> cardsSelectorListView = new ListView<>(FXCollections.observableArrayList(options));
        cardsSelectorListView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        ObservableList<SortedBag<Card>> chosenCards = cardsSelectorListView.getSelectionModel().getSelectedItems();

        Button cardsSelectorButton = new Button("Choisir");
        cardsSelectorButton.disableProperty().bind(Bindings.size(chosenCards).isEqualTo(0));

        cardsSelectorButton.setOnAction(e -> {
            initialCardsSelectorStage.hide();
            chooseCardsH.onChooseCards(cardsSelectorListView.getSelectionModel().getSelectedItem());
        });

        VBox cardsSelectorBox = new VBox();
        cardsSelectorBox.getChildren().addAll(cardsSelectorTextFlow, cardsSelectorButton, cardsSelectorListView);

        Scene cardsSelectorScene = new Scene(cardsSelectorBox);
        cardsSelectorScene.getStylesheets().add("chooser.css");

        initialCardsSelectorStage.setScene(cardsSelectorScene);
        initialCardsSelectorStage.setTitle(StringsFr.CARDS_CHOICE);
        initialCardsSelectorStage.initModality(Modality.WINDOW_MODAL);
        initialCardsSelectorStage.initOwner(mainView);
        initialCardsSelectorStage.setOnCloseRequest(Event::consume);
        return initialCardsSelectorStage;
    }

    private Stage initialTicketsSelector(SortedBag<Ticket> options, ActionHandler.ChooseTicketsHandler chooseTicketsH) {
        Stage initialTicketsSelectorStage = new Stage(StageStyle.UTILITY);

        TextFlow ticketsSelectorTextFlow = new TextFlow();
        Text ticketsSelectorText = new Text(String.format(StringsFr.CHOOSE_TICKETS, Constants.IN_GAME_TICKETS_COUNT, "s"));
        ticketsSelectorTextFlow.getChildren().add(ticketsSelectorText);

        ListView<Ticket> ticketsSelectorListView = new ListView<>(FXCollections.observableArrayList(options.toList()));
        ticketsSelectorListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Button ticketsSelectorButton = new Button("Choisir");
        ObservableList<Ticket> chosenTickets = ticketsSelectorListView.getSelectionModel().getSelectedItems();
        ticketsSelectorButton.disableProperty().bind(Bindings.size(chosenTickets)
                .lessThan(ticketsSelectorListView.getItems().size() - Constants.DISCARDABLE_TICKETS_COUNT));
        ticketsSelectorButton.setOnAction(e -> {
            initialTicketsSelectorStage.hide();
            chooseTicketsH.onChooseTickets(SortedBag.of(chosenTickets));
        });

        VBox ticketsSelectorBox = new VBox();
        ticketsSelectorBox.getChildren().addAll(ticketsSelectorTextFlow, ticketsSelectorButton, ticketsSelectorListView);

        Scene ticketsSelectorScene = new Scene(ticketsSelectorBox);
        ticketsSelectorScene.getStylesheets().add("chooser.css");

        initialTicketsSelectorStage.setScene(ticketsSelectorScene);
        initialTicketsSelectorStage.setTitle(StringsFr.TICKETS_CHOICE);
        initialTicketsSelectorStage.initModality(Modality.WINDOW_MODAL);
        initialTicketsSelectorStage.initOwner(mainView);
        initialTicketsSelectorStage.setOnCloseRequest(Event::consume);
        return initialTicketsSelectorStage;
    }

}
