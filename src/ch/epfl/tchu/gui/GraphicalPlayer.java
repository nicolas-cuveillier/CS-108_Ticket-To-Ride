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

import static javafx.application.Platform.isFxApplicationThread;

/**
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
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
     * @param player      the id of the player for which the GraphicalPlayer is instantiated
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
        Node infoView = InfoViewCreator.createInfoView(this.player, this.playersName, gameState, information);
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
     * @param playerState  the current playerState
     */
    public void setState(PublicGameState newGameState, PlayerState playerState) {
        assert isFxApplicationThread();
        gameState.setState(newGameState, playerState);
    }

    /**
     * add the message to the left hand side of the gui to display it and remove one if the number of messages exceed 5
     *
     * @param message the message that will be display in the InfoView at the left of the screen
     */
    public void receiveInfo(String message) {
        assert isFxApplicationThread();
        if (information.size() == 5)
            information.remove(0);
        information.add(new Text(message));
    }

    /**
     * method that handle the choice of the player in a turn with setting the different ActionHandler in their
     * correspondent property. If the action is possible, all past properties are assigned to null and this property is set
     *
     * @param drawTicketsH an instance of DrawTicketsHandler which describe how the graphical player is drawing a ticket
     * @param drawCardH    an instance of DrawCardsHandler which describe how the graphical player is drawing a card
     * @param claimRouteH  an instance of ClaimRouteHandler which describe how the graphical player is claiming a route
     */
    public void startTurn(ActionHandler.DrawTicketsHandler drawTicketsH, ActionHandler.DrawCardHandler drawCardH, ActionHandler.ClaimRouteHandler claimRouteH) {
        assert isFxApplicationThread();
        drawTickets.setValue(!gameState.canDrawTickets() ? null : () -> {
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

    /**
     * create the ticket's selector pop-up that will force the player to choose a certain minimum number of tickets
     *
     * @param options        a sortedBag of the tickets from which the player will have to choose
     * @param chooseTicketsH an instance of ChooseTicketsHandler that will be use when the player's choice will be made
     */
    public void chooseTickets(SortedBag<Ticket> options, ActionHandler.ChooseTicketsHandler chooseTicketsH) {
        assert isFxApplicationThread();
        //tickets selection window
        Stage initialTicketsSelectorStage = initialTicketsSelector(options, chooseTicketsH);
        initialTicketsSelectorStage.show();
    }

    /**
     * allows the player to choose a card, either one of the five face-up cards or the one at the top of the deck.
     * According to the chosen card the handler is called up with the player's choice to make the move. This method is
     * intended to be called up when the player has already drawn a first card and must now draw the second.
     *
     * @param drawCardH an instance of DrawCardsHandler which describe how the graphical player is drawing a card
     */
    public void drawCard(ActionHandler.DrawCardHandler drawCardH) {
        assert isFxApplicationThread();
        drawCard.setValue(slot -> {
            clearProperties();
            drawCardH.onDrawCard(slot);
        });
    }

    /**
     * create the claim cards' selector pop-up that will force the player to choose a sortedBag of cards that
     * will be used to claim the route
     *
     * @param options      a List of all the different option that the player has to claim the route
     * @param chooseCardsH an instance of ChooseCardsHandler that will be use when the player's choice will be made
     */
    public void chooseClaimCards(List<SortedBag<Card>> options, ActionHandler.ChooseCardsHandler chooseCardsH) {
        assert isFxApplicationThread();
        //cards selection window
        Stage cardsSelectorStage = cardsSelector(options, chooseCardsH, StringsFr.CHOOSE_CARDS,false);
        cardsSelectorStage.show();
    }

    /**
     * create the additional cards' selector pop-up that will force the player to choose a sortedBag of cards that
     * will be used as additional cards to claim the route
     *
     * @param additionalCards a List of all the different option of additional cards that the player has to claim the route
     * @param chooseCardsH    an instance of ChooseCardsHandler that will be use when the player's choice will be made
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> additionalCards, ActionHandler.ChooseCardsHandler chooseCardsH) {
        assert isFxApplicationThread();
        //In Game cards selection window
        Stage additionalCardsSelectorStage = cardsSelector(additionalCards, chooseCardsH,StringsFr.CHOOSE_ADDITIONAL_CARDS,true);
        additionalCardsSelectorStage.show();
    }

    private void clearProperties() {
        drawCard.setValue(null);
        drawTickets.setValue(null);
        claimRoute.setValue(null);
    }

    /**
     * create the claim cards selector panel, a stage own by the main stage, according to a list of option and a
     * ChooseCardsHandler
     */
    private Stage cardsSelector(List<SortedBag<Card>> options, ActionHandler.ChooseCardsHandler chooseCardsH,String title, boolean isAdditionalCardSelector){
        Stage initialCardsSelectorStage = new Stage(StageStyle.UTILITY);

        TextFlow cardsSelectorTextFlow = new TextFlow();
        Text cardsSelectorText = new Text(title);
        cardsSelectorTextFlow.getChildren().add(cardsSelectorText);

        ListView<SortedBag<Card>> cardsSelectorListView = new ListView<>(FXCollections.observableArrayList(options));
        cardsSelectorListView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        ObservableList<SortedBag<Card>> chosenCards = cardsSelectorListView.getSelectionModel().getSelectedItems();

        Button cardsSelectorButton = new Button("Choisir");
        if(!isAdditionalCardSelector) cardsSelectorButton.disableProperty().bind(Bindings.size(chosenCards).isEqualTo(0));

        cardsSelectorButton.setOnAction(e -> {
            initialCardsSelectorStage.hide();
            if(!chosenCards.isEmpty()){
                chooseCardsH.onChooseCards(chosenCards.get(0));
            }
        });

        VBox cardsSelectorBox = new VBox();
        cardsSelectorBox.getChildren().addAll(cardsSelectorTextFlow, cardsSelectorButton, cardsSelectorListView);

        return setStageFromBox(initialCardsSelectorStage, cardsSelectorBox,StringsFr.CARDS);
    }

    /**
     * create the tickets selector panel, a stage own by the main stage, according to a sortedBag of tickets and a
     * ChooseTicketsHandler
     */
    private Stage initialTicketsSelector(SortedBag<Ticket> options, ActionHandler.ChooseTicketsHandler chooseTicketsH) {
        Stage initialTicketsSelectorStage = new Stage(StageStyle.UTILITY);

        ListView<Ticket> ticketsSelectorListView = new ListView<>(FXCollections.observableArrayList(options.toList()));
        ticketsSelectorListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ObservableList<Ticket> chosenTickets = ticketsSelectorListView.getSelectionModel().getSelectedItems();

        Button ticketsSelectorButton = new Button("Choisir");
        ticketsSelectorButton.disableProperty().bind(Bindings.size(chosenTickets)
                .lessThan(ticketsSelectorListView.getItems().size() - Constants.DISCARDABLE_TICKETS_COUNT));
        ticketsSelectorButton.setOnAction(e -> {
            initialTicketsSelectorStage.hide();
            chooseTicketsH.onChooseTickets(SortedBag.of(chosenTickets));
        });

        TextFlow ticketsSelectorTextFlow = new TextFlow();
        Text ticketsSelectorText = new Text(String.format(StringsFr.CHOOSE_TICKETS, ticketsSelectorListView.getItems().size() - Constants.DISCARDABLE_TICKETS_COUNT, "s"));
        ticketsSelectorTextFlow.getChildren().add(ticketsSelectorText);

        VBox ticketsSelectorBox = new VBox();
        ticketsSelectorBox.getChildren().addAll(ticketsSelectorTextFlow, ticketsSelectorButton, ticketsSelectorListView);

        return setStageFromBox(initialTicketsSelectorStage, ticketsSelectorBox,StringsFr.TICKETS);
    }

    private Stage setStageFromBox(Stage stage, VBox box,String name) {
        Scene selectorScene = new Scene(box);
        selectorScene.getStylesheets().add("chooser.css");

        stage.setScene(selectorScene);
        stage.setTitle(name);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(mainView);
        stage.setOnCloseRequest(Event::consume);
        return stage;
    }

}
