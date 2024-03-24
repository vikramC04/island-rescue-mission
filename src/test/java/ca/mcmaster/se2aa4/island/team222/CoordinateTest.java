package ca.mcmaster.se2aa4.island.team222;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.Test;

public class CoordinateTest {
    private Coordinate coordinate;

    @Before
    public void setUp() {
        coordinate = new Coordinate(3, 4);
    }
    @After
    public void tearDown() {
        coordinate = null;
    }

    @Test
    public void testGetX() {
        assertEquals(3, coordinate.getX());
    }

    @Test
    public void testGetY() {
        assertEquals(4, coordinate.getY());
    }

    @Test
    public void testUpdateX() {
        coordinate.updateX(2);
        assertEquals(5, coordinate.getX());
    }

    @Test
    public void testUpdateY() {
        coordinate.updateY(-1);
        assertEquals(3, coordinate.getY());
    }

    @Test
    public void testUpdateXandY() {
        coordinate.updateXandY(2, -1);
        assertEquals(5, coordinate.getX());
        assertEquals(3, coordinate.getY());
    }
    
}
