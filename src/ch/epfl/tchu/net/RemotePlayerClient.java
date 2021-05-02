package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Ticket;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 * <p>
 * represent a remote player client that make the connection with listening its port, between the server,
 * hosted by one player, to the other player
 */
public final class RemotePlayerClient {
    private final Player player;
    private final String proxyName;
    private final int proxyPort;

    /**
     * Unique constructor of a RemotePlayerClient, build it with an instance of Player for which it has tout get a proxy,
     * the name of the host and the port
     *
     * @param player    The player that need a proxy
     * @param proxyName the host name
     * @param proxyPort the port name
     */
    public RemotePlayerClient(Player player, String proxyName, int proxyPort) {
        this.player = player;
        this.proxyName = proxyName;
        this.proxyPort = proxyPort;
    }

    /**
     * method that is normally always running when the server isn't close. It tries to listen the socket for a message
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

            String messageFromReader;
            while ((messageFromReader = reader.readLine()) != null) {
                String[] messages = messageFromReader.split(Pattern.quote(" "), -1);

                switch (MessageId.valueOf(messages[0])) {
                    case INIT_PLAYERS:
                        List<String> names = Serdes.L_STRING.deserialize(messages[2]);
                        Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, names.get(0), PlayerId.PLAYER_2, names.get(1));

                        player.initPlayers(Serdes.PLAYER_ID.deserialize(messages[1]), playerNames);
                        break;

                    case RECEIVE_INFO:
                        player.receiveInfo(Serdes.STRING.deserialize(messages[1]));
                        break;

                    case UPDATE_STATE:
                        player.updateState(Serdes.SC_PUBLIC_GAME_STATE.deserialize(messages[1]), Serdes.SC_PLAYER_STATE.deserialize(messages[2]));
                        break;

                    case SET_INITIAL_TICKETS:
                        player.setInitialTicketChoice(Serdes.SB_TICKET.deserialize(messages[1]));
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
                        SortedBag<Ticket> chooseTickets = player.chooseTickets(Serdes.SB_TICKET.deserialize(messages[1]));
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
                        List<SortedBag<Card>> possibleAdditionalCards = Serdes.L_SB_CARD.deserialize(messages[1]);
                        SortedBag<Card> additionalCards = player.chooseAdditionalCards(possibleAdditionalCards);
                        writeMessage(writer, Serdes.SB_CARD.serialize(additionalCards));
                        break;

                    default:
                        break;
                }

            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    private static void writeMessage(BufferedWriter writer, String message) {
        try {
            writer.write(message);
            writer.write('\n');
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
