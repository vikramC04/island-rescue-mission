package ca.mcmaster.se2aa4.island.team222;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team222.actions.Action;
import ca.mcmaster.se2aa4.island.team222.actions.ActionType;
import ca.mcmaster.se2aa4.island.team222.directions.CardinalDirection;
import ca.mcmaster.se2aa4.island.team222.phases.Phase;
import ca.mcmaster.se2aa4.island.team222.phases.TravelToIsland;
import ca.mcmaster.se2aa4.island.team222.responses.Response;
import ca.mcmaster.se2aa4.island.team222.pois.AllPOIS;

public class TravelToIslandTest {
    private Drone drone;
    private Phase currentPhase;
    public enum State {
        TURN_TO_ISLAND,
        ECHOING,
        FLYING
    }
    @BeforeEach
    public void setUp() {
        drone = new Drone(15000, CardinalDirection.N);
        currentPhase = new TravelToIsland(drone, new AllPOIS(new ArrayList<>()));
    }

    @AfterEach
    public void tearDown() {
        drone = null;
        currentPhase = null;
    }

    @Test
    public void traveller() {
        State currentState = State.TURN_TO_ISLAND;
        Response anyResponse = CreateResponse.createNormal(3, "OK");
        while(!currentPhase.reachedEnd()) {
            Action nextAction = currentPhase.getNextDecision();
            if(currentState == State.TURN_TO_ISLAND) {
                currentState = State.ECHOING;
                TestAction.testAction(nextAction, CardinalDirection.E, ActionType.HEADING);
            } else if(currentState == State.ECHOING) {
                TestAction.testAction(nextAction, CardinalDirection.E, ActionType.ECHO);
                currentState = State.FLYING;
                anyResponse = CreateResponse.createEcho(3, "OK", "GROUND", 2);
            } else if(currentState == State.FLYING) {
                TestAction.testAction(nextAction,ActionType.FLY);
                anyResponse = CreateResponse.createNormal(3, "OK");
            }

            currentPhase.react(anyResponse);
        }
    }
}
