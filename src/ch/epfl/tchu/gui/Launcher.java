package ch.epfl.tchu.gui;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static javafx.geometry.HPos.RIGHT;

public class Launcher extends Application {
    private final String DEFAULT_HOSTNAME = "localhost";
    private final int DEFAULT_PORT = 5108;

    public static final int PLAYER_NUMBER = 3;
    private final String DEFAULT_P1NAME = "Ada";
    private final String DEFAULT_P2NAME = "Charles";
    private final String Default_PXNAME = "Player_";


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // TODO Auto-generated method stub
        LauncherViewCreator.createLauncherView(primaryStage);
    }
}

final class LauncherViewCreator {

    private LauncherViewCreator() {
    }

    public static void createLauncherView(Stage primaryStage) {
        VBox box = new VBox();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        Text sceneTitle = new Text("Welcome on the Tchu Game");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);

        Label userName = new Label("Will you host the game ? ");
        grid.add(userName, 0, 1);

        ToggleButton yesButton = new ToggleButton("Yes/No");
        grid.add(yesButton, 1, 1);

        Label text = new Label("number of players : ");
        text.visibleProperty().bind(yesButton.selectedProperty());
        grid.add(text, 0, 2);
        TextField textField = new TextField();
        textField.visibleProperty().bind(yesButton.selectedProperty());
        grid.add(textField, 1, 2);
        Label errorText = new Label("invalid number");
        errorText.visibleProperty().bind((textField.textProperty().isNotEqualTo("2")
                .and(textField.textProperty().isNotEqualTo("3")))
                .and(textField.textProperty().isNotEmpty()));
        grid.add(errorText, 3, 2);

        Label name1 = new Label("Enter the first player's name :");
        name1.visibleProperty().bind(yesButton.selectedProperty());
        grid.add(name1, 0, 3);
        TextField name1textField = new TextField();
        name1textField.visibleProperty().bind(yesButton.selectedProperty());
        grid.add(name1textField, 1, 3);
        Label name2 = new Label("Enter the second player's name :");
        name2.visibleProperty().bind(yesButton.selectedProperty());
        grid.add(name2, 0, 4);
        TextField name2textField = new TextField();
        name2textField.visibleProperty().bind(yesButton.selectedProperty());
        grid.add(name2textField, 1, 4);
        Label name3 = new Label("Enter the last player's name :");
        name3.visibleProperty().bind(textField.textProperty().isEqualTo("3").and(yesButton.selectedProperty()));
        grid.add(name3, 0, 5);
        TextField name3textField = new TextField();
        name3textField.visibleProperty().bind(textField.textProperty().isEqualTo("3").and(yesButton.selectedProperty()));
        grid.add(name3textField, 1, 5);

        Label hostName = new Label("HostName : ");
        hostName.visibleProperty().bind(yesButton.selectedProperty().not());
        grid.add(hostName, 0, 6);
        TextField hostNameText = new TextField();
        hostNameText.visibleProperty().bind(yesButton.selectedProperty().not());
        grid.add(hostNameText, 1, 6);
        Label port = new Label("Port : ");
        port.visibleProperty().bind(yesButton.selectedProperty().not());
        grid.add(port, 2, 6);
        TextField portName = new TextField();
        portName.visibleProperty().bind(yesButton.selectedProperty().not());
        grid.add(portName, 3, 6);

        Button btn = new Button("play !");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        hbBtn.visibleProperty().bind(playButtonBinding(yesButton, hostNameText, portName, textField, name1textField, name2textField, name3textField));
        grid.add(hbBtn, 4, 6);

        box.getChildren().add(grid);
        Scene scene = new Scene(box, 680, 280);
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.setScene(scene);
        stage.setTitle("tChu");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.setX(500);
        stage.setY(400);
        stage.show();
    }

    private static BooleanBinding playButtonBinding(ToggleButton but, TextField hostName, TextField port,
                                                    TextField playersNumber, TextField name1,
                                                    TextField name2, TextField name3) {
        if (but.selectedProperty().get()) {
            return playersNumber.textProperty().isNotEmpty()
                    .and(name1.textProperty().isNotEmpty())
                    .and(name2.textProperty().isNotEmpty());
                    //.and(playersNumber.textProperty().isEqualTo("3").get() ? name3.textProperty().isNotEmpty()
                            //: name3.textProperty().isEmpty());
        } else {
            return hostName.textProperty().isNotEmpty().and(port.textProperty().isNotEmpty());
        }


    }


}
