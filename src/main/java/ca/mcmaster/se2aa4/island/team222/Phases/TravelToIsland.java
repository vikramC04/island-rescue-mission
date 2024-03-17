package ca.mcmaster.se2aa4.island.team222.Phases;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.mcmaster.se2aa4.island.team222.Drone;
import ca.mcmaster.se2aa4.island.team222.Value;
import ca.mcmaster.se2aa4.island.team222.Actions.Action;
import ca.mcmaster.se2aa4.island.team222.Actions.ActionType;
import ca.mcmaster.se2aa4.island.team222.Directions.RelativeDirection;
import ca.mcmaster.se2aa4.island.team222.Responses.Response;

public class TravelToIsland implements Phase {
    
    private final Logger logger = LogManager.getLogger();

    private boolean reachedEnd = false;
    private TravelToIslandState currentState;
    private Drone drone;

    public enum TravelToIslandState {
        TEMP;
    }

    public TravelToIsland(Drone drone) {
        logger.info("FindIsland phase begins.");
        this.reachedEnd = false;
        this.currentState = TravelToIslandState.TEMP;
        this.drone = drone;
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
            case TEMP:
                nextAction = drone.scan();
                break;
            default:
                throw new IllegalStateException("Undefined state: " + this.currentState);
        }
        logger.info("Next Action: " + nextAction.getActionType());

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
            case TEMP:
                break;
            default:
                throw new IllegalStateException("Undefined state: " + this.currentState);
        }
        logger.info("Next State: " + this.currentState);
    }

    @Override
    public Phase getNextPhase() {
        return new TravelToIsland(this.drone);
    }

    @Override
    public boolean reachedEnd() {
        return this.reachedEnd;
    }

    @Override
    public boolean isFinal() {
        return true;
    }
}
