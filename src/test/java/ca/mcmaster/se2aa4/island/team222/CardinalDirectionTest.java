package ca.mcmaster.se2aa4.island.team222;

import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team222.directions.CardinalDirection;

import static org.junit.jupiter.api.Assertions.*;




public class CardinalDirectionTest {
    @Test
    public void testNextRight() {
        assertEquals(CardinalDirection.N, CardinalDirection.W.nextRight());
        assertEquals(CardinalDirection.E, CardinalDirection.N.nextRight());
        assertEquals(CardinalDirection.S, CardinalDirection.E.nextRight());
        assertEquals(CardinalDirection.W, CardinalDirection.S.nextRight());
        
    }

    @Test
    public void testNextLeft() {
        assertEquals(CardinalDirection.N, CardinalDirection.E.nextLeft());
        assertEquals(CardinalDirection.E, CardinalDirection.S.nextLeft());
        assertEquals(CardinalDirection.S, CardinalDirection.W.nextLeft());
        assertEquals(CardinalDirection.W, CardinalDirection.N.nextLeft());
    }


}
