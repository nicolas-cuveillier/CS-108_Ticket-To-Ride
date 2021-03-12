package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

public final class StationPartition implements StationConnectivity{
    int[] representativeId;

    private StationPartition(int[] linksId) {
        representativeId = linksId;
    }
    
    @Override
    public boolean connected(Station st1, Station st2) {
        Preconditions.checkArgument(st1.id()>=0 && st2.id()>=0);
        return (st1.id()<representativeId.length && st2.id()<representativeId.length) ? representativeId[st1.id()]==representativeId[st2.id()] : st1.id()==st2.id();
    }

    public static final class Builder{
        private int stationCount = 0;
        int[] representativeId;
        
        private int representative(int id) {
            return representativeId[id];
        }
        
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
            this.stationCount = stationCount;
            representativeId = new int[stationCount];
            for(int i = 0; i<stationCount; i++) {
                representativeId[i] = i;
            }
        }
        
        public Builder connect(Station s1, Station s2) {
            Preconditions.checkArgument(s1.id()>=0 && s2.id()>=0);
            int i = (representative(s1.id())<representative(s2.id())?representative(s2.id()):representative(s1.id()));
            representativeId[s1.id()] = representativeId[s2.id()] = i;
            return this;
        }
        
        public StationPartition build() {
            return new StationPartition(representativeId);
        }
    }
    
}
