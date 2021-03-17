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
        private final int stationCount;
        private final int[] representativeId;

        private int representative(int id) {
            return representativeId[id];
        }

        /**
         * constructor for the StationPartition Builder
         * build the representative id table of size stationCount
         * @param stationCount (int)
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
            this.stationCount = stationCount;
            representativeId = new int[stationCount];
            for (int i = 0; i < stationCount; i++) {
                representativeId[i] = i;
            }
        }

        /**
         * join set containing the two Stations and make one representative id
         * @param s1 (Station)
         * @param s2 (Station)
         * @return (Builder)
         */
        public Builder connect(Station s1, Station s2) {
            Preconditions.checkArgument(s1.id() >= 0 && s2.id() >= 0);
            int i = (representative(s1.id()) < representative(s2.id()) ? representative(s2.id()) : representative(s1.id()));
            representativeId[s1.id()] = representativeId[s2.id()] = i;
            return this;
        }

        /**
         * @return a new StationPartition with the built table
         */
        public StationPartition build() {

            for (int i = 0; i < representativeId.length; i++) {
                int representant = i;

                while(representative(representant) != representant){
                    representant = representative(representant);
                }
                representativeId[i] = representant;
            }
            return new StationPartition(representativeId);
        }
    }

}
