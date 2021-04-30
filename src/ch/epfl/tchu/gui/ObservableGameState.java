package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;


/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public final class ObservableGameState {

    private PublicGameState publicGameState;
    private PlayerState playerState;

    private final IntegerProperty ticketsInDeckPercent;
    private final IntegerProperty cardsInDeckPercent;
    private final List<ObjectProperty<Card>> faceUpCards;
    private final Map<Route, ObjectProperty<PlayerId>> routeOwner;

    //PublicPlayerState Properties
    private final Map<PlayerId, IntegerProperty> ticketsCount, cardsCount, carsCount, pointsCount;

    //PlayerState (local player)
    private final ObservableList<Ticket> tickets;
    private final Map<Card, IntegerProperty> cards;
    private final Map<Route, BooleanProperty> claimableRoutes;


    //Constructor
    public ObservableGameState(PlayerId playerId) {
        publicGameState = null;
        playerState = null;

        ticketsInDeckPercent = new SimpleIntegerProperty(0);
        cardsInDeckPercent = new SimpleIntegerProperty(0);

        faceUpCards = new ArrayList<>(Constants.FACE_UP_CARDS_COUNT);
        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++)
            faceUpCards.add(new SimpleObjectProperty<>(null));

        routeOwner = new HashMap<>(ChMap.routes().size());
        for (Route route : ChMap.routes())
            routeOwner.put(route, new SimpleObjectProperty<>(null));

        ticketsCount = new EnumMap<>(PlayerId.class);
        cardsCount = new EnumMap<>(PlayerId.class);
        carsCount = new EnumMap<>(PlayerId.class);
        pointsCount = new EnumMap<>(PlayerId.class);
        for (PlayerId pId : PlayerId.ALL) {
            ticketsCount.put(pId, new SimpleIntegerProperty());
            cardsCount.put(pId, new SimpleIntegerProperty());
            carsCount.put(pId, new SimpleIntegerProperty());
            pointsCount.put(pId, new SimpleIntegerProperty());
        }

        tickets = FXCollections.observableArrayList();

        cards = new EnumMap<>(Card.class);
        for (Card card : Card.ALL)
            cards.put(card, new SimpleIntegerProperty());

        claimableRoutes = new HashMap<>(ChMap.routes().size());
        for (Route route : ChMap.routes())
            claimableRoutes.put(route, new SimpleBooleanProperty(false));
    }

    //Private Methods
    private int ticketsPercent(int ticketCount) {
        int totalTickets = ChMap.tickets().size();
        if (ticketCount < totalTickets && ticketCount >= 0)
            return ((ticketCount * 100) / totalTickets);
        return 0;
    }

    private int cardsPercent(int cardCount) {
        int totalCards = Constants.CAR_CARDS_COUNT * Card.CARS.size() + Constants.LOCOMOTIVE_CARDS_COUNT;
        if (cardCount < totalCards && cardCount >= 0)
            return ((cardCount * 100) / totalCards);
        return 0;
    }

    //Public Methods
    public void setState(PublicGameState gameState, PlayerState playerState) {
        publicGameState = gameState;
        this.playerState = playerState;

        ticketsInDeckPercent.setValue(ticketsPercent(publicGameState.ticketsCount()));
        cardsInDeckPercent.setValue(cardsPercent(publicGameState.cardState().deckSize()));

        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = publicGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).setValue(newCard);
        }

        for (Route claimedRoute : gameState.claimedRoutes()) {
            for (PlayerId pId : PlayerId.ALL) {
                if (gameState.playerState(pId).routes().contains(claimedRoute))
                    routeOwner.get(claimedRoute).setValue(pId);
            }

        }

        for (PlayerId player : PlayerId.ALL) {
            ticketsCount.get(player).setValue(gameState.playerState(player).ticketCount());
            cardsCount.get(player).setValue(gameState.playerState(player).cardCount());
            carsCount.get(player).setValue(gameState.playerState(player).carCount());
            pointsCount.get(player).setValue(gameState.playerState(player).claimPoints());
        }

        tickets.setAll(playerState.tickets().toList());

        for (Card card : Card.ALL)
            cards.get(card).setValue(playerState.cards().countOf(card));

        //TODO moche
        for (Route route : ChMap.routes()) {
            Route nextRoute = getDoubleRoute(route);

            if (!gameState.claimedRoutes().contains(route)) {
                if (nextRoute != null) {
                    if (!gameState.claimedRoutes().contains(nextRoute))
                        claimableRoutes.get(route).setValue(true);
                } else
                    claimableRoutes.get(route).setValue(true);
            } else {
                if (nextRoute != null)
                    claimableRoutes.get(nextRoute).setValue(false);
            }
        }
    }

    private static Route getDoubleRoute(Route route) {
        for (Route r : ChMap.routes()) {
            if (route.station1().id() == r.station1().id() && route.station2().id() == r.station2().id())
                return r;
        }
        return null;
    }

    public ReadOnlyIntegerProperty ticketsInDeckPercent() {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(ticketsInDeckPercent);
    }


    public ReadOnlyIntegerProperty cardsInDeckPercent() {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(cardsInDeckPercent);
    }


    public ReadOnlyObjectProperty<Card> faceUpCard(int index) {
        return faceUpCards.get(index);
    }

    public ReadOnlyObjectProperty<PlayerId> routeOwner(Route route) {
        return routeOwner.get(route);
    }

    public ReadOnlyIntegerProperty ticketsCount(PlayerId playerId) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(ticketsCount.get(playerId));
    }

    public ReadOnlyIntegerProperty currentPlayerIdTicketsCount() {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(ticketsCount.get(publicGameState.currentPlayerId()));
    }

    public ReadOnlyIntegerProperty cardsCount(PlayerId playerId) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(cardsCount.get(playerId));
    }

    public ReadOnlyIntegerProperty carsCount(PlayerId playerId) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(carsCount.get(playerId));
    }

    public ReadOnlyIntegerProperty pointsCount(PlayerId playerId) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(pointsCount.get(playerId));
    }

    public ObservableList<Ticket> ticketsProperties() {
        return FXCollections.unmodifiableObservableList(tickets);
    }

    public ReadOnlyIntegerProperty cardProperty(Card card) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(cards.get(card));
    }

    public ReadOnlyBooleanProperty claimableRouteProperty(Route route) {
        return ReadOnlyBooleanProperty.readOnlyBooleanProperty(claimableRoutes.get(route));
    }

    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        return (playerState != null) ? playerState.possibleClaimCards(route) : List.of();
    }
}
