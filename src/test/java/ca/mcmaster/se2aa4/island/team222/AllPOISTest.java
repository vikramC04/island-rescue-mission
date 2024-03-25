package ca.mcmaster.se2aa4.island.team222;

import static org.junit.Assert.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;

public class AllPOISTest {

    private AllPOIS allPOIS;
    private List<POI> creekLocations;

    @BeforeEach
    public void setUp() {
        creekLocations = new ArrayList<>();
        allPOIS = new AllPOIS(creekLocations);
    }

    @Test
    public void testAddCreek() {
        POI creek = new POI(new Coordinate(1, 2), "Creek1", POIType.CREEKS);
        allPOIS.addPoi(creek, POIType.CREEKS);
        
        List<POI> creeks = allPOIS.getCreeks();
        assertNotNull(creeks);
        assertEquals(1, creeks.size());
        assertTrue(creeks.contains(creek));
    }

    @Test
    public void testAddEmergencySite() {
        POI emergencySite = new POI(new Coordinate(3, 4), "EmergencySite1", POIType.SITES);
        allPOIS.addPoi(emergencySite, POIType.SITES);
        
        assertEquals(emergencySite, allPOIS.getEmergencySite());
    }

    @Test
    public void testGetEmergencySite() {
        POI emergencySite = new POI(new Coordinate(5, 6), "EmergencySite2", POIType.SITES);
        allPOIS.addPoi(emergencySite, POIType.SITES);
        
        assertEquals(emergencySite, allPOIS.getEmergencySite());
    }

    @Test
    public void testGetCreeks() {
        POI creek1 = new POI(new Coordinate(7, 8), "Creek1", POIType.CREEKS);
        POI creek2 = new POI(new Coordinate(9, 10), "Creek2", POIType.CREEKS);
        creekLocations.add(creek1);
        creekLocations.add(creek2);
        
        List<POI> creeks = allPOIS.getCreeks();
        assertNotNull(creeks);
        assertEquals(2, creeks.size());
        assertTrue(creeks.contains(creek1));
        assertTrue(creeks.contains(creek2));
    }
}
