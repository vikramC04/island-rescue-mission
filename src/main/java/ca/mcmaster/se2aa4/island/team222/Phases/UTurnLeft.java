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

public class UTurnLeft implements Phase {

    private final Logger logger = LogManager.getLogger();

    //Phase Variables
    private boolean reachedEnd;
    private UTurnState currentState;
    private Drone drone;
    private boolean isFinalPhase;
    private boolean reset;

    public enum UTurnState {
        LEFT,
        SECOND_LEFT,
        ECHO,
    }

    public UTurnLeft(Drone drone) {
        this.reachedEnd = false;
        this.currentState = UTurnState.LEFT;
        this.drone = drone;
        this.isFinalPhase = false;
        this.reset = false;
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
            case LEFT:
                nextAction = drone.heading(RelativeDirection.LEFT);
                break;
            case SECOND_LEFT:
                nextAction = drone.heading(RelativeDirection.LEFT);
                break;
            case ECHO:
                nextAction = drone.echo(RelativeDirection.FORWARD);
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
            case LEFT: 
                this.currentState = UTurnState.SECOND_LEFT;        
                break;
            case SECOND_LEFT:
                this.currentState = UTurnState.ECHO; 
                break;
            case ECHO:
                String found = data.get("found").getStringValue();
                if(found.equals("OUT_OF_RANGE")) {
                    if(drone.getStatus() == ScanStatus.NONE) {
                        this.reset = true;
                    } else {
                        this.isFinalPhase = true;
                    } 
                }
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
        if(reset) {
            if(drone.getOrientation() == Orientation.LEFT) {
                return new ResetLeft(this.drone);
            }
            return new ResetRight(this.drone);
        } else {
            return new ScanLine(this.drone);
        }
    }

    @Override
    public boolean reachedEnd() {
        return this.reachedEnd;
    }

    @Override
    public boolean isFinal() {
        return this.isFinalPhase;
    }
}

