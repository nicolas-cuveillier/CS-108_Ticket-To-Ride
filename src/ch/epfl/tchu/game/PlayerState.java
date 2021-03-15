package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

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
        this.tickets = tickets;
        this.cards = cards;
        this.routes = routes;
    }

    /**
     * static method for building the initial state of a PlayerState
     *
     * @param initialCards (SortedBag<Ticket>)
     * @return (PlayerState)
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
     * @return (SortedBag < Ticket >)
     */
    public SortedBag<Ticket> tickets() {
        return SortedBag.of(tickets);
    }

    /**
     * compute a new PlayerState with more tickets
     *
     * @return (PlayerState)
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(tickets.union(newTickets), cards, routes);
    }

    /**
     * getter for the PlayerState cards
     *
     * @return (SortedBag < Card >)
     */
    public SortedBag<Card> cards() {
        return SortedBag.of(cards);
    }

    /**
     * compute a new PlayerState with one more card
     *
     * @param card (Card)
     * @return (PlayerState)
     */
    public PlayerState withAddedCard(Card card) {
        SortedBag.Builder<Card> newCards = new SortedBag.Builder<>();
        newCards.add(cards).add(card);
        return new PlayerState(tickets, newCards.build(), routes);
    }

    /**
     * compute a new PlayerState with some additionalCards added to his cards
     *
     * @param additionalCards (SortedBag<Card>)
     * @return (PlayerState)
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        return new PlayerState(tickets, cards.union(additionalCards), routes);
    }

    /**
     * @param route (Route)
     * @return (boolean) true iff the player can take possession of the given Route according to his car and cards
     */
    public boolean canClaimRoute(Route route) {
        SortedBag<Card> s = SortedBag.of(route.length(), Card.of(route.color()));
        return (this.carCount() >= route.length() && cards.contains(s));
    }

    /**
     * compute the list of all possible cards the player can play to take possession of the given Route
     *
     * @param route (Route)
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
     * @param additionalCardsCount (int)
     * @param initialCards         (SortedBag<Card>)
     * @param drawnCards           (SortedBag<Card>)
     * @return (List < SortedBag < Card > >)
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= 3);
        Preconditions.checkArgument(!initialCards.isEmpty());//+ ne contient pas plus de 2 types de cartes différents.
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);

        List<Card> usableCard = SortedBag.of(cards).toList();
        usableCard.remove(initialCards.toList());
        usableCard.removeIf(card -> !card.equals(initialCards.get(0)) && !card.equals(Card.LOCOMOTIVE));

        Set<SortedBag<Card>> s = SortedBag.of(usableCard).subsetsOfSize(additionalCardsCount);
        List<SortedBag<Card>> sortedBagCardList = new ArrayList<>(s);
        sortedBagCardList.sort(Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));

        return sortedBagCardList;
    }

    /**
     * compute a new PlayerState with one route added to his routes and the cards he used removed from his cards
     *
     * @param route      (Route)
     * @param claimCards (SortedBag<Card>)
     * @return (PlayerState)
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        routes.add(route);
        return new PlayerState(tickets, cards.difference(claimCards), routes);
    }

    /**
     * compute the number of points obtain by the player according to its tickets, can be negative
     *
     * @return (int)
     */
    public int ticketPoints() {
        int id = 0;
        for (Route r : routes) {

            if (r.station1().id() > id) {
                id = r.station1().id();
            }

            if (r.station2().id() > id) {
                id = r.station1().id();
            }

        }

        StationPartition.Builder b = new StationPartition.Builder(id + 1);

        int points = 0;
        for (Ticket t : tickets) {
            points += t.points(b.build());
        }
        return points;
    }

    /**
     * compute all points the player add during the game
     *
     * @return (int)
     */
    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }
}
