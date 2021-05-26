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
public final class ServerMain extends Application {
    private final String[] names = new String[] {"Ada", "Charles", "Player_", "Player_", "Player_"};
    private Boolean localPlayer = false;
    private String localPlayerName = "Ada";
    ServerSocket s0;
    private int nbPlayers = 2;

    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void init() throws Exception {
        super.init();
        List<String> parameters = getParameters().getRaw();
        nbPlayers = Integer.parseInt(parameters.get(0));
    }
    
    public void init(String[] args) throws Exception {
        List<String> parameters = List.of(args);
        nbPlayers = Integer.parseInt(parameters.get(0));
        localPlayer = parameters.size() >= 2;
        localPlayerName = localPlayer?parameters.get(1):"";
        
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
        try {
            s0 = new ServerSocket(5108);
            Map<PlayerId, Player> players = new LinkedHashMap<>(nbPlayers);
            Map<PlayerId, String> playerNames = new LinkedHashMap<>(nbPlayers);


            Socket[] sockets = new Socket[nbPlayers];

            
            for(int i = 0; i < nbPlayers; i++) {
                if(localPlayer && i == 0) {
                    players.put(PlayerId.CURRENT_PLAYERS.get(i), new RemotePlayerProxy(sockets[i], i));
                    playerNames.put(PlayerId.CURRENT_PLAYERS.get(i), players.get(PlayerId.CURRENT_PLAYERS.get(i)).name() == String.format("Player_%n", i)?names[i]:players.get(PlayerId.CURRENT_PLAYERS.get(i)).name());
                }else {
                    System.out.println("Waiting on player: " + i);
                    sockets[i] = s0.accept();
                    players.put(PlayerId.CURRENT_PLAYERS.get(i), new RemotePlayerProxy(sockets[i], i));
                    playerNames.put(PlayerId.CURRENT_PLAYERS.get(i), players.get(PlayerId.CURRENT_PLAYERS.get(i)).name() == String.format("Player_%n", i)?names[i]:players.get(PlayerId.CURRENT_PLAYERS.get(i)).name());
                    System.out.println("Player " + i + " connected !");
                }
            }
                
            new Thread(() -> Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), new Random())).start();
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