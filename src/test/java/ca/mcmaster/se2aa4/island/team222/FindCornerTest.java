package ca.mcmaster.se2aa4.island.team222;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team222.actions.Action;
import ca.mcmaster.se2aa4.island.team222.actions.ActionType;
import ca.mcmaster.se2aa4.island.team222.directions.CardinalDirection;
import ca.mcmaster.se2aa4.island.team222.phases.FindCorner;
import ca.mcmaster.se2aa4.island.team222.phases.Phase;
import ca.mcmaster.se2aa4.island.team222.responses.Response;


public class FindCornerTest {
    
    private Drone drone;
    private Phase currentPhase;
    public enum State {
        CHECKING_LEFT,
        TURNING_LEFT,
        FLYING_FORWARD,
        TURNING_RIGHT
    }
    @BeforeEach
    public void setUp() {
        drone = new Drone(15000, CardinalDirection.E);
        currentPhase = new FindCorner(drone, new AllPOIS(new ArrayList<>()));
    }

    @AfterEach
    public void tearDown() {
        drone = null;
        currentPhase = null;
    }

    @Test
    public void DroneStartsAtCorner() {
        State currentState = State.CHECKING_LEFT;
        Response echoR = CreateResponse.createEcho(3, "OK", "OUT_OF_RANGE", 0);
        while(!currentPhase.reachedEnd()) {
            Action nextAction = currentPhase.getNextDecision();
            if(currentState == State.CHECKING_LEFT) {
                TestAction.testAction(nextAction, CardinalDirection.N, ActionType.ECHO);
            } 

            currentPhase.react(echoR);
        }
    }

    @Test
    public void DroneStartsAtMiddle() {
        State currentState = State.CHECKING_LEFT;
        Response anyResponse = CreateResponse.createEcho(6, "OK", "OUT_OF_RANGE", 3);
        while(!currentPhase.reachedEnd()) {
            Action nextAction = currentPhase.getNextDecision();
            if(currentState == State.CHECKING_LEFT) {
                TestAction.testAction(nextAction, CardinalDirection.N, ActionType.ECHO);
                currentState = State.TURNING_LEFT;
            } else if(currentState == State.TURNING_LEFT) {
                TestAction.testAction(nextAction, CardinalDirection.N, ActionType.HEADING);
                currentState = State.FLYING_FORWARD;
                anyResponse = CreateResponse.createNormal(5, "OK");
            } else if (currentState == State.FLYING_FORWARD) {
                TestAction.testAction(nextAction, ActionType.FLY);
                anyResponse = CreateResponse.createNormal(15, "OK");
                currentState = State.TURNING_RIGHT;
            } else if (currentState == State.TURNING_RIGHT) {
                TestAction.testAction(nextAction, CardinalDirection.E, ActionType.HEADING);
                anyResponse = CreateResponse.createNormal(15, "OK");
            }

            currentPhase.react(anyResponse);
        }
    }
    @Test
    public void finalPhaseTest() {
        assertEquals(false,currentPhase.isFinal());
    } 
}
