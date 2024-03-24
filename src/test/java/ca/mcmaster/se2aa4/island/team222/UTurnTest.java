package ca.mcmaster.se2aa4.island.team222;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import ca.mcmaster.se2aa4.island.team222.directions.*;
import ca.mcmaster.se2aa4.island.team222.phases.UTurn;
import ca.mcmaster.se2aa4.island.team222.responses.EchoResponse;
import ca.mcmaster.se2aa4.island.team222.actions.Action;
import ca.mcmaster.se2aa4.island.team222.actions.ActionType;

public class UTurnTest {
    
    private Drone drone;
    private AllPOIS allPOIS;
    private UTurn uTurn;

    @Before
    public void setUp() {
        drone = new Drone(1000, CardinalDirection.E);
        allPOIS = new AllPOIS(null); // Initialize with null since not used in these tests
    }

    @Test
    public void testNextStateTurnRight() {
        uTurn = new UTurn(drone, allPOIS, Orientation.RIGHT);
        Action nextDecision = uTurn.getNextDecision();
        assertEquals(ActionType.HEADING, nextDecision.getType());
        assertEquals(CardinalDirection.S, nextDecision.getDirection());
    }

    @Test
    public void testNextStateTurnLeft() {
        uTurn = new UTurn(drone, allPOIS, Orientation.LEFT);
        Action nextDecision = uTurn.getNextDecision();
        assertEquals(ActionType.HEADING, nextDecision.getType());
        assertEquals(CardinalDirection.N, nextDecision.getDirection());
    }
}

    
