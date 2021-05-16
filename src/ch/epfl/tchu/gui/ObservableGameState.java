package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;


/**
 * represent the observable PublicGameState, having properties for every sensible attributs of the game's state
 * 
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public final class ObservableGameState {

    private PublicGameState publicGameState;
    private PlayerState playerState;
    private final PlayerId playerId;

    //global games' properties
    private final IntegerProperty ticketsInDeckPercent;
    private final IntegerProperty cardsInDeckPercent;
    private final List<ObjectProperty<Card>> faceUpCards;
    private final Map<Route, ObjectProperty<PlayerId>> routeOwner;

    //PublicPlayerState Properties
    private final Map<PlayerId, IntegerProperty> ticketsCount, cardsCount, carsCount, pointsCount;

    //PlayerState (local player) properties
    private final ObservableList<Ticket> tickets;
    private final Map<Card, IntegerProperty> cards;
    private final Map<Route, BooleanProperty> claimableRoutes;


    /**
     * Unique constructor for an ObservableGameState, specific to a player. Initialise all properties with default
     * value such as false, null, 0
     *
     * @param playerId the identity of its corresponding player in {@link PlayerId}
     */
    public ObservableGameState(PlayerId playerId) {
        this.publicGameState = null;
        this.playerState = null;
        this.playerId = playerId;

        this.ticketsInDeckPercent = new SimpleIntegerProperty(0);
        this.cardsInDeckPercent = new SimpleIntegerProperty(0);

        this.faceUpCards = new ArrayList<>(Constants.FACE_UP_CARDS_COUNT);
        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++)
            faceUpCards.add(new SimpleObjectProperty<>(null));

        this.routeOwner = new HashMap<>(ChMap.routes().size());
        for (Route route : ChMap.routes())
            routeOwner.put(route, new SimpleObjectProperty<>(null));

        this.ticketsCount = new EnumMap<>(PlayerId.class);
        this.cardsCount = new EnumMap<>(PlayerId.class);
        this.carsCount = new EnumMap<>(PlayerId.class);
        this.pointsCount = new EnumMap<>(PlayerId.class);
        for (PlayerId pId : PlayerId.ALL) {
            ticketsCount.put(pId, new SimpleIntegerProperty());
            cardsCount.put(pId, new SimpleIntegerProperty());
            carsCount.put(pId, new SimpleIntegerProperty());
            pointsCount.put(pId, new SimpleIntegerProperty());
        }

        this.tickets = FXCollections.observableArrayList();

        this.cards = new EnumMap<>(Card.class);
        for (Card card : Card.ALL)
            cards.put(card, new SimpleIntegerProperty());

        this.claimableRoutes = new HashMap<>(ChMap.routes().size());
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

    /**
     * Method that will update all properties that have changed during a player turn according to the new game state
     * and to the actual playerState.
     *
     * @param newGameState the new PublicGameState that has been updated during the game
     * @param playerState  actual PlayerState specific to the corresponding PlayerId
     */
    public void setState(PublicGameState newGameState, PlayerState playerState) {
        this.publicGameState = newGameState;
        this.playerState = playerState;

        ticketsInDeckPercent.setValue(ticketsPercent(publicGameState.ticketsCount()));
        cardsInDeckPercent.setValue(cardsPercent(publicGameState.cardState().deckSize()));

        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = publicGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).setValue(newCard);
        }

        for (Route claimedRoute : newGameState.claimedRoutes()) {
            for (PlayerId pId : PlayerId.ALL) {
                if (newGameState.playerState(pId).routes().contains(claimedRoute))
                    routeOwner.get(claimedRoute).setValue(pId);
            }
        }

        for (PlayerId player : PlayerId.ALL) {
            ticketsCount.get(player).setValue(newGameState.playerState(player).ticketCount());
            cardsCount.get(player).setValue(newGameState.playerState(player).cardCount());
            carsCount.get(player).setValue(newGameState.playerState(player).carCount());
            pointsCount.get(player).setValue(newGameState.playerState(player).claimPoints());
        }

        tickets.setAll(playerState.tickets().toList());

        for (Card card : Card.ALL)
            cards.get(card).setValue(playerState.cards().countOf(card));

        for (Route route : ChMap.routes()) {
            Route nextRoute = getDoubleRoute(route);

            if (!possibleClaimCards(route).isEmpty() && !newGameState.claimedRoutes().contains(route)) {
                if(nextRoute != null && newGameState.claimedRoutes().contains(nextRoute)){
                    claimableRoutes.get(route).setValue(false);
                } else {
                    claimableRoutes.get(route).setValue(true);
                }

            } else {
                claimableRoutes.get(route).setValue(false);
            }
        }
    }

    private static Route getDoubleRoute(Route route) {
        for (Route r : ChMap.routes()) {
            if (route.station1().id() == r.station1().id() && route.station2().id() == r.station2().id() && !r.id().equals(route.id()))
                return r;
        }
        return null;
    }

    /**
     * Getter for the percentage of Tickets in deck properties.
     *
     * @return a ReadOnlyIntegerProperty of the property containing the percentage of tickets
     */
    public ReadOnlyIntegerProperty ticketsInDeckPercentProperty() {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(ticketsInDeckPercent);
    }

    /**
     * Getter for the percentage of cards in deck properties.
     *
     * @return a ReadOnlyIntegerProperty of the property containing the percentage of cards
     */
    public ReadOnlyIntegerProperty cardsInDeckPercentProperty() {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(cardsInDeckPercent);
    }

    /**
     * Getter for the index card's property.
     *
     * @param index the index of the wanted faceUpCard
     * @return a ReadOnlyObjectProperty< Card > of the property
     */
    public ReadOnlyObjectProperty<Card> faceUpCardProperty(int index) {
        return faceUpCards.get(index);
    }

    /**
     * Getter for the PlayerId's property that is the owner of the route.
     *
     * @param route Route for which it will gives the corresponding PlayerId's property
     * @return a ReadOnlyObjectProperty< PlayerId > of the corresponding route
     */
    public ReadOnlyObjectProperty<PlayerId> routeOwnerProperty(Route route) {
        return routeOwner.get(route);
    }

    /**
     * Getter for the ticketsCount's property of the corresponding PlayerId.
     *
     * @param playerId the PlayerId for the one it will compute his tickets' count
     * @return in a ReadOnlyIntegerProperty, the number of tickets
     */
    public ReadOnlyIntegerProperty ticketsCountProperty(PlayerId playerId) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(ticketsCount.get(playerId));
    }

    /**
     * Getter for the cardsCount's property of the corresponding PlayerId.
     *
     * @param playerId the PlayerId for the one it will compute his cards' count
     * @return in a ReadOnlyIntegerProperty, the number of cards
     */
    public ReadOnlyIntegerProperty cardsCountProperty(PlayerId playerId) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(cardsCount.get(playerId));
    }

    /**
     * Getter for the carsCount's property of the corresponding PlayerId.
     *
     * @param playerId the PlayerId for the one it will compute his cars' count
     * @return in a ReadOnlyIntegerProperty, the number of cars
     */
    public ReadOnlyIntegerProperty carsCountProperty(PlayerId playerId) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(carsCount.get(playerId));
    }

    /**
     * Getter for the pointsCount's property of the corresponding PlayerId.
     *
     * @param playerId the PlayerId for the one it will compute his points' count
     * @return in a ReadOnlyIntegerProperty, the number of points
     */
    public ReadOnlyIntegerProperty pointsCountProperty(PlayerId playerId) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(pointsCount.get(playerId));
    }

    /**
     * Getter for the properties of the actual player.
     *
     * @return an unmodifiable ObservableList< Ticket > containing the properties of the players' tickets
     */
    public ObservableList<Ticket> ticketsProperties() {
        return FXCollections.unmodifiableObservableList(tickets);
    }

    /**
     * Getter for the number of card that the actual player has.
     *
     * @param card the card in {@link Card} for which its count in the actual player cards will be computed
     * @return a ReadOnlyIntegerProperty of the number of cards
     */
    public ReadOnlyIntegerProperty cardProperty(Card card) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(cards.get(card));
    }

    /**
     * Getter for the boolean property containing a boolean saying if the route can be claimed.
     *
     * @param route the route for which its boolean property will be return
     * @return a ReadOnlyBooleanProperty saying if the route can be claim
     */
    public ReadOnlyBooleanProperty claimableRouteProperty(Route route) {
        return ReadOnlyBooleanProperty.readOnlyBooleanProperty(claimableRoutes.get(route));
    }

    /**
     * Getter for the list of possible claim cards for a given route according to the actual player cards.
     *
     * @param route the route that the player wants to claim
     * @return a List< SortedBag < Card > > containing all possibilities that the player has for claiming the route
     * @see PlayerState
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        return (playerState != null) ? playerState.possibleClaimCards(route) : List.of();
    }

    /**
     * Getter for the publicGameState.canDrawTickets method in {@link PublicGameState}.
     *
     * @return (boolean) true iff the PublicGameState tickets' count is different from 0
     * @see PublicGameState
     */
    public boolean canDrawTickets() {
        return publicGameState.canDrawTickets();
    }

    /**
     * Getter for the publicGameState.canDrawCards method in {@link PublicGameState}.
     *
     * @return (boolean) true iff the PublicGameState cards overall are more or equal to five
     * @see PublicGameState
     */
    public boolean canDrawCards() {
        return publicGameState.canDrawCards();
    }
}
