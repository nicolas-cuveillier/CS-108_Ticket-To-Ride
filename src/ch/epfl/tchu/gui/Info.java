package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Color;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.List;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 * <p>
 * Information that player will see during the game gives as String
 */
public final class Info {
    private final String playerName;

    /**
     * Build a message generator link to the player
     *
     * @param payerName (String)
     */
    public Info(String payerName) {
        this.playerName = payerName;
    }

    /**
     * compute a card name according to its count
     *
     * @param card  (Card)
     * @param count (int)
     * @return (String)
     */
    public static String cardName(Card card, int count) {

        if (card.color().equals(Color.BLACK)) {
            return StringsFr.BLACK_CARD + StringsFr.plural(count);
        } else if (card.color().equals(Color.VIOLET)) {
            return StringsFr.VIOLET_CARD + StringsFr.plural(count);
        } else if (card.color().equals(Color.BLUE)) {
            return StringsFr.BLUE_CARD + StringsFr.plural(count);
        } else if (card.color().equals(Color.GREEN)) {
            return StringsFr.GREEN_CARD + StringsFr.plural(count);
        } else if (card.color().equals(Color.YELLOW)) {
            return StringsFr.YELLOW_CARD + StringsFr.plural(count);
        } else if (card.color().equals(Color.ORANGE)) {
            return StringsFr.ORANGE_CARD + StringsFr.plural(count);
        } else if (card.color().equals(Color.RED)) {
            return StringsFr.RED_CARD + StringsFr.plural(count);
        } else if (card.color().equals(Color.WHITE)) {
            return StringsFr.WHITE_CARD + StringsFr.plural(count);
        } else {
            return StringsFr.LOCOMOTIVE_CARD + StringsFr.plural(count);
        }

    }

    /**
     * message when the game end and player are ex æqo
     *
     * @param playerNames (List<String>)
     * @param points      (int)
     * @return (String)
     */
    //TODO
    public static String draw(List<String> playerNames, int points) {
        String draw = "";
        return draw;
    }

    /**
     * message saying that this player is going to play first
     *
     * @return (String)
     */
    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
    }

    /**
     * message saying that this player is keeping count billets
     *
     * @return (String)
     */
    public String keptTickets(int count) {
        return String.format(StringsFr.KEPT_N_TICKETS, playerName, count, StringsFr.plural(count));
    }

    /**
     * message saying that the player can now playing
     *
     * @return (String)
     */
    public String canPlay() {
        return String.format(StringsFr.CAN_PLAY, playerName);
    }

    /**
     * message saying that the player drew count tickets
     *
     * @return (String)
     */
    public String drewTickets(int count) {
        return String.format(StringsFr.DREW_TICKETS, playerName, count, StringsFr.plural(count));
    }

    /**
     * message saying that the player drew a blind card from the deck
     *
     * @return (String)
     */
    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }

    /**
     * message saying that the player drew a visible card
     *
     * @return (String)
     */
    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, playerName, cardName(card, 1));
    }

    /**
     * message saying that the player took this Route with these cards
     *
     * @return (String)
     */
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return String.format(StringsFr.CLAIMED_ROUTE, playerName, routeText(route), cardText(cards));
    }

    /**
     * message saying that the player claim this tunnel with these cards
     *
     * @return (String)
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, playerName, routeText(route), cardText(initialCards));
    }

    /**
     * message saying that the player drew three additional cards and that it implies for him to play additionalCost of cards
     *
     * @return (String)
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        String text = String.format(StringsFr.ADDITIONAL_CARDS_ARE, cardText(drawnCards));
        if (additionalCost != 0) {
            text += String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, StringsFr.plural(additionalCost));
        } else {
            text += String.format(StringsFr.NO_ADDITIONAL_COST);
        }
        return text;
    }

    /**
     * message saying that the player did not took this tunnel
     *
     * @return (String)
     */
    public String didNotClaimRoute(Route route) {
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, routeText(route));
    }

    /**
     * message saying that the last turn of the game begins
     *
     * @return (String)
     */
    public String lastTurnBegins(int carCount) {
        return String.format(StringsFr.LAST_TURN_BEGINS, playerName, carCount, StringsFr.plural(carCount));
    }

    /**
     * message saying that the player got the 10 points bonus thanks to the longest Trail
     *
     * @return (String)
     */
    public String getsLongestTrailBonus(Trail longestTrail) {
        return String.format(StringsFr.GETS_BONUS, playerName, longestTrail.station1() + StringsFr.EN_DASH_SEPARATOR + longestTrail.station2());
    }

    /**
     * message saying that the player won
     *
     * @return (String)
     */
    public String won(int points, int loserPoints) {
        return String.format(StringsFr.WINS, playerName, points, StringsFr.plural(points), loserPoints, StringsFr.plural(loserPoints));
    }

    private static String routeText(Route route) {
        return route.station1().name() + StringsFr.EN_DASH_SEPARATOR + route.station2().name();
    }

    private static String cardText(SortedBag<Card> cards) {
        List<Card> cardsList = cards.toList().subList(1, cards.size());
        String s = "";
        String text = cardName(cards.get(0), cards.countOf(cards.get(0)));
        for (Card c : cardsList) {
            int n = cards.countOf(c);
            s += String.format("%s %s", n, cardName(c, n));
            text += String.join(", ", s);
        }
        return text;
    }


}
