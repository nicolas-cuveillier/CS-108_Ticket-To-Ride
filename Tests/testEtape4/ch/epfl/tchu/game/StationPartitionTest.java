package ch.epfl.tchu.game;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StationPartitionTest {

    @Test
    void notConnectedWithOutOfBoundsAndDifferentStations() {
        StationPartition.Builder sp = new StationPartition.Builder(5);
        assertFalse(sp.build().connected(ChMap.stations().get(6), ChMap.stations().get(7)));
    }

    @Test
    void notConnectedWithNotConnectedStations() {
        StationPartition.Builder sp = new StationPartition.Builder(5);
        sp.connect(ChMap.stations().get(1),ChMap.stations().get(2));
        assertFalse(sp.build().connected(ChMap.stations().get(0), ChMap.stations().get(1)));
        assertTrue(sp.build().connected(ChMap.stations().get(1),ChMap.stations().get(2)));
    }
    
    @Test
    void connectedWithOutOfBoundsAndSameStations() {
        StationPartition.Builder sp = new StationPartition.Builder(5);
        assertTrue(sp.build().connected(ChMap.stations().get(6), ChMap.stations().get(6)));
    }
    
    @Test
    void connectedWithCorrectStations() {
        StationPartition.Builder sp = new StationPartition.Builder(5);
        sp.connect(ChMap.stations().get(0), ChMap.stations().get(1));
        assertTrue(sp.build().connected(ChMap.stations().get(0), ChMap.stations().get(1)));
    }
}
