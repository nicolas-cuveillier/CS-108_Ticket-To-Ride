package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

/**
 * Contains the main program for the tchu server.
 * 
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public final class ClientMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Firstly, analysing the arguments passed to the program to determine the names of the two players. Then, waiting
     * for a connection from the client on port and creating the two players, the first being a graphical player,
     * the second a proxy for the remote player on the client. Finally, starting the thread that runs the game, which
     * execute the play method of Game.
     *
     * @param primaryStage argument that is ignored in the context of the tCHu game
     */
    @Override
    public void start(Stage primaryStage) {

        List<String> parameters = getParameters().getRaw();
        String hostName = parameters.get(0);
        int port = Integer.parseInt(parameters.get(1));

        try {
            RemotePlayerClient remotePlayerClient = new RemotePlayerClient(new GraphicalPlayerAdapter(), hostName, port);
            new Thread(remotePlayerClient::run).start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

    }
}
