package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * @author Grégory Preisig & Nicolas Cuveillier
 * <p>
 * represent the notion of stationPartition (flattened), implements StationConnectivity
 */
public final class StationPartition implements StationConnectivity {
    private final int[] representativeId;

    private StationPartition(int[] linksId) {
        representativeId = linksId;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if one or the other id is negative
     */
    @Override
    public boolean connected(Station st1, Station st2) {
        Preconditions.checkArgument(st1.id() >= 0 && st2.id() >= 0);
        return (st1.id() < representativeId.length && st2.id() < representativeId.length) ? representativeId[st1.id()] == representativeId[st2.id()] : st1.id() == st2.id();
    }

    /**
     * Builder for a StationPartition
     */
    public static final class Builder {
        private final int[] representativeId;

        private int representative(int id) {
            return representativeId[id];
        }

        /**
         * constructor for the StationPartition Builder
         * build the representative id table of size stationCount
         *
         * @param stationCount (int) the biggest id of the Stations
         * @throws IllegalArgumentException if stationCount is negative
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
            representativeId = new int[stationCount];
            for (int i = 0; i < stationCount; i++) {
                representativeId[i] = i;
            }
        }

        /**
         * join "set" containing the two Stations and make one representative id for both Stations
         *
         * @param s1 the first Station to connect
         * @param s2 the second Station to connect
         * @return (Builder) this
         * @throws IllegalArgumentException if one or the other id is negative
         */
        public Builder connect(Station s1, Station s2) {
            Preconditions.checkArgument(s1.id() >= 0 && s2.id() >= 0);
            int i = (Math.max(representative(s1.id()), representative(s2.id())));
            representativeId[s1.id()] = representativeId[s2.id()] = i;
            return this;
        }

        /**
         * @return a new StationPartition with the built table and flattened partition
         */
        public StationPartition build() {

            for (int i = 0; i < representativeId.length; i++) {
                int representant = i;

                while (representative(representant) != representant) {
                    representant = representative(representant);
                }
                representativeId[i] = representant;
            }
            return new StationPartition(representativeId);
        }
    }

}