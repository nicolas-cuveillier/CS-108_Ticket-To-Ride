package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.stage.Stage;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * <h1>ServerMain</h1>
 * Contains the main program of the local client (local player).
 *
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public final class ServerMain {

    private final static int DEFAULT_PORT = 5108;
    private final String[] names = new String[]{"Ada", "Charles", "Player_", "Player_", "Player_"};
    private int port;
    private Boolean localPlayer = false;
    private String localPlayerName = "Ada";
    private ServerSocket s0;
    public static int nbPlayers;


    //todo javadoc + suprimer celle du dessus
    public void init(String[] args) {
        List<String> parameters = List.of(args);
        nbPlayers = Integer.parseInt(parameters.get(0));

        port = parameters.get(1).isBlank() ? DEFAULT_PORT : Integer.parseInt(parameters.get(1));
        PlayerId.NUMBER_OF_PLAYER = nbPlayers;
        localPlayer = parameters.size() >= 3;
        localPlayerName = localPlayer ? parameters.get(2).isBlank() ? "Ada" : parameters.get(2) : "Ada";


    }

    /**
     * Starting point of the server part of the tCHu game. Firstly parsing the arguments passed to the program to
     * determine the host name and port number of the server. Then, creating a remote client associated with a graphical
     * player and starting the network access thread, which execute the run method of the remote client.
     *
     * @param primaryStage argument that is ignored in the context of the tCHu game
     */
    public void start(Stage primaryStage) {

        System.out.printf("Launching a server for %s players\n", nbPlayers);

        try {
            s0 = new ServerSocket(port);
            Map<PlayerId, Player> players = new LinkedHashMap<>(nbPlayers);
            Map<PlayerId, String> playerNames = new LinkedHashMap<>(nbPlayers);


            Socket[] sockets = new Socket[nbPlayers];


            for (int i = 0; i < nbPlayers; i++) {
                if (localPlayer && i == 0) {
                    players.put(PlayerId.CURRENT_PLAYERS().get(i), new RemotePlayerProxy(sockets[i], String.format("Player_%s", (i + 1)), nbPlayers));
                    playerNames.put(PlayerId.CURRENT_PLAYERS().get(i), localPlayerName);
                } else {
                    System.out.printf("Waiting on player %s   ---   (%s/%s connected)\n", (i+1), i, nbPlayers);
                    sockets[i] = s0.accept();
                    players.put(PlayerId.CURRENT_PLAYERS().get(i), new RemotePlayerProxy(sockets[i], i >= 2 ? String.format("Player_%s", (i + 1)) : names[i], nbPlayers));
                    playerNames.put(PlayerId.CURRENT_PLAYERS().get(i), players.get(PlayerId.CURRENT_PLAYERS().get(i)).name());
                    System.out.printf("Player %s connected !   (%s/%s)\n\n",  playerNames.get(PlayerId.CURRENT_PLAYERS().get(i)), (i+1), nbPlayers);

                }
            }
            new Thread(() -> Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), new Random())).start();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}