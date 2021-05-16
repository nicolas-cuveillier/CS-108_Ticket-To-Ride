package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

/**<h1>ServerMain</h1>
 * Contains the main program of the local client (local player).
 * 
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public final class ServerMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starting point of the server part of the tCHu game. Firstly parsing the arguments passed to the program to
     * determine the host name and port number of the server. Then, creating a remote client associated with a graphical
     * player and starting the network access thread, which execute the run method of the remote client.
     *
     * @param primaryStage argument that is ignored in the context of the tCHu game
     */
    @Override
    public void start(Stage primaryStage) {
        List<String> parameters = getParameters().getRaw();
        String name1 = parameters.get(0);
        String name2 = parameters.get(1);

        try {
            ServerSocket s0 = new ServerSocket(5108);
            Socket s = s0.accept();

            GraphicalPlayerAdapter graphicalPlayer = new GraphicalPlayerAdapter();
            RemotePlayerProxy remotePlayerProxy = new RemotePlayerProxy(s);
            Map<PlayerId, String> playersName = Map.of(PLAYER_1, name1, PLAYER_2, name2);
            Map<PlayerId, Player> players = Map.of(PLAYER_1, graphicalPlayer, PLAYER_2, remotePlayerProxy);

            new Thread(() -> Game.play(players, playersName, SortedBag.of(ChMap.tickets()), new Random())).start();
        } catch (Exception e) {
            System.exit(0);
        }
    }
}