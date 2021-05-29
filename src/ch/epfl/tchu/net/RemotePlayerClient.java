package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <h1>RemotePlayerClient</h1>
 * Implements a remote player client that connects to the server and listens for messages sent through the socket (slave behaviour) by the distant server (master behaviour) hosted by the distant player.
 *
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public final class RemotePlayerClient {
    public String name;
    
    private final Player player;
    private final String proxyName;
    private final int proxyPort;
    private int nbPlayers = 2;

    /**
     * Unique constructor of a RemotePlayerClient, build it with an instance of Player for which it has tout get a proxy,
     * the name of the host and the port.
     *
     * @param player    The player that needs a proxy
     * @param proxyName the host name
     * @param proxyPort the port name
     */
    public RemotePlayerClient(Player player, String proxyName, int proxyPort, String name) {
        this.player = player;
        this.name = name;
        this.proxyName = proxyName;
        this.proxyPort = proxyPort;
    }

    /**
     * Method that is normally always running when the server isn't close. It tries to listen the socket for a message
     * sent by the RemotePlayerProxy.
     * <p>
     * Then, according to the id of the Message that is decrypted with the first String
     * of the table and using the {@link MessageId}, it deserializes the arguments and call the same method with the Player that is given to
     * the constructor so the graphic interface will be changed and the (human) player will be able to see and possibly
     * to give a response.
     * <p>
     * if this method returns a result, serializes the player's response to send it back to the proxy in response.
     */
    public void run() {

        try (Socket socket = new Socket(proxyName, proxyPort)) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII));

            String messagesFromReader;
            while ((messagesFromReader = reader.readLine()) != null) {
                String[] message = messagesFromReader.split(Pattern.quote(" "), -1);

                switch (MessageId.valueOf(message[0])) {

                    case INIT_CLIENT:
                        String n = (name.equals("Player_") ? Serdes.STRING.deserialize(message[1]) : name);
                        name = n;
                        player.name(name);
                        nbPlayers = Serdes.INT.deserialize(message[2]);
                        PlayerId.NUMBER_OF_PLAYER = nbPlayers;

                        writeMessage(writer, Serdes.STRING.serialize(name));
                        break;
                    case INIT_PLAYERS:
                        List<String> names = Serdes.L_STRING.deserialize(message[2]);
                        Map<PlayerId, String> playerNames = new HashMap<>();
                        int index = 0;
                        for (PlayerId p : PlayerId.CURRENT_PLAYERS()) {
                            playerNames.put(p, names.get(index));
                            ++index;
                        }

                        player.initPlayers(Serdes.PLAYER_ID.deserialize(message[1]), playerNames);
                        break;

                    case RECEIVE_INFO:
                        player.receiveInfo(Serdes.STRING.deserialize(message[1]));
                        break;

                    case UPDATE_STATE:
                        PublicGameState pgs = Serdes.SC_PUBLIC_GAME_STATE.deserialize(message[1]);
                        PlayerState ps = Serdes.SC_PLAYER_STATE.deserialize(message[2]);
                        player.updateState(pgs, ps);
                        break;

                    case SET_INITIAL_TICKETS:
                        player.setInitialTicketChoice(Serdes.SB_TICKET.deserialize(message[1]));
                        break;

                    case CHOOSE_INITIAL_TICKETS:
                        SortedBag<Ticket> tickets = player.chooseInitialTickets();
                        writeMessage(writer, Serdes.SB_TICKET.serialize(tickets));
                        break;

                    case NEXT_TURN:
                        Player.TurnKind turn = player.nextTurn();
                        writeMessage(writer, Serdes.TURN_KIND.serialize(turn));
                        break;

                    case CHOOSE_TICKETS:
                        SortedBag<Ticket> chooseTickets = player.chooseTickets(Serdes.SB_TICKET.deserialize(message[1]));
                        writeMessage(writer, Serdes.SB_TICKET.serialize(chooseTickets));
                        break;

                    case DRAW_SLOT:
                        writeMessage(writer, Serdes.INT.serialize(player.drawSlot()));
                        break;

                    case ROUTE:
                        writeMessage(writer, Serdes.ROUTE.serialize(player.claimedRoute()));
                        break;

                    case CARDS:
                        writeMessage(writer, Serdes.SB_CARD.serialize(player.initialClaimCards()));
                        break;

                    case CHOOSE_ADDITIONAL_CARDS:
                        List<SortedBag<Card>> possibleAdditionalCards = Serdes.L_SB_CARD.deserialize(message[1]);
                        SortedBag<Card> additionalCards = player.chooseAdditionalCards(possibleAdditionalCards);
                        writeMessage(writer, Serdes.SB_CARD.serialize(additionalCards));
                        break;

                    default:
                        throw new Error();
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Send the message in the socket thanks to thw BufferedWriter.
     *
     * @param writer  BufferedWriter build according to a socket
     * @param message message to will be sent
     */
    private void writeMessage(BufferedWriter writer, String message) {
        try {
            writer.write(message);
            writer.write('\n');
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
