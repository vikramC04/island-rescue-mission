package ca.mcmaster.se2aa4.island.team222;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

import ca.mcmaster.se2aa4.island.team222.pois.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClosestCreekTest {

    private ClosestCreek closestCreek;
    private AllPOIS allPOIS;

    @BeforeEach
    public void setUp() {
        List<POI> creekLocations = new ArrayList<>();
        creekLocations.add(new POI(new Coordinate(1, 2), "Creek1", POIType.CREEKS));
        creekLocations.add(new POI(new Coordinate(3, 4), "Creek2", POIType.CREEKS));
        creekLocations.add(new POI(new Coordinate(5, 6), "Creek3", POIType.CREEKS));

        POI emergencySite = new POI(new Coordinate(7, 8), "EmergencySite", POIType.SITES);

        allPOIS = new AllPOIS(creekLocations);
        allPOIS.addPoi(emergencySite, POIType.SITES);

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
        allPOIS.addPoi(null, POIType.SITES);
        POI closest = closestCreek.findClosestCreek();
        assertNotNull(closest);
        assertEquals("Creek1", closest.getID());
    }

    @Test
    public void testFindClosestCreekWithNoCreeks() {
        allPOIS.getCreeks().clear();
        POI closest = closestCreek.findClosestCreek();
        assertNull(closest);
    }
}
