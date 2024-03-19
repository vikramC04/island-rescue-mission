package ca.mcmaster.se2aa4.island.team222.Phases;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.mcmaster.se2aa4.island.team222.Drone;
import ca.mcmaster.se2aa4.island.team222.ScanStatus;
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
    private boolean need_to_scan;

    public enum Reset {
        ECHO_RIGHT,
        FLY,
        RIGHT,
        FORWARD,
        SECOND_FORWARD,
        THIRD_FORWARD,
        SECOND_RIGHT,
        FOURTH_FORWARD,
        THIRD_RIGHT,
        FOURTH_RIGHT,
        FIFTH_FORWARD,
        LEFT,
        SECOND_LEFT,
        ECHO_FORWARD,
        FLY_SINGULAR,
        ECHO_LEFT,
    }

    public ResetLeft(Drone drone) {
        logger.info("RESET LEFT BEGINS");
        this.reachedEnd = false;
        this.currentState = Reset.ECHO_RIGHT;
        this.drone = drone;
        this.isFinalPhase = false;
        this.need_to_scan = false;
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
            case FLY:
                nextAction = drone.fly();
                break;
            case RIGHT:
                nextAction = drone.heading(RelativeDirection.RIGHT);
                break;
            case FORWARD:
                nextAction = drone.fly();
                break;
            case SECOND_FORWARD:
                nextAction = drone.fly();
                break;
            case THIRD_FORWARD:
                nextAction = drone.fly();
                break;
            case SECOND_RIGHT:
                nextAction = drone.heading(RelativeDirection.RIGHT);
                break;
            case FOURTH_FORWARD:
                nextAction = drone.fly();
                break;
            case THIRD_RIGHT:
                nextAction = drone.heading(RelativeDirection.RIGHT);
                break;
            case FOURTH_RIGHT:
                nextAction = drone.heading(RelativeDirection.RIGHT);
                break; 
            case FIFTH_FORWARD:
                nextAction = drone.fly();
                break;  
            case LEFT:
                nextAction = drone.heading(RelativeDirection.LEFT);
                break;
            case SECOND_LEFT:
                nextAction = drone.heading(RelativeDirection.LEFT);
                break; 
            case ECHO_FORWARD:
                nextAction = drone.echo(RelativeDirection.FORWARD);
                break; 
            case ECHO_LEFT:
                nextAction = drone.echo(RelativeDirection.LEFT);
                break; 
            case FLY_SINGULAR:
                nextAction = drone.fly();
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
        Map<String, Value> data = response.getData();
        //Updates the current state using the response
        switch(this.currentState) {
            case ECHO_RIGHT:
                String found = data.get("found").getStringValue(); 
                if(found.equals("GROUND")) {
                    this.currentState = Reset.FLY;
                } else {
                    this.currentState = Reset.RIGHT;
                }  
                break;
            case FLY:
                this.currentState = Reset.ECHO_RIGHT;
                break;
            case RIGHT:
                this.currentState = Reset.FORWARD;
                break;
            case FORWARD:
                this.currentState = Reset.SECOND_FORWARD;
                break;
            case SECOND_FORWARD:
                this.currentState = Reset.THIRD_FORWARD;
                break;
            case THIRD_FORWARD:
                this.currentState = Reset.SECOND_RIGHT;
                break;
            case SECOND_RIGHT: 
                this.currentState = Reset.FOURTH_FORWARD;        
                break;
            case FOURTH_FORWARD:
                this.currentState = Reset.THIRD_RIGHT; 
                break;
            case THIRD_RIGHT:
                this.currentState = Reset.FOURTH_RIGHT;
                break;
            case FOURTH_RIGHT:
                this.currentState = Reset.FIFTH_FORWARD;
                break;
            case FIFTH_FORWARD:
                this.currentState = Reset.LEFT;
                break;
            case LEFT:
                this.currentState = Reset.SECOND_LEFT;
                break;
            case SECOND_LEFT:  
                this.currentState = Reset.ECHO_FORWARD;
                break;
            case ECHO_FORWARD:
                String found_forward = data.get("found").getStringValue(); 
                if(found_forward.equals("GROUND")) {
                    logger.info("GROUND IS AHEAD");
                    this.reachedEnd = true;
                    this.need_to_scan = true;
                } else {
                    logger.info("No Need to Scan Line");
                    this.currentState = Reset.FLY_SINGULAR;
                } 
                break; 
            case ECHO_LEFT:
                String found_left = data.get("found").getStringValue(); 
                if(found_left.equals("GROUND")) {
                    this.currentState = Reset.FLY_SINGULAR;
                } else {
                    this.reachedEnd = true;
                } 
                break; 
            case FLY_SINGULAR:
                this.currentState = Reset.ECHO_LEFT;
                break;
            default:
                throw new IllegalStateException("Undefined state: " + this.currentState);
        }
        logger.info("Next State: " + this.currentState);
    }

    @Override
    public Phase getNextPhase() {
        drone.setStatus();
        if(this.need_to_scan) {
            return new ScanLine(this.drone);
        } 
        return new UTurnLeft(this.drone);
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
