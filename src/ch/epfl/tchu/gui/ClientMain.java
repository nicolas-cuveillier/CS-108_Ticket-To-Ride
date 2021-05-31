package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;

import java.util.List;

/**<h1>ClientMain</h1>
 * Contains the main program for the game server.
 * 
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public final class ClientMain {
    private final static String DEFAULT_HOSTNAME = "localhost";
    private final static int DEFAULT_PORT = 5108;
    private final static String DEFAULT_NAME = "Player_";
    
    private String hostname;
    private int port;
    private String name;

    public void init(String[] args) {
        List<String> parameters = List.of(args);
        hostname = parameters.get(0).isBlank()? DEFAULT_HOSTNAME:parameters.get(0);
        port = parameters.get(1).isBlank()? DEFAULT_PORT:Integer.parseInt(parameters.get(1));
        name = parameters.get(2).isBlank()? DEFAULT_NAME:parameters.get(2);
    }
    
    /**
     * Firstly, analysing the arguments passed to the program to determine the names of the two players. Then, waiting
     * for a connection from the client on port and creating the two players, the first being a graphical player,
     * the second a proxy for the remote player on the client. Finally, starting the thread that runs the game, which
     * execute the play method of Game.
     *
     */
    public void start() {
        Thread t;
        try {
            RemotePlayerClient remotePlayerClient = new RemotePlayerClient(new GraphicalPlayerAdapter(), hostname, port, name);
            t = new Thread(remotePlayerClient::run);
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

    }

}
