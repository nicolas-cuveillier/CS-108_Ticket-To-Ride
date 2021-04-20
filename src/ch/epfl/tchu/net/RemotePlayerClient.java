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
 *
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
     * of the table, it deserializes the arguments and call the same method with the Player that is given to
     * the constructor so the graphic interface will be changed and the (human) player will be able to see and possibly
     * to give a response.
     * <p>
     * if this method returns a result, serializes the player's response to send it back to the proxy in response.
     */
    public void run() {

        try (Socket socket = new Socket(proxyName, proxyPort)) {

            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII));

            String readerMessage;
            while ((readerMessage = reader.readLine()) != null) {
                String[] message = readerMessage.split(Pattern.quote(" "), -1);

                switch (MessageId.valueOf(message[0])) {
                    case INIT_PLAYERS:
                        List<String> names = Serdes.L_STRING.deserialize(message[2]);
                        Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, names.get(0), PlayerId.PLAYER_2, names.get(1));

                        player.initPlayers(Serdes.PLAYER_ID.deserialize(message[1]), playerNames);
                        break;

                    case RECEIVE_INFO:
                        player.receiveInfo(message[1]);
                        break;

                    case UPDATE_STATE:
                        player.updateState(Serdes.SC_PUBLIC_GAME_STATE.deserialize(message[1]), Serdes.SC_PLAYER_STATE.deserialize(message[2]));
                        break;

                    case SET_INITIAL_TICKETS:
                        player.setInitialTicketChoice(Serdes.SB_TICKET.deserialize(message[1]));
                        break;

                    case CHOOSE_INITIAL_TICKETS:
                        SortedBag<Ticket> tickets = player.chooseInitialTickets();
                        writer.write(Serdes.SB_TICKET.serialize(tickets));
                        writer.write('\n');
                        writer.flush();
                        break;

                    case NEXT_TURN:
                        Player.TurnKind turn = player.nextTurn();
                        writer.write(Serdes.TURN_KIND.serialize(turn));
                        writer.write('\n');
                        writer.flush();
                        break;

                    case CHOOSE_TICKETS:
                        SortedBag<Ticket> chooseTickets = player.chooseTickets(Serdes.SB_TICKET.deserialize(message[1]));
                        writer.write(Serdes.SB_TICKET.serialize(chooseTickets));
                        writer.write('\n');
                        writer.flush();
                        break;

                    case DRAW_SLOT:
                        writer.write(Serdes.INT.serialize(player.drawSlot()));
                        writer.write('\n');
                        writer.flush();
                        break;

                    case ROUTE:
                        writer.write(Serdes.ROUTE.serialize(player.claimedRoute()));
                        writer.write('\n');
                        writer.flush();
                        break;

                    case CARDS:
                        writer.write(Serdes.SB_CARD.serialize(player.initialClaimCards()));
                        writer.write('\n');
                        writer.flush();
                        break;

                    case CHOOSE_ADDITIONAL_CARDS:
                        List<SortedBag<Card>> possibleAdditionalCards = Serdes.L_SB_CARD.deserialize(message[1]);
                        SortedBag<Card> additionalCards = player.chooseAdditionalCards(possibleAdditionalCards);

                        writer.write(Serdes.SB_CARD.serialize(additionalCards));
                        writer.write('\n');
                        writer.flush();
                        break;

                    default:
                        break;
                }

            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }
}
