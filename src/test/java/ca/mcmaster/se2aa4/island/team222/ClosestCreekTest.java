package ca.mcmaster.se2aa4.island.team222;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

public class ClosestCreekTest {

    private ClosestCreek closestCreek;
    private AllPOIS allPOIS;

    @Before
    public void setUp() {
        List<POI> creekLocations = new ArrayList<>();
        creekLocations.add(new POI(new Coordinate(1, 2), "Creek1", POIType.CREEK));
        creekLocations.add(new POI(new Coordinate(3, 4), "Creek2", POIType.CREEK));
        creekLocations.add(new POI(new Coordinate(5, 6), "Creek3", POIType.CREEK));

        POI emergencySite = new POI(new Coordinate(7, 8), "EmergencySite", POIType.SITE);

        allPOIS = new AllPOIS(creekLocations);
        allPOIS.addPoi(emergencySite, POIType.SITE);

        closestCreek = new ClosestCreek(allPOIS);
    }

    @Test
    public void testFindClosestCreekWithEmergencySite() {
        POI closest = closestCreek.findClosestCreek();
        assertNotNull(closest);
        assertEquals("Creek3", closest.getID());
    }

    @Test
    public void testFindClosestCreekWithoutEmergencySite() {
        // Remove the emergency site
        allPOIS.addPoi(null, POIType.SITE);
        POI closest = closestCreek.findClosestCreek();
        assertNotNull(closest);
        assertEquals("Creek1", closest.getID()); // Closest creek should be the first one
    }

    @Test
    public void testFindClosestCreekWithNoCreeks() {
        // Remove all creeks
        allPOIS.getCreeks().clear();
        POI closest = closestCreek.findClosestCreek();
        assertNull(closest); // Since there are no creeks, it should return null
    }
}
