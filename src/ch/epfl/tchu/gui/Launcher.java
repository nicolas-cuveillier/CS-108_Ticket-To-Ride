package ch.epfl.tchu.gui;

import javafx.application.Application;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
    public void start(Stage primaryStage) throws Exception {
        // TODO Auto-generated method stub
        LauncherViewCreator.createLauncherView(primaryStage);
    }
}

final class LauncherViewCreator {

    private LauncherViewCreator() {
    }

    public static void createLauncherView(Stage primaryStage) {
        VBox box = new VBox();
        Scene scene = new Scene(box);

        Stage stage = new Stage(StageStyle.UTILITY);
        stage.setScene(scene);
        stage.setTitle("Début de la Partie");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.setX(500);
        stage.setY(400);
        stage.show();
    }

}
