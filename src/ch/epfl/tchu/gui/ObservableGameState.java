package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public final class ObservableGameState {
    
    //PublicGameState Properties
    private PublicGameState currentPublicGameState;
    private int ticketsInDeckPercent;
    private int cardsInDeckPercent;
    private final List<ObjectProperty<Card>> faceUpCards;
    private Map<Route,PlayerId> routeOwner;
    
    //PublicPlayerState Properties
    // Local Player
    private PlayerProperties localPlayer = new PlayerProperties();
    
    // Foreign Player
    private PlayerProperties foreignPlayer = new PlayerProperties();
    
    //PlayerState (local player)
    private PlayerState currentPlayerState;
    private List<Ticket> tickets;
    private Map<Card,Integer> cards;
    private Map<Route,Boolean> claimableRoutes;
    

    //Constructor
    
    public ObservableGameState(PlayerId playerId){
        currentPublicGameState = null;
        setTicketsPercent(0);
        setCardsPercent(0);
        faceUpCards = createFaceUpCards();
        for(Route r : ChMap.routes()) {
            routeOwner.put(r, null);
        }
        
        localPlayer.tickets = 0;
        localPlayer.cards = 0;
        localPlayer.cars = 0;
        localPlayer.points = 0;
        
        foreignPlayer.tickets = 0;
        foreignPlayer.cards = 0;
        foreignPlayer.cars = 0;
        foreignPlayer.points = 0;
        
        currentPlayerState = null;
        tickets = null;
        for(Card c : Card.ALL) {
            cards.put(c, 0);
        }for(Route r : ChMap.routes()) {
            claimableRoutes.put(r, false);
        }
    }
    
    //Private Methods
    
    private boolean setTicketsPercent(int ticketCount) {
        int totalTickets = ChMap.tickets().size();
        if(ticketCount<totalTickets && ticketCount>=0) {
            ticketsInDeckPercent = ticketCount*100/totalTickets;
            return true;
        }
        return false;
    }
    
    private boolean setCardsPercent(int cardCount) {
        int totalCards = Constants.CAR_CARDS_COUNT * Card.CARS.size() + Constants.LOCOMOTIVE_CARDS_COUNT;
        if(cardCount<totalCards && cardCount>=0) {
            cardsInDeckPercent = cardCount*100/totalCards;
            return true;
        }
        return false;
    }
    
    //TODO: request card list from server and return it
    private List<ObjectProperty<Card>> createFaceUpCards(){
        List<SimpleObjectProperty<Card>> l = new ArrayList<>();
        l.add(new SimpleObjectProperty<Card>());
        l.add(new SimpleObjectProperty<Card>());
        l.add(new SimpleObjectProperty<Card>());
        l.add(new SimpleObjectProperty<Card>());
        l.add(new SimpleObjectProperty<Card>());
        return null;
    }
    
    //Public Methods
    
    public void setState(PublicGameState gameState, PlayerState playerState){
        currentPublicGameState = gameState;
        currentPlayerState = playerState;
        
        setTicketsPercent(currentPublicGameState.ticketsCount());
        setCardsPercent(currentPublicGameState.cardState().deckSize());
        
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = currentPublicGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }
        
    }

    //not sure it is good but needed for CardsViewCreator
    public PublicGameState getPublicGameState() {
        return currentPublicGameState;
    }
    
    
    private class PlayerProperties{
        public int tickets = 0;
        public int cards = 0;
        public int cars = 0;
        public int points = 0;
    }
}
