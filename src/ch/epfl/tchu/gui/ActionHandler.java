package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 * <p>
 * Implement the notion of handler for several actions in Tchu.
 */
public interface ActionHandler {

    /**
     * Implements the notion of handler for the drawing ticket action.
     */
    @FunctionalInterface
    interface DrawTicketsHandler {
        /**
         * Method that handle drawing a ticket.
         */
        void onDrawTickets();
    }

    /**
     * Implements the notion of handler for the drawing card action.
     */
    @FunctionalInterface
    interface DrawCardHandler {
        /**
         * Method that handle the player's action of drawing a card at a certain index in [-1]U[0,4].
         *
         * @param index the index of the card that has been draw
         */
        void onDrawCard(int index);
    }

    /**
     * Implements the notion of handler for the claiming route action.
     */
    @FunctionalInterface
    interface ClaimRouteHandler {
        /**
         * Method that will handle the player's action of claiming a route with set of cards.
         *
         * @param route the route that a player wants to claim
         * @param cards the cards that the player wants to use
         */
        void onClaimRoute(Route route, SortedBag<Card> cards);
    }

    /**
     * Implements the notion of handler for the choosing tickets action.
     */
    @FunctionalInterface
    interface ChooseTicketsHandler {
        /**
         * Method that will handle the player action that is keeping tickets.
         *
         * @param tickets tickets that will be kept by the player
         */
        void onChooseTickets(SortedBag<Ticket> tickets);
    }

    /**
     * Implements the notion of handler for the choosing cards action.
     */
    @FunctionalInterface
    interface ChooseCardsHandler {
        /**
         * Method that will handle the action of choosing cards, either as initial cards at the beginning of the game
         * or as additional cards used for taking a route. if they are additional cards, then the multiset can be empty,
         * which means that the player gives up taking possession of the tunnel.
         *
         * @param cards that will be used for claiming a route or kept as initial cards
         */
        void onChooseCards(SortedBag<Card> cards);
    }

}
