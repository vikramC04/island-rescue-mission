package ca.mcmaster.se2aa4.island.team222;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team222.actions.Action;
import ca.mcmaster.se2aa4.island.team222.actions.ActionType;
import ca.mcmaster.se2aa4.island.team222.directions.CardinalDirection;
import ca.mcmaster.se2aa4.island.team222.phases.FindIsland;
import ca.mcmaster.se2aa4.island.team222.phases.Phase;
import ca.mcmaster.se2aa4.island.team222.phases.ScanLine;
import ca.mcmaster.se2aa4.island.team222.responses.Response;

public class ScanLineTest {
    private Drone drone;
    private Phase currentPhase;

    public enum State {
        ECHO,
        SCAN,
        ECHO_NEIGHBOUR,
        ECHO_NEIGHBOUR_SECOND,
        FLY,
        FLY_SINGULAR,
    }
    @BeforeEach
    public void setUp() {
        drone = new Drone(15000, CardinalDirection.S);
        currentPhase = new ScanLine(drone, new AllPOIS(new ArrayList<>()));
    }

    @AfterEach
    public void tearDown() {
        drone = null;
        currentPhase = null;
    }

    @Test
    public void scanTest() {
        State currentState = State.ECHO;
        Response anyResponse = CreateResponse.createEcho(8, "OK", "OUT_OF_RANGE", 20);
        while(!currentPhase.reachedEnd()) {
            Action nextAction = currentPhase.getNextDecision();
            if(currentState == State.ECHO) {
                TestAction.testAction(nextAction, CardinalDirection.S, ActionType.ECHO);
                currentState = State.FLY;
            } else if(currentState == State.FLY) {
                TestAction.testAction(nextAction, ActionType.FLY);
                currentState = State.SCAN;
                anyResponse = CreateResponse.createNormal(3, "OK");
            } else if(currentState == State.SCAN) {
                TestAction.testAction(nextAction, ActionType.SCAN);
                List<String> creeks = new ArrayList<>(Arrays.asList("dddc", "eeef"));
                List<String> biomes = new ArrayList<>(Arrays.asList("Shrubland"));
                List<String> sites = new ArrayList<>(Arrays.asList("bababa123"));
                anyResponse = CreateResponse.createScan(3, "OK", creeks, biomes, sites);
                currentState = State.ECHO_NEIGHBOUR;
            } else if(currentState == State.ECHO_NEIGHBOUR) {
                TestAction.testAction(nextAction,CardinalDirection.E, ActionType.ECHO);
                anyResponse = CreateResponse.createEcho(3, "OK", "GROUND", 1);
                currentState = State.FLY_SINGULAR;
            } else if(currentState == State.FLY_SINGULAR) {
                TestAction.testAction(nextAction, ActionType.FLY);
                anyResponse = CreateResponse.createNormal(3, "OK");
                currentState = State.ECHO_NEIGHBOUR_SECOND;
            } else if(currentState == State.ECHO_NEIGHBOUR_SECOND) {
                TestAction.testAction(nextAction,CardinalDirection.E, ActionType.ECHO);
                anyResponse = CreateResponse.createEcho(3, "OK", "OUT_OF_RANGE", 3);
            }

            currentPhase.react(anyResponse);
        }
    }   
}
