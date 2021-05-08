package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Arrays;

/**
 * @author Grégory Preisig (299489) & Nicolas Cuveillier (329672)
 * <p>
 * represent the notion of stationPartition (flattened), implements StationConnectivity
 */
public final class StationPartition implements StationConnectivity {
    private final int[] representativeId;

    private StationPartition(int[] linksId) {
        representativeId = Arrays.copyOf(linksId,linksId.length);
    }

    /**
     * {@inheritDoc} by checking the equality of their representative
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

        /**
         * Compute the representative of the station, is used in connect when connecting two stations so it
         * always return the representative of the partition
         *
         * @param id id of the station you want to compute the representative
         * @return (int) the representative
         */
        private int representative(int id) {
            int representative = id;

            while (representativeId[representative] != representative) {
                representative = representativeId[representative];
            }

            return representative;
        }

        /**
         * Constructor for the StationPartition Builder
         * instantiates the representative id table of size stationCount
         *
         * @param stationCount the biggest id of the Stations
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
         * @return (Builder) this
         * @throws IllegalArgumentException if one or the other id is negative
         */
        public Builder connect(Station s1, Station s2) {
            representativeId[representative(s1.id())] = representative(s2.id());
            return this;
        }

        /**
         * Builds the StationPartition following the representativeId mapping and flattened the partition
         *
         * @return a new StationPartition with the built table and flattened partition
         */
        public StationPartition build() {

            for (int i = 0; i < representativeId.length; i++) {
                int representative = i;

                while (representative(representative) != representative) {
                    representative = representative(representative);
                }
                representativeId[i] = representative;
            }

            return new StationPartition(Arrays.copyOf(representativeId,representativeId.length));
        }
    }

}
