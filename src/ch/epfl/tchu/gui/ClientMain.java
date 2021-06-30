package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

/**
 * <h1>ClientMain</h1>
 * Contains the main program for the game server.
 *
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public final class ClientMain extends Application {
    private final static String DEFAULT_HOSTNAME = "localhost";
    private final static int DEFAULT_PORT = 5108;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Firstly parsing the arguments passed to the program to determine the hostname and port number or use default
     * name and port. Then, creating a RemotePLayerClient according to an instance of a GraphicalPlayerAdapter used by
     * the human player. From this point, the client begin starting the network access thread, which execute the run
     * method of the remote client.
     *
     * @param primaryStage argument that is ignored in the context of the tCHu game
     */
    @Override
    public void start(Stage primaryStage) {
        List<String> parameters = getParameters().getRaw();

        final String hostname = (parameters.size() == 2) ? parameters.get(0) : DEFAULT_HOSTNAME;
        final int port = (parameters.size() == 2) ? Integer.parseInt(parameters.get(1)) : DEFAULT_PORT;

        RemotePlayerClient remotePlayerClient = new RemotePlayerClient(new GraphicalPlayerAdapter(), hostname, port);
        new Thread(remotePlayerClient::run).start();

    }

}
