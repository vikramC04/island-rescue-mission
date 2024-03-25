package ca.mcmaster.se2aa4.island.team222;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team222.actions.Action;
import ca.mcmaster.se2aa4.island.team222.actions.ActionType;
import ca.mcmaster.se2aa4.island.team222.directions.CardinalDirection;
import ca.mcmaster.se2aa4.island.team222.directions.Orientation;
import ca.mcmaster.se2aa4.island.team222.phases.Phase;
import ca.mcmaster.se2aa4.island.team222.phases.ResetLR;
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

    @BeforeEach
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
                TestAction.testAction(nextAction, CardinalDirection.S, ActionType.HEADING);
                currentState = UTurnLRState.SECOND_TURN;
            } else if (currentState == UTurnLRState.SECOND_TURN) {
                TestAction.testAction(nextAction, CardinalDirection.E, ActionType.HEADING);
                currentState = UTurnLRState.ECHO;
            } else if (currentState == UTurnLRState.ECHO) {
                TestAction.testAction(nextAction, ActionType.ECHO);
            }

            currentPhase.react(defaultResponse);
        }
    }
    public void finalPhaseTest() {
        Phase currentPhase = new UTurn(drone, new AllPOIS(new ArrayList<>()), Orientation.RIGHT);
        Phase currentPhase2 = new UTurn(drone, new AllPOIS(new ArrayList<>()), Orientation.LEFT);
        assertEquals(false,currentPhase.isFinal());
        assertEquals(false,currentPhase2.isFinal());
    } 


    @AfterEach
    public void tearDown() {
        drone = null;
    }
}
