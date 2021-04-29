package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public final class ObservableGameState {

    //PublicGameState Properties
    private PublicGameState currentPublicGameState;
    private ObjectProperty<Integer> ticketsInDeckPercent;
    private ObjectProperty<Integer> cardsInDeckPercent;
    private final List<ObjectProperty<Card>> faceUpCards;
    private final List<ObjectProperty<PlayerId>> routes;

    //PublicPlayerState Properties
    private final Map<PlayerId, ObjectProperty<Integer>> ticketsCount, cardsCount, carsCount, pointsCount;

    //PlayerState (local player)
    private PlayerState currentPlayerState;
    private final ObjectProperty<SortedBag<Ticket>> tickets;
    private final List<ObjectProperty<Integer>> cards;
    private final List<ObjectProperty<Boolean>> claimableRoutes;


    //Constructor

    public ObservableGameState(PlayerId playerId) {
        currentPublicGameState = null;
        setTicketsPercent(0);
        setCardsPercent(0);
        faceUpCards = createFaceUpCards();
        routes = new ArrayList<>(ChMap.routes().size());

        ticketsCount = new HashMap<>(PlayerId.COUNT);
        cardsCount = new HashMap<>(PlayerId.COUNT);
        carsCount = new HashMap<>(PlayerId.COUNT);
        pointsCount = new HashMap<>(PlayerId.COUNT);

        currentPlayerState = null;
        tickets = null;
        cards = new ArrayList<>(Card.COUNT);
        claimableRoutes = new ArrayList<>(ChMap.routes().size());
    }

    //Private Methods
    private boolean setTicketsPercent(int ticketCount) {
        int totalTickets = ChMap.tickets().size();
        if (ticketCount < totalTickets && ticketCount >= 0) {
            ticketsInDeckPercent.setValue(ticketCount * 100 / totalTickets);
            return true;
        }
        return false;
    }

    private boolean setCardsPercent(int cardCount) {
        int totalCards = Constants.CAR_CARDS_COUNT * Card.CARS.size() + Constants.LOCOMOTIVE_CARDS_COUNT;
        if (cardCount < totalCards && cardCount >= 0) {
            cardsInDeckPercent.setValue((cardCount * 100) / totalCards);
            return true;
        }
        return false;
    }

    //TODO: request card list from server and return it
    private List<ObjectProperty<Card>> createFaceUpCards() {
        List<ObjectProperty<Card>> l = new ArrayList<>(Constants.FACE_UP_CARDS_COUNT);
        return l;
    }

    //Public Methods
    public void setState(PublicGameState gameState, PlayerState playerState) {
        currentPublicGameState = gameState;
        currentPlayerState = playerState;

        setTicketsPercent(currentPublicGameState.ticketsCount());
        setCardsPercent(currentPublicGameState.cardState().deckSize());

        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = currentPublicGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).setValue(newCard);
        }

        for (Route route : gameState.claimedRoutes()) {
            if (playerState.routes().contains(route))
                routes.get(routes.indexOf(route)).setValue(gameState.currentPlayerId());
        }

        for (PlayerId player : PlayerId.ALL) {
            ticketsCount.get(player).setValue(gameState.playerState(player).ticketCount());
            cardsCount.get(player).setValue(gameState.playerState(player).ticketCount());
            carsCount.get(player).setValue(gameState.playerState(player).ticketCount());
            pointsCount.get(player).setValue(gameState.playerState(player).ticketCount());
        }

        tickets.setValue(playerState.tickets());

        for (Card card : Card.ALL) {
            int count = 0;

            for (Card playerCard : playerState.cards()) {
                if (playerCard == card)
                    ++count;

            }
            cards.get(cards.indexOf(card)).setValue(count);
        }

        for (Route route: ChMap.routes()) {

            if(!gameState.claimedRoutes().contains(route) && !gameState.claimedRoutes().contains(checkDoubleRoute(route))){//TODO private method to check neighbors
                
            }
        }

    }

    private static Route checkDoubleRoute(Route route){

        for (Route r : ChMap.routes()) {
            if(route.station1().id() == r.station1().id() && route.station2().id() == r.station2().id()){
                return r;
            }
        }
        return null;
    }


}
