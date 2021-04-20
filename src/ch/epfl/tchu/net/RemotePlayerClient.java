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
 */
public final class RemotePlayerClient {
    private final Player player;
    private final String proxyName;
    private final int proxyPort;
    private final BufferedWriter writer;
    private final BufferedReader reader;

    public RemotePlayerClient(Player player, String proxyName, int proxyPort) {
        this.player = player;
        this.proxyName = proxyName;
        this.proxyPort = proxyPort;

        try  {
            Socket socket = new Socket(proxyName, proxyPort);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void run() {

        try {
            String readerMessage;
            while ((readerMessage = reader.readLine()) != null) {
                String[] message = readerMessage.split(Pattern.quote(" "), -1);

                switch (MessageId.valueOf(message[0])) {
                    case INIT_PLAYERS:
                        List<String> names = Serdes.L_STRING.deserialize(message[2]);
                        Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1,names.get(0),PlayerId.PLAYER_2,names.get(1));

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
