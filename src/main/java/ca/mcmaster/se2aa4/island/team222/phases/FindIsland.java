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
    private AllPOIS creekSpots;

    public enum FindIslandState {
        ECHO_RIGHT,
        FLY_FORWARD,
        TURN_RIGHT;
    }

    public FindIsland(Drone drone, AllPOIS creekSpots) {
        logger.info("FindIsland phase begins.");
        this.reachedEnd = false;
        this.currentState = FindIslandState.ECHO_RIGHT;
        this.drone = drone;
        this.creekSpots = creekSpots;
    }

    @Override
    public Action getNextDecision() {

        //Terminate if Drone Battery <= 100
        if(drone.getBattery() <= 100) {
            return new Action(ActionType.STOP);
        }

        //Get the next action based on the current state and the drone
        Action nextAction;
        logger.info("Current State: " + this.currentState);
        switch(this.currentState) {
            case ECHO_RIGHT:
                nextAction = drone.echo(RelativeDirection.RIGHT);
                break;
            case FLY_FORWARD:
                nextAction = drone.fly();
                break;
            default:
                throw new IllegalStateException("Undefined state: " + this.currentState);
        }
        logger.info("Next Action: " + nextAction.getType());

        return nextAction;
    }

    @Override
    public void react(Response response) {

        //Subtract Battery
        this.drone.useBattery(response.getCost());
        logger.info("Drone new battery: " + this.drone.getBattery());

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
                throw new IllegalStateException("Undefined state: " + this.currentState);
        }
        logger.info("Next State: " + this.currentState);
    }

    @Override
    public Phase getNextPhase() {
        return new TravelToIsland(this.drone, this.creekSpots);
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
    public AllPOIS getCreeks(){
        return creekSpots;
    }
}
