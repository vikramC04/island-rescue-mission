package ca.mcmaster.se2aa4.island.team222;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;

public class AllPOISTest {

    private AllPOIS allPOIS;
    private List<POI> creekLocations;

    @Before
    public void setUp() {
        creekLocations = new ArrayList<>();
        allPOIS = new AllPOIS(creekLocations);
    }

    @Test
    public void testAddCreek() {
        POI creek = new POI(new Coordinate(1, 2), "Creek1", POIType.CREEK);
        allPOIS.addPoi(creek, POIType.CREEK);
        
        List<POI> creeks = allPOIS.getCreeks();
        assertNotNull(creeks);
        assertEquals(1, creeks.size());
        assertTrue(creeks.contains(creek));
    }

    @Test
    public void testAddEmergencySite() {
        POI emergencySite = new POI(new Coordinate(3, 4), "EmergencySite1", POIType.SITE);
        allPOIS.addPoi(emergencySite, POIType.SITE);
        
        assertEquals(emergencySite, allPOIS.getEmergencySite());
    }

    @Test
    public void testGetEmergencySite() {
        POI emergencySite = new POI(new Coordinate(5, 6), "EmergencySite2", POIType.SITE);
        allPOIS.addPoi(emergencySite, POIType.SITE);
        
        assertEquals(emergencySite, allPOIS.getEmergencySite());
    }

    @Test
    public void testGetCreeks() {
        POI creek1 = new POI(new Coordinate(7, 8), "Creek1", POIType.CREEK);
        POI creek2 = new POI(new Coordinate(9, 10), "Creek2", POIType.CREEK);
        creekLocations.add(creek1);
        creekLocations.add(creek2);
        
        List<POI> creeks = allPOIS.getCreeks();
        assertNotNull(creeks);
        assertEquals(2, creeks.size());
        assertTrue(creeks.contains(creek1));
        assertTrue(creeks.contains(creek2));
    }
}
