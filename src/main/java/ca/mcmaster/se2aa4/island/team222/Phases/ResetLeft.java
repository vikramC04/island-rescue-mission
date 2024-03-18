package ca.mcmaster.se2aa4.island.team222.Phases;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.mcmaster.se2aa4.island.team222.Drone;
import ca.mcmaster.se2aa4.island.team222.Value;
import ca.mcmaster.se2aa4.island.team222.Actions.*;
import ca.mcmaster.se2aa4.island.team222.Directions.*;
import ca.mcmaster.se2aa4.island.team222.Phases.FindCorner.FindCornerState;
import ca.mcmaster.se2aa4.island.team222.Phases.ScanLine.ScanLineState;
import ca.mcmaster.se2aa4.island.team222.Phases.TravelToIsland.MoveToIsland;
import ca.mcmaster.se2aa4.island.team222.Responses.Response;

public class ResetLeft implements Phase {

    private final Logger logger = LogManager.getLogger();

    //Phase Variables
    private boolean reachedEnd;
    private Reset currentState;
    private Drone drone;
    private boolean isFinalPhase;

    public enum Reset {
        FORWARD,
        RIGHT,
        SECOND_RIGHT,
        THIRD_RIGHT,
        LEFT,
        SECOND_LEFT,
        SECOND_FORWARD,
        THIRD_LEFT,
    }

    public ResetLeft(Drone drone) {
        logger.info("RESET LEFT BEGINS");
        this.reachedEnd = false;
        this.currentState = Reset.FORWARD;
        this.drone = drone;
        this.isFinalPhase = false;
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
            case FORWARD:
                nextAction = drone.fly();
                break;
            case RIGHT:
                nextAction = drone.heading(RelativeDirection.RIGHT);
                break;
            case SECOND_RIGHT:
                nextAction = drone.heading(RelativeDirection.RIGHT);
                break;
            case THIRD_RIGHT:
                nextAction = drone.heading(RelativeDirection.RIGHT);
                break;
            case LEFT:
                nextAction = drone.heading(RelativeDirection.LEFT);
                break;
            case SECOND_LEFT:
                nextAction = drone.heading(RelativeDirection.LEFT);
                break; 
            case SECOND_FORWARD:
                nextAction = drone.fly();
                break;  
            case THIRD_LEFT:
                nextAction = drone.heading(RelativeDirection.LEFT);
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

        //Updates the current state using the response
        switch(this.currentState) {
            case FORWARD:
                this.currentState = Reset.RIGHT;
                break;
            case RIGHT: 
                this.currentState = Reset.SECOND_RIGHT;        
                break;
            case SECOND_RIGHT:
                this.currentState = Reset.THIRD_RIGHT; 
                break;
            case THIRD_RIGHT:
                this.currentState = Reset.LEFT;
                break;
            case LEFT:
                this.currentState = Reset.SECOND_LEFT;
                break;
            case SECOND_LEFT:
                this.currentState = Reset.SECOND_FORWARD;
                break;
            case SECOND_FORWARD:
                this.currentState = Reset.THIRD_LEFT;
                break;
            case THIRD_LEFT:  
                drone.setStatus();
                drone.switchOrientation();
                this.reachedEnd = true;
                break;
            default:
                throw new IllegalStateException("Undefined state: " + this.currentState);
        }
        logger.info("Next State: " + this.currentState);
    }

    @Override
    public Phase getNextPhase() {
        return new ScanLine(this.drone);
    }

    @Override
    public boolean reachedEnd() {
        return this.reachedEnd;
    }

    @Override
    public boolean isFinal() {
        return false;
    }
}
