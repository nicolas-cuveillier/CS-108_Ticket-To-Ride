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
     * Starting point of the server part of the tCHu game. Firstly, analysing the arguments passed to the program to
     * determine the names of the two players. Then, waiting for a connection from the client on port and creating the
     * two players, the first being a graphical player, the second a RemotePlayerProxy associated with a
     * RemotePLayerClient that will start with the client. Finally, starting the thread that runs the game, which
     * execute the play method of Game.
     *
     * @param primaryStage argument that is ignored in the context of the tCHu game
     */
    @Override
    public void start(Stage primaryStage) {
        String firstPlayerName = "Ada";
        String secondPlayerName = "Charles";
        
        List<String> parameters = getParameters().getRaw();
        
        if(parameters.size() == 2) {
            firstPlayerName = parameters.get(0);
            secondPlayerName = parameters.get(1);
        }

        try {
            ServerSocket s0 = new ServerSocket(5108);
            Socket s = s0.accept();

            GraphicalPlayerAdapter graphicalPlayer = new GraphicalPlayerAdapter();
            RemotePlayerProxy remotePlayerProxy = new RemotePlayerProxy(s);
            Map<PlayerId, String> playersName = Map.of(PLAYER_1, firstPlayerName, PLAYER_2, secondPlayerName);
            Map<PlayerId, Player> players = Map.of(PLAYER_1, graphicalPlayer, PLAYER_2, remotePlayerProxy);

            new Thread(() -> Game.play(players, playersName, SortedBag.of(ChMap.tickets()), new Random())).start();
        } catch (Exception e) {
            System.exit(0);
        }
    }
}