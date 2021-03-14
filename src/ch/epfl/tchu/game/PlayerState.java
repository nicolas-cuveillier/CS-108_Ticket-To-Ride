package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public final class PlayerState extends PublicPlayerState {

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;
    private final List<Route> routes;

    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;
        this.routes = routes;
    }

    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);

        SortedBag.Builder<Ticket> tickets = new SortedBag.Builder<>();
        List<Route> routes = new ArrayList<>();
        return new PlayerState(tickets.build(), initialCards, routes);
    }

    public SortedBag<Ticket> tickets() {
        return tickets;
    }

    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(tickets.union(newTickets), cards, routes);
    }

    public SortedBag<Card> cards() {
        return cards;
    }

    public PlayerState withAddedCard(Card card) {
        SortedBag.Builder<Card> newCards = new SortedBag.Builder<>();
        newCards.add(cards).add(card);
        return new PlayerState(tickets, newCards.build(), routes);
    }

    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        return new PlayerState(tickets, cards.union(additionalCards), routes);
    }

    public boolean canClaimRoute(Route route) {
        SortedBag<Card> s = SortedBag.of(route.length(), Card.of(route.color()));
        return (this.carCount() >= route.length() && cards.contains(s));
    }

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

    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        routes.add(route);
        return new PlayerState(tickets, cards.difference(claimCards), routes);
    }

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

    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }
}
