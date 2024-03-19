package ca.mcmaster.se2aa4.island.team222.Phases;

import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.mcmaster.se2aa4.island.team222.Drone;
import ca.mcmaster.se2aa4.island.team222.Value;
import ca.mcmaster.se2aa4.island.team222.Actions.*;
import ca.mcmaster.se2aa4.island.team222.Directions.*;
import ca.mcmaster.se2aa4.island.team222.Responses.Response;

public class ScanLine implements Phase {

    private final Logger logger = LogManager.getLogger();

    //Phase Variables
    private boolean reachedEnd;
    private ScanLineState currentState;
    private Drone drone;

    public enum ScanLineState {
        SCAN,
        ECHO,
        ECHO_NEIGHBOUR,
        FLY,
        FLY_SINGULAR
    }

    public ScanLine(Drone drone) {
        logger.info("ScanLine phase begins.");
        this.reachedEnd = false;
        this.currentState = ScanLineState.SCAN;
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
            case SCAN:
                nextAction = drone.scan();
                break;
            case ECHO:
                nextAction = drone.echo(RelativeDirection.FORWARD);
                break;
            case ECHO_NEIGHBOUR:
                if(drone.getOrientation() == Orientation.LEFT) {
                    nextAction = drone.echo(RelativeDirection.LEFT);
                } else {
                    nextAction = drone.echo(RelativeDirection.RIGHT);
                }
                break;
            case FLY:
                nextAction = drone.fly();
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

        //Get the data from the response
        Map<String, Value> data = response.getData();

        //Updates the current state using the response
        switch(this.currentState) {
            case SCAN:
                List<String> biomes = data.get("biomes").getArrayValue();
                if(!biomes.contains("OCEAN")) {
                    logger.info("On Ground");
                    this.currentState = ScanLineState.FLY;
                } else {
                    this.currentState = ScanLineState.ECHO;
                }
                break;
            case ECHO:     
                String found = data.get("found").getStringValue();                             
                if(found.equals("OUT_OF_RANGE")) {
                    this.currentState = ScanLineState.ECHO_NEIGHBOUR;  
                } else {
                    this.currentState = ScanLineState.FLY;
                }   
                break;
            case ECHO_NEIGHBOUR: 
                String found_land= data.get("found").getStringValue();                                 
                if(found_land.equals("OUT_OF_RANGE")) {
                    this.reachedEnd = true;
                } else {
                    this.currentState = ScanLineState.FLY_SINGULAR;
                } 
                break;    
            case FLY:
                this.currentState = ScanLineState.SCAN;
                break;
            case FLY_SINGULAR:
                this.currentState = ScanLineState.ECHO_NEIGHBOUR;
                break;
            default:
                throw new IllegalStateException("Undefined state: " + this.currentState);
        }
        logger.info("Next State: " + this.currentState);
    }

    @Override
    public Phase getNextPhase() {
        if(drone.getOrientation() == Orientation.LEFT) {
            return new UTurnLeft(this.drone);
        }
        return new UTurnRight(this.drone);
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
