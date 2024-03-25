package ca.mcmaster.se2aa4.island.team222;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class POITest {

    private Coordinate coordinate;
    private String id;
    private POIType type;
    private POI poi;

    @BeforeEach
    public void setUp() {
        id = "761a3ca6-be72-40d3-ae17-2ef01330f7df";
        coordinate = new Coordinate(10, 2);
        type = POIType.SITES;
        poi = new POI(coordinate, id, type);
    }

    @AfterEach
    public void tearDown() {
        coordinate = null;
        id = null;
        type = null;
        poi = null;
    }

    @Test
    public void testGetX() {
        assertEquals(10, poi.getX());
    }
    @Test
    public void testGetY() {
        assertEquals(2, poi.getY());
    }

    @Test
    public void testGetID() {
        assertEquals("761a3ca6-be72-40d3-ae17-2ef01330f7df", poi.getID());
    }

    @Test
    public void testGetPoiType() {
        assertEquals(POIType.SITES, poi.getPoiType());
    }

}
