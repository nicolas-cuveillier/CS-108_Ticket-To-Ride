package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;
import java.util.stream.Collectors;

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
     * Primary constructor of a PlayerState, builds a PlayerState with its Tickets, Cards and Routes
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
     * Static method building the initial PlayerState
     *
     * @param initialCards the initial cards of the player
     * @return (PlayerState)
     * @throws IllegalArgumentException if there are not four initial cards
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), initialCards, List.of());
    }

    /**
     * Getter for the private field tickets
     *
     * @return (SortedBag < Ticket >) the PlayerState tickets
     */
    public SortedBag<Ticket> tickets() {
        return SortedBag.of(tickets);
    }

    /**
     * Computes a new PlayerState with more tickets
     *
     * @return (PlayerState) new PlayerState with more tickets
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(tickets.union(newTickets), cards, routes);
    }

    /**
     * Getter for the private field cards
     *
     * @return (SortedBag < Card >) the PlayerState cards
     */
    public SortedBag<Card> cards() {
        return SortedBag.of(cards);
    }

    /**
     * Computes a new PlayerState with one additional card
     *
     * @param card the card that will be added to the player's cards
     * @return (PlayerState) new PlayerState with one more card
     * @see #withAddedCards(SortedBag)
     */
    public PlayerState withAddedCard(Card card) {
        return withAddedCards(SortedBag.of(card));
    }

    /**
     * Computes a new PlayerState with the given additionalCards added to his cards
     *
     * @param additionalCards the cards that will be added to the player's cards
     * @return (PlayerState) new PlayerState with some additionalCards
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        return new PlayerState(tickets, cards.union(additionalCards), routes);
    }

    /**
     * Tests if the player can claim the specified route
     *
     * @param route the route that the player try to claim
     * @return (boolean) true iff the player can take possession of the given Route according to his car and cards
     */
    public boolean canClaimRoute(Route route) {
        return (this.carCount() >= route.length() ? !possibleClaimCards(route).isEmpty() : false);
    }

    /**
     * Computes the list of all possible cards the player can play to take possession of the given Route
     *
     * @param route the Route for which this method compute the possible claim cards
     * @return (List < SortedBag < Card > >) the list of all possible cards the player can use to take possession of the Route
     * @throws IllegalArgumentException if the player has less car then it takes to claim the Route
     * @see #possibleClaimCards(Route)
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(this.carCount() >= route.length());

        List<SortedBag<Card>> allCards = route.possibleClaimCards();

        List<SortedBag<Card>> possibleCards = allCards.stream()
                .filter(i -> cards.contains(i))
                .collect(Collectors.toList());

        return possibleCards;
    }

    /**
     * Computes the list of all possible additional cards the player can play to take possession of the given Route
     * according the initial cards he played and the cards he drawn
     *
     * @param additionalCardsCount the number of cards the player must play
     * @param initialCards         cards that the player choose to claim the Route
     * @param drawnCards           cards that will implies additionalCardsCount cards for the player to play
     * @return (List< SortedBag < Card > >) the list of all possible additional cards
     * @throws IllegalArgumentException if additionalCardsCount isn't in [1;3]
     *                                  if initialCards is empty or if it contains more than to kind for cards
     *                                  if there are not 3 drawnCards
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS);
        Preconditions.checkArgument(!initialCards.isEmpty() && initialCards.toSet().size() <= 2);
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);

        List<Card> usableCard = cards.difference(initialCards).stream()
                .filter(c -> c.equals(Card.LOCOMOTIVE) || c.equals(initialCards.get(0)))
                .collect(Collectors.toList());

        Set<SortedBag<Card>> additionalCardsSet = new HashSet<>();

        if (usableCard.size() >= additionalCardsCount) {
            additionalCardsSet = SortedBag.of(usableCard).subsetsOfSize(additionalCardsCount);
        }

        List<SortedBag<Card>> additionalCards = new ArrayList<>(additionalCardsSet);
        additionalCards.sort(Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));

        return additionalCards;
    }

    /**
     * Computes a new PlayerState with one route added to his routes and the cards he used removed from his cards
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
     * Computes the points obtained by the player according to its tickets, can be negative
     *
     * @return (int) the number of points per tickets
     */
    public int ticketPoints() {
        int id = 0;

        for (Route r : routes) {
            id = Math.max(id, r.station1().id());
            id = Math.max(id, r.station2().id());
        }

        StationPartition.Builder stationPartitionBuilder = new StationPartition.Builder(id + 1);

        routes.forEach(route -> stationPartitionBuilder.connect(route.station1(), route.station2()));
        StationPartition connectivity = stationPartitionBuilder.build();

        int points = tickets.stream()
                .mapToInt(i -> i.points(connectivity))
                .sum();

        return points;
    }

    /**
     * Computes the total points the player obtained during the game
     *
     * @return (int) the total number of points
     * @see #claimPoints()
     * @see #ticketPoints()
     */
    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }
}
