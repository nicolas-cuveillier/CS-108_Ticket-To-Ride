package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Color;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.List;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 */
public final class Info {
    private final String payerName;

    public Info(String payerName) {
        this.payerName = payerName;
    }

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

    public static String draw(List<String> playerNames, int points) {
        String draw = "";
        return draw;
    }

    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, payerName);
    }

    public String keptTickets(int count) {
        return String.format(StringsFr.KEPT_N_TICKETS, payerName, count, StringsFr.plural(count));
    }

    public String canPlay() {
        return String.format(StringsFr.CAN_PLAY, payerName);
    }

    public String drewTickets(int count) {
        return String.format(StringsFr.DREW_TICKETS, payerName, count, StringsFr.plural(count));
    }

    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, payerName);
    }

    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, payerName, cardName(card,1));
    }
    //add text for cards
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return String.format(StringsFr.CLAIMED_ROUTE, payerName, routeText(route), cards);
    }

    //add text for cards
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, payerName, routeText(route), initialCards);
    }
    //add text for drawncards
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        String text = String.format(StringsFr.ADDITIONAL_CARDS_ARE, drawnCards);
        if (additionalCost != 0) {
            text += String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, StringsFr.plural(additionalCost));
        } else {
            text += String.format(StringsFr.NO_ADDITIONAL_COST);
        }
        return text;
    }

    public String didNotClaimRoute(Route route) {
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, payerName, routeText(route));
    }

    public String lastTurnBegins(int carCount) {
        return String.format(StringsFr.LAST_TURN_BEGINS, payerName, carCount, StringsFr.plural(carCount));
    }

    public String getsLongestTrailBonus(Trail longestTrail) {
        return String.format(StringsFr.GETS_BONUS, payerName, longestTrail.station1() + StringsFr.EN_DASH_SEPARATOR + longestTrail.station2());
    }

    public String won(int points, int loserPoints) {
        return String.format(StringsFr.WINS, payerName, points, StringsFr.plural(points), loserPoints, StringsFr.plural(loserPoints));
    }

    private static String routeText(Route route){
       return route.station1().name() + StringsFr.EN_DASH_SEPARATOR + route.station2().name();
    }


}
