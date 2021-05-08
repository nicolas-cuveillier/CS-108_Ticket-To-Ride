package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.Info;
import ch.epfl.test.TestRandomizer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public class ServerTest {

    void checkServer() {
        System.out.println("Starting server!");
        try (ServerSocket serverSocket = new ServerSocket(5108);
             Socket socket = serverSocket.accept()) {
            Player playerProxy = new RemotePlayerProxy(socket);
            var playerNames = Map.of(PLAYER_1, "Ada",
                    PLAYER_2, "Charles");

            var playerStates = Map.of(PLAYER_1, new PublicPlayerState(5,5, List.of()),
                    PLAYER_2, new PublicPlayerState(5,5, List.of()));

            playerProxy.initPlayers(PLAYER_1, playerNames);
            playerProxy.receiveInfo(new Info("Nico").canPlay());
            playerProxy.updateState(new PublicGameState(30,
                    CardState.of(Deck.of(SortedBag.of(5,Card.BLUE), TestRandomizer.newRandom()))
                    ,PLAYER_2,playerStates,null),new PlayerState(SortedBag.of(),SortedBag.of(), List.of()));
            playerProxy.chooseInitialTickets();

            playerProxy.chooseAdditionalCards(List.of(SortedBag.of(5,Card.BLUE),SortedBag.of(2,Card.LOCOMOTIVE)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server done!");
    }
}
