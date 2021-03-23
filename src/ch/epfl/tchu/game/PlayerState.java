package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 * <p>
 * represent the private part of the player's state, enhanced class from PublicPlayerState
 */
public final class PlayerState extends PublicPlayerState {

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;
    private final List<Route> routes;

    /**
     * Principal constructor of a PlayerState, build a PlayerState with its Tickets, Cards and Routes
     *
     * @param tickets (SortedBag<Ticket>)
     * @param cards   (SortedBag<Ticket>)
     * @param routes  (List<Route>)
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = SortedBag.of(tickets);
        this.cards = SortedBag.of(cards);
        this.routes = List.copyOf(routes);
    }

    /**
     * static method for building the initial state of a PlayerState
     *
     * @param initialCards the initial cards of the player
     * @return (PlayerState)
     * @throws IllegalArgumentException if there are not four initial cards
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);
        SortedBag.Builder<Ticket> tickets = new SortedBag.Builder<>();
        List<Route> routes = new ArrayList<>();
        return new PlayerState(tickets.build(), initialCards, routes);
    }

    /**
     * getter for the PlayerState tickets
     *
     * @return (SortedBag < Ticket >) the PlayerState tickets
     */
    public SortedBag<Ticket> tickets() {
        return SortedBag.of(tickets);
    }

    /**
     * compute a new PlayerState with more tickets
     *
     * @return (PlayerState) new PlayerState with more tickets
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(tickets.union(newTickets), cards, routes);
    }

    /**
     * getter for the PlayerState cards
     *
     * @return (SortedBag < Card >) the PlayerState cards
     */
    public SortedBag<Card> cards() {
        return SortedBag.of(cards);
    }

    /**
     * compute a new PlayerState with one more card
     *
     * @param card (Card) the card that will be added to the player's cards
     * @return (PlayerState) new PlayerState with one more card
     * @see #withAddedCards(SortedBag)
     */
    public PlayerState withAddedCard(Card card) {
        return withAddedCards(SortedBag.of(card));
    }

    /**
     * compute a new PlayerState with some additionalCards added to his cards
     *
     * @param additionalCards (SortedBag<Card>) the cards that will be added to the player's cards
     * @return (PlayerState) new PlayerState with some additionalCards
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        return new PlayerState(tickets, cards.union(additionalCards), routes);
    }

    /**
     * @param route (Route)
     * @return (boolean) true iff the player can take possession of the given Route according to his car and cards
     */
    public boolean canClaimRoute(Route route) {
        return (this.carCount() >= route.length() ? !possibleClaimCards(route).isEmpty() : false);
    }

    /**
     * compute the list of all possible cards the player can play to take possession of the given Route
     *
     * @param route the Route for which this method compute the possible claim cards
     *
     * @throws IllegalArgumentException if the player has less car then it takes to claim the Route
     * @see #possibleClaimCards(Route)
     * @return (List < SortedBag < Card > >)
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(this.carCount() >= route.length());

        List<SortedBag<Card>> allCards = route.possibleClaimCards();
        List<SortedBag<Card>> possibleCards = new ArrayList<>();

        for (SortedBag<Card> sbc : allCards) {
            if (cards.contains(sbc)) {
                possibleCards.add(sbc);
            }
        }
        return possibleCards;
    }

    /**
     * compute the list of all possible additional cards the player can play to take possession of the given Route
     * according the initial cards he played and the cards he drawn
     *
     * @param additionalCardsCount the number of cards the player must play
     * @param initialCards         cards that the player choose to claim the Route
     * @param drawnCards           cards that will implies additionalCardsCount cards for the player to play
     * @throws IllegalArgumentException if additionalCardsCount isn't in [1;3]
     *                                  if initialCards is empty or if it contains more than to kind for cards
     *                                  if there are not 3 drawnCards
     * @return (List < SortedBag < Card > >) the list of all possible additional cards
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS);
        Preconditions.checkArgument(!initialCards.isEmpty() && initialCards.toSet().size() <= 2);
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);

        List<Card> usableCard = new ArrayList<>();
        for (Card c : cards.difference(initialCards)) {
            if (c.equals(Card.LOCOMOTIVE) || c.equals(initialCards.get(0))){
                usableCard.add(c);
            }
        }

        Set<SortedBag<Card>> s = new HashSet<>();

        if (usableCard.size() >= additionalCardsCount) {
            s = SortedBag.of(usableCard).subsetsOfSize(additionalCardsCount);
        }
        List<SortedBag<Card>> sortedBagCardList = new ArrayList<>(s);
        sortedBagCardList.sort(Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));

        return sortedBagCardList;
    }

    /**
     * compute a new PlayerState with one route added to his routes and the cards he used removed from his cards
     *
     * @param route      the Route that the player claimed
     * @param claimCards the cards used for it
     * @return (PlayerState) a new PlayerState with one route added
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> newRoutes = new ArrayList<>(routes);
        newRoutes.add(route);
        return new PlayerState(tickets, cards.difference(claimCards), newRoutes);
    }

    /**
     * compute the number of points obtain by the player according to its tickets, can be negative
     *
     * @return (int) the number of points per tickets
     */
    public int ticketPoints() {
        int id = 0;

        for (Route r : routes) {
            id = Math.max(id, r.station1().id());
            id = Math.max(id, r.station2().id());
        }

        StationPartition.Builder spb = new StationPartition.Builder(id + 1);

        for (Route r : routes) {
            spb.connect(r.station1(), r.station2());
        }

        StationPartition connectivity = spb.build();

        int points = 0;
        for (Ticket t : tickets) {
            points += t.points(connectivity);
        }
        return points;
    }

    /**
     * compute all points the player add during the game
     *
     * @see #claimPoints()
     * @see #ticketPoints()
     * @return (int) the total number of points
     */
    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }
}
