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
 * represent the remote player on the locale machine, local side of the bridge between the two players.
 * 
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public final class RemotePlayerProxy implements Player {
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    /**
     * Construct a RemotePlayerProxy according to a socket.
     *
     * @param socket socket that the proxy is using for listening and sending textual message through the network
     */
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

    /**
     * Write in the BufferedWriter built with the socket the serialize information that this method has been called
     * and so the graphic interface must be updated displaying the name of the players.
     *
     * @param ownId       Id of the player (self)
     * @param playerNames Ids of all the payers, mapped to their name (includes self)
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(Serdes.PLAYER_ID.serialize(ownId))
                .add(Serdes.L_STRING.serialize(new ArrayList<>(playerNames.values())));

        writeMessage(MessageId.INIT_PLAYERS, joiner.toString());
    }

    /**
     * Write in the BufferedWriter built with the socket the serialize information that this method has been called
     * and so the graphic interface must be updated displaying info.
     *
     * @param info Information to give to the player, parsed by the class {@link ch.epfl.tchu.gui.Info}
     */
    @Override
    public void receiveInfo(String info) {
        writeMessage(MessageId.RECEIVE_INFO, Serdes.STRING.serialize(info));
    }

    /**
     * Write in the BufferedWriter built with the socket the serialize information that this method
     * has been called and so the graphic interface must be updated given the new PublicGameState and PlayerState.
     *
     * @param newState new state of the game
     * @param ownState current state of the player
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(Serdes.SC_PUBLIC_GAME_STATE.serialize(newState))
                .add(Serdes.SC_PLAYER_STATE.serialize(ownState));

        writeMessage(MessageId.UPDATE_STATE, joiner.toString());
    }

    /**
     * Write in the BufferedWriter built with the socket the serialize information that this method
     * has been called and so the graphic interface must be updated displaying the tickets for the initial ticket.
     * choice for the player
     *
     * @param tickets 5 initial tickets assigned to the player
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        writeMessage(MessageId.SET_INITIAL_TICKETS, Serdes.SB_TICKET.serialize(tickets));
    }

    /**
     * Firstly, write in the BufferedWriter built with the socket the serialize information that this method
     * has been called and so the graphic interface must be updated.<p> Then, wait for the player to choose its initial
     * tickets, looking at the BufferedReader built with the socket and deserialize the message to pass the information.
     *
     * @return SortedBag<Ticket> - the tickets that the player has chosen
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        writeMessage(MessageId.CHOOSE_INITIAL_TICKETS, "");
        return Serdes.SB_TICKET.deserialize(readMessage());
    }

    /**
     * Firstly, write in the BufferedWriter built with the socket the serialize information that this method
     * has been called and so the graphic interface must be updated.<p> Then, wait for the player to choose the kind of
     * its turn, looking at the BufferedReader built with the socket and deserialize the message to pass the information.
     *
     * @return TurnKind - the kind of turn that the player decided to play
     */
    @Override
    public TurnKind nextTurn() {
        writeMessage(MessageId.NEXT_TURN, "");
        return Serdes.TURN_KIND.deserialize(readMessage());
    }

    /**
     * Firstly, write in the BufferedWriter built with the socket the serialize information that this method
     * has been called with a SortedBag of tickets and so the graphic interface must be updated.<p> Then,
     * wait for the player to choose the tickets that he will keep, looking at the BufferedReader built with the
     * socket and deserialize the message to pass the information.
     *
     * @param options the tickets drawn by the player
     * @return SortedBag<Ticket> - the tickets that the player choose
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        writeMessage(MessageId.CHOOSE_TICKETS, Serdes.SB_TICKET.serialize(options));
        return Serdes.SB_TICKET.deserialize(readMessage());
    }

    /**
     * Firstly, write in the BufferedWriter built with the socket the serialize information that this method
     * has been called and so the graphic interface must be updated.<p> Then, wait for the player to choose the
     * slot of the card that he will keep, looking at the BufferedReader built with the socket and deserialize the
     * message to pass the information.
     *
     * @return int - the slot of the card that the player has chosen
     */
    @Override
    public int drawSlot() {
        writeMessage(MessageId.DRAW_SLOT, "");
        return Serdes.INT.deserialize(readMessage());
    }

    /**
     * Firstly, write in the BufferedWriter built with the socket the serialize information that this method
     * has been called and so the graphic interface must be updated.<p> Then, wait for the player to choose the
     * route that he is claiming, looking at the BufferedReader built with the socket and deserialize the message to
     * pass the information.
     *
     * @return Route - the route that the player is trying to get
     */
    @Override
    public Route claimedRoute() {
        writeMessage(MessageId.ROUTE, "");
        return Serdes.ROUTE.deserialize(readMessage());
    }

    /**
     * Firstly, write in the BufferedWriter built with the socket the serialize information that this method
     * has been called and so the graphic interface must be updated.<p> Then, wait for the player to choose the
     * cards that he wants to play, looking at the BufferedReader built with the socket and deserialize the message to
     * pass the information.
     *
     * @return SortedBag<Card> - the cards that the player is playing to claim a Route
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        writeMessage(MessageId.CARDS, "");
        return Serdes.SB_CARD.deserialize(readMessage());
    }

    /**
     * Firstly, write in the BufferedWriter built with the socket the serialize information that this method
     * has been called with a list of SortedBag of cards (player's option) and so the graphic interface must be updated.
     * <p> Then, wait for the player to choose the cards (or no cards) that he wants to play for the Route, looking at
     * the BufferedReader built with the socket and deserialize the message to pass the information.
     *
     * @param options the additional cards to claim the tunnel
     * @return SortedBag<Card> - the cards that the player is (or not) adding to the initial cards according
     * to the option(s) he have
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        writeMessage(MessageId.CHOOSE_ADDITIONAL_CARDS, Serdes.L_SB_CARD.serialize(options));
        return Serdes.SB_CARD.deserialize(readMessage());
    }
}
