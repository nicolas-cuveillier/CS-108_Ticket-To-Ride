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
        this.tickets = SortedBag.of(tickets);
        this.cards = SortedBag.of(cards);
        this.routes = List.copyOf(routes);
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
        return withAddedCards(SortedBag.of(card));
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
        return (this.carCount() >= route.length() ? !possibleClaimCards(route).isEmpty() : false);
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
        Preconditions.checkArgument(((initialCards.contains(Card.BLACK)?1:0)+(initialCards.contains(Card.BLUE)?1:0)+(initialCards.contains(Card.GREEN)?1:0)+(initialCards.contains(Card.LOCOMOTIVE)?1:0)+(initialCards.contains(Card.ORANGE)?1:0)+(initialCards.contains(Card.RED)?1:0)+(initialCards.contains(Card.VIOLET)?1:0)+(initialCards.contains(Card.WHITE)?1:0)+(initialCards.contains(Card.YELLOW)?1:0))<=2);
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);

        List<Card> usableCard = cards.toList();
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
        List<Route> newRoutes = new ArrayList<>(routes);
        newRoutes.add(route);
        return new PlayerState(tickets, cards.difference(claimCards), newRoutes);
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
                id = r.station2().id();
            }

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
     * @return (int)
     */
    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }
}
