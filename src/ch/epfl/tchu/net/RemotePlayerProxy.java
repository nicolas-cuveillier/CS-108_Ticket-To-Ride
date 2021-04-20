package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public final class RemotePlayerProxy implements Player {
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    public RemotePlayerProxy(Socket socket) {
        try {
            this.socket = socket;
            this.writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.US_ASCII));
            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), StandardCharsets.US_ASCII));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void writeMessage(MessageId id, String serialization) {
        try {
            writer.write(id.name() + " " + serialization);
            writer.write('\n');
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String readMessage() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(Serdes.PLAYER_ID.serialize(ownId))
                .add(Serdes.L_STRING.serialize(new ArrayList<>(playerNames.values())));

        writeMessage(MessageId.INIT_PLAYERS, joiner.toString());
    }

    @Override
    public void receiveInfo(String info) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(Serdes.STRING.serialize(info));

        writeMessage(MessageId.RECEIVE_INFO, joiner.toString());
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(Serdes.SC_PUBLIC_GAME_STATE.serialize(newState))
                .add(Serdes.SC_PLAYER_STATE.serialize(ownState));

        writeMessage(MessageId.UPDATE_STATE, joiner.toString());
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(Serdes.SB_TICKET.serialize(tickets));

        writeMessage(MessageId.SET_INITIAL_TICKETS, joiner.toString());
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        writeMessage(MessageId.CHOOSE_INITIAL_TICKETS, "");

        return Serdes.SB_TICKET.deserialize(readMessage());
    }

    @Override
    public TurnKind nextTurn() {
        writeMessage(MessageId.NEXT_TURN, "");

        return Serdes.TURN_KIND.deserialize(readMessage());
    }

    //TODO : replace joiner
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(Serdes.SB_TICKET.serialize(options));

        writeMessage(MessageId.CHOOSE_TICKETS, joiner.toString());

        return Serdes.SB_TICKET.deserialize(readMessage());
    }

    @Override
    public int drawSlot() {
        writeMessage(MessageId.DRAW_SLOT, "");

        return Serdes.INT.deserialize(readMessage());
    }

    @Override
    public Route claimedRoute() {
        writeMessage(MessageId.ROUTE, "");

        return Serdes.ROUTE.deserialize(readMessage());
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        writeMessage(MessageId.CARDS, "");

        return Serdes.SB_CARD.deserialize(readMessage());
    }

    //TODO : replace joiner
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(Serdes.L_SB_CARD.serialize(options));

        writeMessage(MessageId.CHOOSE_ADDITIONAL_CARDS, joiner.toString());
        
        return Serdes.SB_CARD.deserialize(readMessage());
    }
}
