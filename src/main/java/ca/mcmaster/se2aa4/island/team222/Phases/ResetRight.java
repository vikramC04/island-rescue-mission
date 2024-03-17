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

public class ResetRight implements Phase {

    private final Logger logger = LogManager.getLogger();

    //Phase Variables
    private boolean reachedEnd;
    private Reset currentState;
    private Drone drone;
    private boolean isFinalPhase;

    public enum Reset {
        RIGHT,
        SECOND_RIGHT,
        FORWARD,
        THIRD_RIGHT,
        SECOND_FORWARD,
        THIRD_FORWARD,
        FOURTH_FORWARD,
        FOURTH_RIGHT,
    }

    public ResetRight(Drone drone) {
        logger.info("RESET RIGHT BEGINS");
        this.reachedEnd = false;
        this.currentState = Reset.RIGHT;
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
            case RIGHT:
                nextAction = drone.heading(RelativeDirection.RIGHT);
                break;
            case SECOND_RIGHT:
                nextAction = drone.heading(RelativeDirection.RIGHT);
                break;
            case FORWARD:
                nextAction = drone.fly();
                break;
            case THIRD_RIGHT:
                nextAction = drone.heading(RelativeDirection.RIGHT);
                break;
            case SECOND_FORWARD:
                nextAction = drone.fly();
                break;
            case THIRD_FORWARD:
                nextAction = drone.fly();
                break; 
            case FOURTH_FORWARD:
                nextAction = drone.fly();
                break;       
            case FOURTH_RIGHT:
                nextAction = drone.heading(RelativeDirection.RIGHT);
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
        logger.info(drone.getCoordinates().getX());
        logger.info(drone.getCoordinates().getY());


        //Updates the current state using the response
        switch(this.currentState) {
            case RIGHT:
                this.currentState = Reset.SECOND_RIGHT;
                break;
            case SECOND_RIGHT: 
                this.currentState = Reset.FORWARD;        
                break;
            case FORWARD:
                this.currentState = Reset.THIRD_RIGHT; 
                break;
            case THIRD_RIGHT:
                this.currentState = Reset.SECOND_FORWARD;
                break;
            case SECOND_FORWARD:
                this.currentState = Reset.THIRD_FORWARD;
                break;
            case THIRD_FORWARD:
                this.currentState = Reset.FOURTH_FORWARD;
                break;
            case FOURTH_FORWARD:
                this.currentState = Reset.FOURTH_RIGHT;
                break;
            case FOURTH_RIGHT:
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
