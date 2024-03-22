package ca.mcmaster.se2aa4.island.team222.Phases;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.mcmaster.se2aa4.island.team222.AllPOIS;
import ca.mcmaster.se2aa4.island.team222.Drone;
import ca.mcmaster.se2aa4.island.team222.ScanStatus;
import ca.mcmaster.se2aa4.island.team222.Value;
import ca.mcmaster.se2aa4.island.team222.Actions.*;
import ca.mcmaster.se2aa4.island.team222.Directions.*;
import ca.mcmaster.se2aa4.island.team222.Phases.FindCorner.FindCornerState;
import ca.mcmaster.se2aa4.island.team222.Phases.ScanLine.ScanLineState;
import ca.mcmaster.se2aa4.island.team222.Phases.TravelToIsland.MoveToIsland;
import ca.mcmaster.se2aa4.island.team222.Responses.Response;

public class ResetLR implements Phase {

    private final Logger logger = LogManager.getLogger();

    //Phase Variables
    private boolean reachedEnd;
    private Reset currentState;
    private Drone drone;
    private boolean isFinalPhase;
    private boolean need_to_scan;
    private AllPOIS creekSpots;
    private Orientation droneOrientation;


    public enum Reset {
        ECHO_LEFT,
        FLY,
        LEFT,
        FORWARD,
        SECOND_FORWARD,
        THIRD_FORWARD,
        SECOND_LEFT,
        FOURTH_FORWARD,
        THIRD_LEFT,
        FOURTH_LEFT,
        FIFTH_FORWARD,
        RIGHT,
        SECOND_RIGHT,
        ECHO_FORWARD,
        FLY_SINGULAR,
        ECHO_RIGHT,
    }


    public ResetLR(Drone drone, AllPOIS creekSpots, Orientation droneOrientation) {
        logger.info("RESET RIGHT BEGINS");
        this.reachedEnd = false;
        this.currentState = Reset.ECHO_LEFT;
        this.drone = drone;
        this.isFinalPhase = false;
        this.need_to_scan = false;
        this.creekSpots = creekSpots;
        this.droneOrientation = droneOrientation;
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
            case ECHO_LEFT:
                if(droneOrientation == Orientation.RIGHT) {
                    nextAction = drone.echo(RelativeDirection.LEFT);
                } else {
                    nextAction = drone.echo(RelativeDirection.RIGHT);
                }
                break;
            case FLY:
                nextAction = drone.fly();
                break;
            case LEFT:
                if(droneOrientation == Orientation.RIGHT) {
                    nextAction = drone.heading(RelativeDirection.LEFT);
                } else {
                    nextAction = drone.heading(RelativeDirection.RIGHT);
                }
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
            case SECOND_LEFT:
                if(droneOrientation == Orientation.RIGHT) {
                    nextAction = drone.heading(RelativeDirection.LEFT);
                } else {
                    nextAction = drone.heading(RelativeDirection.RIGHT);
                }
                break;
            case FOURTH_FORWARD:
                nextAction = drone.fly();
                break;
            case THIRD_LEFT:
                if(droneOrientation == Orientation.RIGHT) {
                    nextAction = drone.heading(RelativeDirection.LEFT);
                } else {
                    nextAction = drone.heading(RelativeDirection.RIGHT);
                }
                break;
            case FOURTH_LEFT:
                if(droneOrientation == Orientation.RIGHT) {
                    nextAction = drone.heading(RelativeDirection.LEFT);
                } else {
                    nextAction = drone.heading(RelativeDirection.RIGHT);
                }
                break; 
            case FIFTH_FORWARD:
                nextAction = drone.fly();
                break;  
            case RIGHT:
                if(droneOrientation == Orientation.RIGHT) {
                    nextAction = drone.heading(RelativeDirection.RIGHT);
                } else {
                    nextAction = drone.heading(RelativeDirection.LEFT);
                }
                break;
            case SECOND_RIGHT:
                if(droneOrientation == Orientation.RIGHT) {
                    nextAction = drone.heading(RelativeDirection.RIGHT);
                } else {
                    nextAction = drone.heading(RelativeDirection.LEFT);
                }
                break; 
            case ECHO_FORWARD:
                nextAction = drone.echo(RelativeDirection.FORWARD);
                break; 
            case ECHO_RIGHT:
                if(droneOrientation == Orientation.RIGHT) {
                    nextAction = drone.echo(RelativeDirection.RIGHT);
                } else {
                    nextAction = drone.echo(RelativeDirection.LEFT);
                }
                break; 
            case FLY_SINGULAR:
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
        Map<String, Value> data = response.getData();
        logger.info(drone.getCoordinates().getX());
        logger.info(drone.getCoordinates().getY());

        //Updates the current state using the response
        switch(this.currentState) {
            case ECHO_LEFT:
                String found = data.get("found").getStringValue(); 
                if(found.equals("GROUND")) {
                    this.currentState = Reset.FLY;
                } else {
                    this.currentState = Reset.LEFT;
                }  
                break;
            case FLY:
                this.currentState = Reset.ECHO_LEFT;
                break;
            case LEFT:
                this.currentState = Reset.FORWARD;
                break;
            case FORWARD:
                this.currentState = Reset.SECOND_FORWARD;
                break;
            case SECOND_FORWARD:
                this.currentState = Reset.THIRD_FORWARD;
                break;
            case THIRD_FORWARD:
                this.currentState = Reset.SECOND_LEFT;
                break;
            case SECOND_LEFT: 
                this.currentState = Reset.FOURTH_FORWARD;        
                break;
            case FOURTH_FORWARD:
                this.currentState = Reset.THIRD_LEFT; 
                break;
            case THIRD_LEFT:
                this.currentState = Reset.FOURTH_LEFT;
                break;
            case FOURTH_LEFT:
                this.currentState = Reset.FIFTH_FORWARD;
                break;
            case FIFTH_FORWARD:
                this.currentState = Reset.RIGHT;
                break;
            case RIGHT:
                this.currentState = Reset.SECOND_RIGHT;
                break;
            case SECOND_RIGHT:  
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
            case ECHO_RIGHT:
                String found_left = data.get("found").getStringValue(); 
                if(found_left.equals("GROUND")) {
                    this.currentState = Reset.FLY_SINGULAR;
                } else {
                    this.reachedEnd = true;
                } 
                break; 
            case FLY_SINGULAR:
                this.currentState = Reset.ECHO_RIGHT;
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
            return new ScanLine(this.drone, this.creekSpots);
        } 
        return new UTurn(this.drone, this.creekSpots, Orientation.RIGHT);
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
