package ca.mcmaster.se2aa4.island.team222.phases;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.mcmaster.se2aa4.island.team222.AllPOIS;
import ca.mcmaster.se2aa4.island.team222.Drone;
import ca.mcmaster.se2aa4.island.team222.Value;
import ca.mcmaster.se2aa4.island.team222.actions.*;
import ca.mcmaster.se2aa4.island.team222.directions.RelativeDirection;
import ca.mcmaster.se2aa4.island.team222.responses.Response;

public class FindIsland implements Phase {
    
    private final Logger logger = LogManager.getLogger();

    private boolean reachedEnd = false;
    private FindIslandState currentState;
    private Drone drone;
    private AllPOIS allPOIS;

    public enum FindIslandState {
        ECHO_RIGHT,
        FLY_FORWARD,
    }

    public FindIsland(Drone drone, AllPOIS allPOIS) {
        logger.info("FindIsland phase begins.");
        this.currentState = FindIslandState.ECHO_RIGHT;
        this.drone = drone;
        this.allPOIS = allPOIS;
    }

    @Override
    public Action getNextDecision() {

        //Get the next action based on the current state and the drone
        Action nextAction;
        switch(this.currentState) {
            case ECHO_RIGHT:
                nextAction = drone.echo(RelativeDirection.RIGHT);
                break;
            case FLY_FORWARD:
                nextAction = drone.fly();
                break;
            default:
                throw new IllegalStateException(String.format("Undefined state: %s", this.currentState));

        }
        return nextAction;
    }

    @Override
    public void react(Response response) {

        //Subtract Battery
        this.drone.useBattery(response.getCost());

        
        //Get the data from the response
        Map<String, Value> data = response.getData();

        //Updates the current state using the response
        switch(this.currentState) {
            case ECHO_RIGHT:
                String landFound = data.get("found").getStringValue();
                if(landFound.equals("OUT_OF_RANGE")) {
                    this.currentState = FindIslandState.FLY_FORWARD;
                } else {
                    this.reachedEnd = true;
                }
                break;
            case FLY_FORWARD:
                this.currentState = FindIslandState.ECHO_RIGHT;
                break;
            default:
                throw new IllegalStateException(String.format("Undefined state: %s", this.currentState));
        }
    }

    @Override
    public Phase getNextPhase() {
        return new TravelToIsland(this.drone, this.allPOIS);
    }

    @Override
    public boolean reachedEnd() {
        return this.reachedEnd;
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public AllPOIS getAllPOIS(){
        return allPOIS;
    }
}
