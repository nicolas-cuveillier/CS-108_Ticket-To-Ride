package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

/**<h1>ClientMain</h1>
 * Contains the main program for the game server.
 * 
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public final class ClientMain extends Application {
    private final static String DEFAULT_HOSTNAME = "localhost";
    private final static int DEFAULT_PORT = 5108;
    
    private String hostname;
    private int port;
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {   //todo make this info be returned by the pop-up
        List<String> parameters = getParameters().getRaw();
        if(parameters.size() == 2) {
            hostname = parameters.get(0);
            port = Integer.parseInt(parameters.get(1));
        } else {
            hostname = DEFAULT_HOSTNAME;
            port = DEFAULT_PORT;
        }
        super.init();
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
        try {
            RemotePlayerClient remotePlayerClient = new RemotePlayerClient(new GraphicalPlayerAdapter(), hostname, port);
            new Thread(remotePlayerClient::run).start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

    }
    
    /**
     * Called when the application should stop.
     * Causes the JVM to stop, and the program to exit.
     */
    @Override
    public void stop() {
        System.exit(0);
    }
    
}
