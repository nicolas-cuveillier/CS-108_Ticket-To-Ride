package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;

public final class Constants {
    private Constants() {
    }

    /**
     * Total number of wagon cards for each color.
     */
    public static final int CAR_CARDS_COUNT = 12;

    /**
     * Total number of locomotive cards.
     */
    public static final int LOCOMOTIVE_CARDS_COUNT = 14;

    /**
     * Total number of cards.
     */
    public static final int TOTAL_CARDS_COUNT =
            LOCOMOTIVE_CARDS_COUNT + CAR_CARDS_COUNT * Color.COUNT;

    /**
     * Collection of all the cards (110 in total).
     */
    public static final SortedBag<Card> ALL_CARDS = computeAllCards();

    private static SortedBag<Card> computeAllCards() {
        var cardsBuilder = new SortedBag.Builder<Card>();
        cardsBuilder.add(LOCOMOTIVE_CARDS_COUNT, Card.LOCOMOTIVE);
        for (Card card : Card.CARS)
            cardsBuilder.add(CAR_CARDS_COUNT, card);
        assert cardsBuilder.size() == TOTAL_CARDS_COUNT;
        return cardsBuilder.build();
    }

    /**
     * Id for the deck.
     */
    public static final int DECK_SLOT = -1;

    /**
     * List of the ids of the visible card.
     */
    public static final List<Integer> FACE_UP_CARD_SLOTS = List.of(0, 1, 2, 3, 4);

    /**
     * Number of slots for the visible cards.
     */
    public static final int FACE_UP_CARDS_COUNT = FACE_UP_CARD_SLOTS.size();

    /**
     * Number of tickets given to each player at the beginning of the game.
     */
    public static final int INITIAL_TICKETS_COUNT = 5;

    /**
     * Number of cards given to each player at the beginning of the game.
     */
    public static final int INITIAL_CARDS_COUNT = 4;

    /**
     * Number of wagons each player has at the beginning of the game.
     */
    public static final int INITIAL_CAR_COUNT = 40;

    /**
     * Number of tickets drawn each time during the game.
     */
    public static final int IN_GAME_TICKETS_COUNT = 3;

    /**
     * Maximum number of tickets a player can discard after a ticket draw.
     */
    public static final int DISCARDABLE_TICKETS_COUNT = 2;

    /**
     * Number of cards to draw as additional cards to claim a tunnel.
     */
    public static final int ADDITIONAL_TUNNEL_CARDS = 3;

    /**
     * Number of points awarded for a claimed route of length i, 1<= i <= 6.
     * the first element (index 0) has an invalid value since there are no routes with a length of 0.
     */
    public static final List<Integer> ROUTE_CLAIM_POINTS =
            List.of(Integer.MIN_VALUE, 1, 2, 4, 7, 10, 15);

    /**
     * Minimum length of any route.
     */
    public static final int MIN_ROUTE_LENGTH = 1;

    /**
     * Maximum length of any route.
     */
    public static final int MAX_ROUTE_LENGTH = ROUTE_CLAIM_POINTS.size() - 1;

    /**
     * Number of bonus points given to the player(s) with the longest trail.
     */
    public static final int LONGEST_TRAIL_BONUS_POINTS = 10;
}
