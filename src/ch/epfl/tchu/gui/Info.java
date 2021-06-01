package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * <h1>Info</h1>
 * Handles the creation of info messages displayed to the players during the game. Returns the message as a String.
 *
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 */
public final class Info {
    private final String playerName;

    /**
     * Build a message generator link to the player.
     *
     * @param payerName the player name
     */
    public Info(String payerName) {
        this.playerName = payerName;
    }

    /**
     * Compute a card name according to its count.
     *
     * @param card  the card that the name will be return
     * @param count the number of card(s)
     * @return the card name
     */
    public static String cardName(Card card, int count) {

        switch (card) {
            case BLACK:
                return StringsFr.BLACK_CARD + StringsFr.plural(count);
            case VIOLET:
                return StringsFr.VIOLET_CARD + StringsFr.plural(count);
            case BLUE:
                return StringsFr.BLUE_CARD + StringsFr.plural(count);
            case GREEN:
                return StringsFr.GREEN_CARD + StringsFr.plural(count);
            case YELLOW:
                return StringsFr.YELLOW_CARD + StringsFr.plural(count);
            case ORANGE:
                return StringsFr.ORANGE_CARD + StringsFr.plural(count);
            case RED:
                return StringsFr.RED_CARD + StringsFr.plural(count);
            case WHITE:
                return StringsFr.WHITE_CARD + StringsFr.plural(count);
            case LOCOMOTIVE:
                return StringsFr.LOCOMOTIVE_CARD + StringsFr.plural(count);
            default:
                throw new Error();
        }
    }

    /**
     * Message saying that this player is going to play first.
     *
     * @return (String)
     */
    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
    }

    /**
     * Message saying that this player is keeping count billets.
     *
     * @param count the number of ticket(s)
     * @return (String)
     */
    public String keptTickets(int count) {
        return String.format(StringsFr.KEPT_N_TICKETS, playerName, count, StringsFr.plural(count));
    }

    /**
     * Message saying that the player can now playing.
     *
     * @return (String)
     */
    public String canPlay() {
        return String.format(StringsFr.CAN_PLAY, playerName);
    }

    /**
     * Message saying that the player drew count tickets.
     *
     * @param count the number of ticket(s)
     * @return (String)
     */
    public String drewTickets(int count) {
        return String.format(StringsFr.DREW_TICKETS, playerName, count, StringsFr.plural(count));
    }

    /**
     * Message saying that the player drew a blind card from the deck.
     *
     * @return (String)
     */
    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }

    /**
     * Message saying that the player drew a visible card.
     *
     * @param card the card that is drew
     * @return (String)
     */
    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, playerName, cardName(card, 1));
    }

    /**
     * Message saying that the player took this Route with these cards.
     *
     * @param route the taken Route
     * @param cards the cards that the player used to claim the Route
     * @return (String)
     */
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return String.format(StringsFr.CLAIMED_ROUTE, playerName, routeText(route), cardText(cards));
    }

    /**
     * Message saying that the player claim this tunnel with these cards.
     *
     * @param route        the Route that the player want to claim
     * @param initialCards the cards that the player initially used to ask for the Route
     * @return (String)
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, playerName, routeText(route), cardText(initialCards));
    }

    /**
     * Message saying that additional cards are draw.
     *
     * @param drawnCards     the cards that the player drew
     * @param additionalCost the cost implied by the cards
     * @return a message saying that the player drew cards that will imply a additionalCost of cards
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        StringBuilder text = new StringBuilder(String.format(StringsFr.ADDITIONAL_CARDS_ARE, cardText(drawnCards)));
        if (additionalCost != 0)
            text.append(String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, StringsFr.plural(additionalCost)));
        else
            text.append(StringsFr.NO_ADDITIONAL_COST);

        return text.toString();
    }

    /**
     * Message saying that the player did not took this tunnel.
     *
     * @param route the route that the player didn't claimed
     * @return (String)
     */
    public String didNotClaimRoute(Route route) {
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, routeText(route));
    }

    /**
     * Message saying that the last turn of the game begins.
     *
     * @param carCount the number of car the player has left
     * @return (String)
     */
    public String lastTurnBegins(int carCount) {
        return String.format(StringsFr.LAST_TURN_BEGINS, playerName, carCount, StringsFr.plural(carCount));
    }

    /**
     * Message saying that the player got the 10 points bonus thanks to the longest Trail
     *
     * @param longestTrail the Trail that allowed the player to have bonus
     * @return (String)
     */
    public String getsLongestTrailBonus(Trail longestTrail) {
        return String.format(StringsFr.GETS_BONUS, playerName, longestTrail.station1() + StringsFr.EN_DASH_SEPARATOR + longestTrail.station2());
    }

    private static String routeText(Route route) {
        return route.station1().name() + StringsFr.EN_DASH_SEPARATOR + route.station2().name();
    }

    /**
     * Compute the text representing the sortedBag of cards in a human way.
     *
     * @param cards the sortedBag of cards for which the text will be computed
     * @return a string which correspond to the textual representation of a sortedBag of cards
     */
    public static String cardText(SortedBag<Card> cards) {

        List<Card> trashList = new ArrayList<>();
        List<Card> cardsList = cards.toList().subList(1, cards.size());

        final StringBuilder text = new StringBuilder(cards.countOf(cards.get(0)) + " " + cardName(cards.get(0), cards.countOf(cards.get(0))));
        trashList.add(cards.get(0));

        for (Card c : cardsList) {

            if (!trashList.contains(c)) {
                int n = cards.countOf(c);

                if (cards.get(cards.size() - 1).equals(c) && !cards.get(0).equals(c))
                    text.append(" et ");
                else
                    text.append(", ");

                text.append(String.format("%s %s", n, cardName(c, n)));
                trashList.add(c);
            }
        }

        return text.toString();
    }

    /**
     * Method use at the end of the game to compute the message for the ranking of the game.
     *
     * @param playersPoint the map of all players linked to their points
     * @param playersName  the map of all players linked to their name
     * @return a String which contains the ranking of all players
     */
    public static String ranking(Map<PlayerId, Integer> playersPoint, Map<PlayerId, String> playersName) {

        //used to sort players according to their points for computing ranking
        Map<PlayerId, Integer> sortedPlayersPoints = new TreeMap<>(((o1, o2) -> {
            if (playersPoint.get(o1).equals(playersPoint.get(o2))) return 0;
            else if (playersPoint.get(o1) > playersPoint.get(o2)) return 1;
            else return -1;
        }));

        for (PlayerId pId : playersPoint.keySet()) {
            sortedPlayersPoints.put(pId, playersPoint.get(pId));
        }

        //compute the ranking
        StringBuilder builder = new StringBuilder();
        builder.append(StringsFr.RANKING);
        for (PlayerId pId : sortedPlayersPoints.keySet()) {
            builder.append(String.format(StringsFr.PLAYER_POINTS, playersName.get(pId), sortedPlayersPoints.get(pId)));
        }
        return builder.toString();
    }

}
