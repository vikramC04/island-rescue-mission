package ca.mcmaster.se2aa4.island.team222;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team222.actions.Action;
import ca.mcmaster.se2aa4.island.team222.actions.ActionType;
import ca.mcmaster.se2aa4.island.team222.directions.CardinalDirection;
import ca.mcmaster.se2aa4.island.team222.directions.Orientation;
import ca.mcmaster.se2aa4.island.team222.phases.UTurn;
import ca.mcmaster.se2aa4.island.team222.responses.EchoResponse;
import ca.mcmaster.se2aa4.island.team222.responses.Response;

public class UTurnTest {
    private Drone drone;
    private Response defaultResponse;
    
    public enum UTurnLRState {
        TURN,
        SECOND_TURN,
        ECHO
    }

    @Before
    public void setUp() {
        drone = new Drone(15000, CardinalDirection.W);
        JSONObject responseJson = new JSONObject();
        responseJson.put("cost", 10);
        responseJson.put("status", "OK");
        JSONObject extrasJson = new JSONObject();
        extrasJson.put("found", "something");
        extrasJson.put("range", 5);
        responseJson.put("extras", extrasJson);

        defaultResponse = new EchoResponse(responseJson);
    }

    @Test
    public void testReact() {
       
        UTurn currentPhase = new UTurn(drone, new AllPOIS(new ArrayList<>()), Orientation.LEFT);
        UTurnLRState currentState = UTurnLRState.TURN;
        
        while (!currentPhase.reachedEnd()) {
        
            Action nextAction = currentPhase.getNextDecision();
            
            // Perform action based on current state
            if (currentState == UTurnLRState.TURN) {
                testAction(nextAction, CardinalDirection.S, ActionType.HEADING);
                currentState = UTurnLRState.SECOND_TURN;
            } else if (currentState == UTurnLRState.SECOND_TURN) {
                testAction(nextAction, CardinalDirection.E, ActionType.HEADING);
                currentState = UTurnLRState.ECHO;
            } else if (currentState == UTurnLRState.ECHO) {
                testAction(nextAction, ActionType.ECHO);
            }

            currentPhase.react(defaultResponse);
        }
    }

    private void testAction(Action action, CardinalDirection dir, ActionType type) {
        assertEquals(dir, action.getDirection());
        assertEquals(type, action.getType());
    }

    private void testAction(Action action, ActionType type) {
        assertEquals(type, action.getType());
    }

    @After
    public void tearDown() {
        drone = null;
    }
}
