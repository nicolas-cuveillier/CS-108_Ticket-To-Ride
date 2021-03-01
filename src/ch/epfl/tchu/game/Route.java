package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;

public final class Route {

    public enum Level {
        OVERGROUND,
        UNDERGROUND;
    }

    private final String id;
    private final Station station1, station2;
    private final Level level;
    private final Color color;
    private final int length;

    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        Preconditions.checkArgument(!station1.name().equals(station2.name()) && length <= Constants.MAX_ROUTE_LENGTH
                && length >= Constants.MIN_ROUTE_LENGTH);

        if (station1 == null || station2 == null || level == null) {
            throw new NullPointerException("Station | Level | both is null");
        }

        this.id = id;
        this.station1 = station1;
        this.station2 = station2;
        this.level = level;
        this.length = length;
        this.color = color;
    }

    public List<Station> stations() {
        return List.of(station1, station2);
    }

    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(station.id() == station1.id() || station.id() == station2.id());
        return (station.id() == station1.id() ? station2 : station1);
    }

    public int claimPoints() {
        return Constants.ROUTE_CLAIM_POINTS.get(length);
    }

    //TODO : complete the if statement for route neutre
    public List<SortedBag<Card>> possibleClaimCards() {
        SortedBag.Builder sBuilder;
        List<SortedBag<Card>> possibleClaimCards = new ArrayList<>();

        if (color == null) {
            if (level.equals(Level.UNDERGROUND)) {
                for (Color c : Color.ALL) {
                    sBuilder = new SortedBag.Builder();
                    sBuilder.add(length, c);
                    possibleClaimCards.add(sBuilder.build());
                }
            } else {

            }
        } else {
            if (level.equals(Level.UNDERGROUND)) {
                for (int i = 0; i <= length; i++) {
                    sBuilder = new SortedBag.Builder();
                    sBuilder.add(length - i, Card.of(color)).add(i, Card.LOCOMOTIVE);
                    possibleClaimCards.add(sBuilder.build());
                }
            } else {
                sBuilder = new SortedBag.Builder();
                sBuilder.add(length, Card.of(color));
                possibleClaimCards.add(sBuilder.build());
            }
            return possibleClaimCards;
        }

        return null;
    }//to be completed

    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(drawnCards.size() == 3 && this.level == Level.UNDERGROUND);
        int additionalCard = 0;

        for (Card dc : drawnCards) {
            if (claimCards.contains(dc) || dc.color() == (Card.LOCOMOTIVE.color())) {
                ++additionalCard;
            }
        }
        return additionalCard;
    }

    public String id() {
        return id;
    }

    public Station station1() {
        return station1;
    }

    public Station station2() {
        return station2;
    }

    public Level level() {
        return level;
    }

    public Color color() {
        return color;
    }

    public int length() {
        return length;
    }
}
