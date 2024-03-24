package ca.mcmaster.se2aa4.island.team222.phases;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.mcmaster.se2aa4.island.team222.AllPOIS;
import ca.mcmaster.se2aa4.island.team222.Drone;
import ca.mcmaster.se2aa4.island.team222.actions.*;
import ca.mcmaster.se2aa4.island.team222.directions.*;
import ca.mcmaster.se2aa4.island.team222.responses.Response;

public class ResetLR implements Phase {

    private final Logger logger = LogManager.getLogger();

    //Phase Variables
    private boolean reachedEnd = false;
    private State currentState = State.TURN;;
    private Drone drone;
    private AllPOIS allPOIS;
    private Orientation droneOrientation;

    public enum State {
        TURN,
        FORWARD,
        SECOND_TURN,
        THIRD_TURN,
        FOURTH_TURN,
        SECOND_FORWARD
    }


    public ResetLR(Drone drone, AllPOIS allPOIS, Orientation droneOrientation) {
        logger.info("Reset Orientation: " + drone.getOrientation());
        logger.info("RESET BEGINS: " + drone.getBattery());
        this.drone = drone;
        this.allPOIS = allPOIS;
        this.droneOrientation = droneOrientation;
    }

    @Override
    public Action getNextDecision() {

        //Get the next action based on the current state and the drone
        Action nextAction;
        switch(this.currentState) {
            case TURN:
                if(droneOrientation == Orientation.LEFT) {
                    nextAction = drone.heading(RelativeDirection.RIGHT);
                } else {
                    nextAction = drone.heading(RelativeDirection.LEFT);
                }
                break;
            case FORWARD:
                nextAction = drone.fly();
                break;
            case SECOND_TURN:
                if(droneOrientation == Orientation.LEFT) {
                    nextAction = drone.heading(RelativeDirection.RIGHT);
                } else {
                    nextAction = drone.heading(RelativeDirection.LEFT);
                }
                break;
            case THIRD_TURN:
                if(droneOrientation == Orientation.LEFT) {
                    nextAction = drone.heading(RelativeDirection.RIGHT);
                } else {
                    nextAction = drone.heading(RelativeDirection.LEFT);
                }
                break;
            case FOURTH_TURN:
                if(droneOrientation == Orientation.LEFT) {
                    nextAction = drone.heading(RelativeDirection.RIGHT);
                } else {
                    nextAction = drone.heading(RelativeDirection.LEFT);
                }
                break;
            case SECOND_FORWARD:
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
        logger.info("Drone new battery: " + this.drone.getBattery());

        logger.info(drone.getCoordinates().getX());
        logger.info(drone.getCoordinates().getY());

        //Updates the current state using the response
        switch(this.currentState) {
            case TURN:
            this.currentState = State.FORWARD;
                break;
            case FORWARD:
                this.currentState = State.SECOND_TURN;
                break;
            case SECOND_TURN:
                this.currentState = State.THIRD_TURN;
                break;
            case THIRD_TURN:
                this.currentState = State.FOURTH_TURN;
                break;
            case FOURTH_TURN:
                this.currentState = State.SECOND_FORWARD;
                break;
            case SECOND_FORWARD:
                this.reachedEnd = true;
                drone.switchOrientation();
                break;
            default:
                throw new IllegalStateException(String.format("Undefined state: %s", this.currentState));

                
        }
        logger.info("Next State: " + this.currentState);
    }

    @Override
    public Phase getNextPhase() {
        drone.setStatus();
        return new ScanLine(this.drone, this.allPOIS); 
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
        return this.allPOIS;
    }
}
