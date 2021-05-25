package ch.epfl.tchu.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Launcher extends Application {
    public static final int PLAYER_NUMBER = 3;

    //public static void main(String[] args) {
     ///   launch(args);
    //}

    @Override
    public void start(Stage primaryStage) {
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

       /* BooleanBinding binding = (textField.textProperty().isNotEmpty()
                .and(name1textField.textProperty().isNotEmpty())
                .and(name2textField.textProperty().isNotEmpty())
                .and(name3textField.textProperty().isEqualTo("3").get() ? name3textField.textProperty().isNotEmpty()
                        : name3textField.textProperty().isEmpty()))
                .or(hostName.textProperty().isNotEmpty().and(port.textProperty().isNotEmpty()));*/

       //todo find binding btn.visibleProperty().bind(binding);

        btn.setOnMouseClicked(e -> {
            if (yesButton.selectedProperty().get()) {
                if (textField.getText().equals("3")) {
                    ServerMain.launch(name1textField.getText(), name2textField.getText(), name3textField.getText());
                } else {
                    ServerMain.launch(name1textField.getText(), name2textField.getText());
                }
            } else {
                String[] args = new String[]{
                        hostNameText.getText(),
                        portName.getText()};
               ClientMain.main(args);
            }
        });
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
}
