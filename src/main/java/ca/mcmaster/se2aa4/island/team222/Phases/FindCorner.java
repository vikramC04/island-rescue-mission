package ca.mcmaster.se2aa4.island.team222.Phases;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.mcmaster.se2aa4.island.team222.Drone;
import ca.mcmaster.se2aa4.island.team222.Value;
import ca.mcmaster.se2aa4.island.team222.Actions.*;
import ca.mcmaster.se2aa4.island.team222.Directions.*;
import ca.mcmaster.se2aa4.island.team222.Responses.Response;

public class FindCorner implements Phase {

    private final Logger logger = LogManager.getLogger();

    //Phase Variables
    private boolean reachedEnd;
    private FindCornerState currentState;
    private Drone drone;

    //State Variables
    private int range;

    public enum FindCornerState {
        CHECKING_LEFT,
        TURNING_LEFT,
        FLYING_FORWARD,
        TURNING_RIGHT
    }

    public FindCorner(Drone drone) {
        logger.info("FindCorner phase begins.");
        this.reachedEnd = false;
        this.currentState = FindCornerState.CHECKING_LEFT;
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
            case CHECKING_LEFT:
                nextAction = drone.echo(RelativeDirection.LEFT);
                break;
            case TURNING_LEFT:
                nextAction = drone.heading(RelativeDirection.LEFT);
                break;
            case FLYING_FORWARD:
                nextAction = drone.fly();
                break;
            case TURNING_RIGHT:
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

        //Get the data from the response
        Map<String, Value> data = response.getData();

        //Updates the current state using the response
        switch(this.currentState) {
            case CHECKING_LEFT:
                this.range = data.get("range").getIntValue();           //Set the distance until the wall
                logger.info("Distance to wall: " + this.range);
                if (this.range > 2) {                                       //Check if the drone is close to the wall                                                           
                    this.currentState = FindCornerState.TURNING_LEFT;       //Turn left if it isnt
                } else {
                    this.reachedEnd = true;                                 //End the phase if it is (drone is in the corner already)
                }
                break;

            case TURNING_LEFT:
                this.range -= 1;                                            //Drone gets 1 unit closer to the wall
                logger.info("Distance to wall: " + this.range);
                this.currentState = FindCornerState.FLYING_FORWARD;         //Fly forward
                break;

            case FLYING_FORWARD:
                if (this.range > 2) {                                       //Check if the drone is close to the wall
                    this.currentState = FindCornerState.FLYING_FORWARD;     //Continue to fly forward
                    this.range -= 1;                                        //Gets 1 unit closer to the wall
                    logger.info("Distance to wall: " + this.range);
                } else {     
                    this.currentState = FindCornerState.TURNING_RIGHT;      //Turn right if it is close
                }
                break;

            case TURNING_RIGHT:
                this.reachedEnd = true;                                     //Drone should be at a corner now
                break;

            default:
                throw new IllegalStateException("Undefined state: " + this.currentState);
        }
        logger.info("Next State: " + this.currentState);
    }

    @Override
    public Phase getNextPhase() {
        return new FindIsland(this.drone);
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
