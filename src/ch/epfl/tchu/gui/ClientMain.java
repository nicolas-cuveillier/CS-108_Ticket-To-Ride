package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

/**
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public final class ClientMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){

        List<String> parameters = getParameters().getRaw();
        String hostName = parameters.get(0);
        int port = Integer.parseInt(parameters.get(1));

        try {
            RemotePlayerClient remotePlayerClient = new RemotePlayerClient(new GraphicalPlayerAdapter(), hostName, port);
            new Thread(() -> remotePlayerClient.run()).start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

    }
}
