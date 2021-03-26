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
        return (st1.id() < representativeId.length && st2.id() < representativeId.length) ? representativeId[st1.id()] == representativeId[st2.id()] : st1.id() == st2.id();
    }

    /**
     * Builder for a StationPartition
     */
    public static final class Builder {
        private final int[] representativeId;

        private int representative(int id) {
            int representant = id;

            while (representativeId[representant] != representant) {
                representant = representativeId[representant];
            }

            return representant;
        }

        /**
         * Constructor for the StationPartition Builder
         * instantiates the representative id table of size stationCount
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
         * Connects the two stations ,joins the two stations sets and chooses a representative for the new set
         *
         * @param s1 the first Station to connect
         * @param s2 the second Station to connect
         * @throws IllegalArgumentException if one or the other id is negative
         * @return (Builder) this
         */
        public Builder connect(Station s1, Station s2) {
            representativeId[representative(s1.id())] = representative(s2.id());
            return this;
        }

        /**
         * Builds the StationPartition following the representativeId mapping and flattends the partition
         * 
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
