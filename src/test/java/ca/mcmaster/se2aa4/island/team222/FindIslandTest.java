package ca.mcmaster.se2aa4.island.team222;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team222.actions.Action;
import ca.mcmaster.se2aa4.island.team222.actions.ActionType;
import ca.mcmaster.se2aa4.island.team222.directions.CardinalDirection;
import ca.mcmaster.se2aa4.island.team222.phases.FindIsland;
import ca.mcmaster.se2aa4.island.team222.phases.Phase;
import ca.mcmaster.se2aa4.island.team222.responses.Response;
import ca.mcmaster.se2aa4.island.team222.pois.AllPOIS;

public class FindIslandTest {
    private Drone drone;
    private Phase currentPhase;

    public enum State {
        ECHO_RIGHT,
        FLY_FORWARD,
        ECHO_RIGHT_SECOND;
    }
    @BeforeEach
    public void setUp() {
        drone = new Drone(15000, CardinalDirection.W);
        currentPhase = new FindIsland(drone, new AllPOIS(new ArrayList<>()));
    }

    @AfterEach
    public void tearDown() {
        drone = null;
        currentPhase = null;
    }

    @Test
    public void DroneStartsInLine() {
        State currentState = State.ECHO_RIGHT;
        Response echoR = CreateResponse.createEcho(5, "OK", "GROUND", 8);
        while(!currentPhase.reachedEnd()) {
            Action nextAction = currentPhase.getNextDecision();
            if(currentState == State.ECHO_RIGHT) {
                TestAction.testAction(nextAction, CardinalDirection.N, ActionType.ECHO);
            } 
            currentPhase.react(echoR);
        }
    }

    @Test
    public void DroneStartsNotInLine() {
        State currentState = State.ECHO_RIGHT;
        Response anyResponse = CreateResponse.createEcho(3, "OK", "OUT_OF_RANGE", 15);
        while(!currentPhase.reachedEnd()) {
            Action nextAction = currentPhase.getNextDecision();
            if(currentState == State.ECHO_RIGHT) {
                TestAction.testAction(nextAction, CardinalDirection.N, ActionType.ECHO);
                currentState = State.FLY_FORWARD;
            } else if(currentState == State.FLY_FORWARD) {
                TestAction.testAction(nextAction, ActionType.FLY);
                currentState = State.ECHO_RIGHT_SECOND;
                anyResponse = CreateResponse.createNormal(3, "OK");
            } else if(currentState == State.ECHO_RIGHT_SECOND) {
                TestAction.testAction(nextAction,CardinalDirection.N, ActionType.ECHO);
                anyResponse = CreateResponse.createEcho(3, "OK", "GROUND", 3);
            } 

            currentPhase.react(anyResponse);
        }
    }
    @Test
    public void finalPhaseTest() {
        assertEquals(false,currentPhase.isFinal());
    }
    
}
