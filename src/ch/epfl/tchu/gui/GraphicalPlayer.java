package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public final class GraphicalPlayer {
    private final PlayerId player;
    private final Map<PlayerId, String> playersName;
    private ObservableGameState gameState;

    public GraphicalPlayer(PlayerId player, Map<PlayerId, String> playersName) {
        this.player = player;
        this.playersName = Map.copyOf(playersName);
        this.gameState = new ObservableGameState(player);

        ObjectProperty<ActionHandler.ClaimRouteHandler> claimRoute =
                new SimpleObjectProperty<>();
        ObjectProperty<ActionHandler.DrawTicketsHandler> drawTickets =
                new SimpleObjectProperty<>();
        ObjectProperty<ActionHandler.DrawCardHandler> drawCard =
                new SimpleObjectProperty<>();

        Node mapView = MapViewCreator
                .createMapView(gameState, claimRoute, (options, handler) -> {
                    //TODO
                });
        Node cardsView = DecksViewCreator
                .createCardsView(gameState, drawTickets, drawCard);
        Node handView = DecksViewCreator
                .createHandView(gameState);
        Node infoView = InfoViewCreator
                .createInfoView(PLAYER_1, playersName, gameState, FXCollections.observableArrayList());
        BorderPane borderPane =
                new BorderPane(mapView, null, cardsView, handView, infoView);
        Stage mainView = new Stage();
        mainView.setScene(new Scene(borderPane));
        mainView.setTitle("tChu" + "\u2014" + playersName.get(player));

        //cards selection window
        setInitialCardsSelector(mainView);

        //tickets selection window
        setInitialTicketsSelector(mainView);

        //In Game cards selection window
        setAdditionalCardsSelector(mainView);

        mainView.show();
    }
    

    private static void setAdditionalCardsSelector(Stage primaryStage) {
        Stage additionalCardsSelectorStage = new Stage(StageStyle.UTILITY);

        VBox additionalCardsSelectorBox = new VBox();

        TextFlow additionalCardsSelectorTextFlow = new TextFlow();
        Text additionalCardsSelectorText = new Text(StringsFr.CHOOSE_ADDITIONAL_CARDS);
        additionalCardsSelectorTextFlow.getChildren().add(additionalCardsSelectorText);

        ListView<SortedBag<Card>> additionalCardsSelectorListView = new ListView<>();//TODO
        additionalCardsSelectorListView.setCellFactory(v ->
                new TextFieldListCell<>(new CardBagStringConverter()));

        Button additionalCardsSelectorButton = new Button("Choisir");
        additionalCardsSelectorButton.setOnAction(e -> {
            additionalCardsSelectorStage.hide();
            //TODO add chooseAdditionalCards
        });

        additionalCardsSelectorBox.getChildren()
                .addAll(additionalCardsSelectorTextFlow, additionalCardsSelectorButton, additionalCardsSelectorListView);

        Scene additionalCardsSelectorScene = new Scene(additionalCardsSelectorBox);
        additionalCardsSelectorScene.getStylesheets().add("chooser.css");

        additionalCardsSelectorStage.setScene(additionalCardsSelectorScene);
        additionalCardsSelectorStage.setTitle(StringsFr.CHOOSE_ADDITIONAL_CARDS);
        additionalCardsSelectorStage.initOwner(primaryStage);
        additionalCardsSelectorStage.initModality(Modality.WINDOW_MODAL);
    }

    private static void setInitialCardsSelector(Stage primaryStage) {
        Stage initialCardsSelectorStage = new Stage(StageStyle.UTILITY);

        TextFlow cardsSelectorTextFlow = new TextFlow();
        Text cardsSelectorText = new Text(StringsFr.CHOOSE_CARDS);
        cardsSelectorTextFlow.getChildren().add(cardsSelectorText);

        ListView<SortedBag<Card>> cardsSelectorListView = new ListView<>();//TODO
        cardsSelectorListView.setCellFactory(v ->
                new TextFieldListCell<>(new CardBagStringConverter()));

        Button cardsSelectorButton = new Button("Choisir");
        cardsSelectorButton.disabledProperty()
                .addListener(o -> Bindings.size(cardsSelectorListView.getSelectionModel().getSelectedItems())
                        .isEqualTo(0));
        cardsSelectorButton.setOnAction(e -> {
            initialCardsSelectorStage.hide();
            //TODO add chooseClaimCards
        });

        VBox cardsSelectorBox = new VBox();
        cardsSelectorBox.getChildren().addAll(cardsSelectorTextFlow, cardsSelectorButton, cardsSelectorListView);

        Scene cardsSelectorScene = new Scene(cardsSelectorBox);
        cardsSelectorScene.getStylesheets().add("chooser.css");

        initialCardsSelectorStage.setScene(cardsSelectorScene);
        initialCardsSelectorStage.setTitle(StringsFr.CARDS_CHOICE);
        initialCardsSelectorStage.initOwner(primaryStage);
        initialCardsSelectorStage.initModality(Modality.WINDOW_MODAL);

        initialCardsSelectorStage.setOnCloseRequest(Event::consume);
    }

    private static void setInitialTicketsSelector(Stage primaryStage) {
        Stage initialTicketsSelectorStage = new Stage(StageStyle.UTILITY);

        TextFlow ticketsSelectorTextFlow = new TextFlow();
        Text ticketsSelectorText = new Text(String.format(StringsFr.CHOOSE_TICKETS, Constants.IN_GAME_TICKETS_COUNT, "s"));
        ticketsSelectorTextFlow.getChildren().add(ticketsSelectorText);

        ListView<SortedBag<Ticket>> ticketsSelectorListView = new ListView<>();//TODO

        Button ticketsSelectorButton = new Button("Choisir");
        ticketsSelectorButton.disabledProperty()
                .addListener(o -> Bindings.size(ticketsSelectorListView.getSelectionModel().getSelectedItems())
                        .isEqualTo(ticketsSelectorListView.getItems().size() - 2));
        ticketsSelectorButton.setOnAction(e -> {
            initialTicketsSelectorStage.hide();
            //TODO add chooseTickets
        });

        VBox ticketsSelectorBox = new VBox();
        ticketsSelectorBox.getChildren().addAll(ticketsSelectorTextFlow, ticketsSelectorButton, ticketsSelectorListView);

        Scene ticketsSelectorScene = new Scene(ticketsSelectorBox);
        ticketsSelectorScene.getStylesheets().add("chooser.css");


        initialTicketsSelectorStage.setScene(ticketsSelectorScene);
        initialTicketsSelectorStage.setTitle(StringsFr.TICKETS_CHOICE);
        initialTicketsSelectorStage.initOwner(primaryStage);
        initialTicketsSelectorStage.initModality(Modality.WINDOW_MODAL);

        initialTicketsSelectorStage.setOnCloseRequest(Event::consume);
    }
}
