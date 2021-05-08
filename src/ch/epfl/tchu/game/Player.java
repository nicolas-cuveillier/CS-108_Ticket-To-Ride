package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

/**
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 * <p>
 * implements the notion of player of the tchu game
 */
public interface Player {

    /**
     * @author Grégory Preisig & Nicolas Cuveillier
     * <p>
     * Turn action kinds
     * {@link #DRAW_TICKETS},
     * {@link #DRAW_CARDS},
     * {@link #CLAIM_ROUTE}.
     */
    enum TurnKind {
        DRAW_TICKETS,
        DRAW_CARDS,
        CLAIM_ROUTE;

        /**
         * an unmodifiable list of all the turn kinds
         */
        public static final List<TurnKind> ALL = List.of(values());

    }

    /**
     * called at the beginning of the game to communicate to the player his own id and names of the other players
     *
     * @param ownId       Id of the player (self)
     * @param playerNames Ids of all the payers, mapped to their name (includes self)
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * Called every time an information must be display to the player
     *
     * @param info Information to give to the player, parsed by the class Info
     */
    void receiveInfo(String info);

    /**
     * Called each time the game state changes
     *
     * @param newState new state of the game
     * @param ownState current state of the player
     */
    void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * Called during the initialization of the game to assign the player it's 5 tickets
     *
     * @param tickets 5 initial tickets assigned to the player
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * Called at the beginning of the game to get the initial tickets that the player kept
     *
     * @return initial tickets assigned to the player
     */
    SortedBag<Ticket> chooseInitialTickets();

    /**
     * Called at the beginning of the player's turn
     *
     * @return the action taken by the player during his turn
     */
    TurnKind nextTurn();

    /**
     * Called when the player chooses to draw tickets during his turn
     *
     * @param options the tickets drawn by the player
     * @return the tickets kept by the player
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * Called when the player chooses to draw cards during his turn
     *
     * @return the slot's id from which the player drew a card (0-4 for visible cards or {@link Constants (DECK_SLOT)} for the deck)
     */
    int drawSlot();

    /**
     * Called when the player chooses to try to claim a route during his turn
     *
     * @return the route the player tries to claim
     */
    Route claimedRoute();

    /**
     * Called when the player chooses to try to claim a route during his turn
     *
     * @return the cards chosen by the player to try to claim the route
     */
    SortedBag<Card> initialClaimCards();

    /**
     * Called when the player chooses to try to claim a tunnel during his turn
     *
     * @param options the additional cards to claim the tunnel
     * @return the additional cards chosen by the player, empty if the player refuses to spend the additional cards or cannot fulfill the requirement
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);


}
