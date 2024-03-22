package ca.mcmaster.se2aa4.island.team222.Phases;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.mcmaster.se2aa4.island.team222.AllPOIS;
import ca.mcmaster.se2aa4.island.team222.ClosestCreek;
import ca.mcmaster.se2aa4.island.team222.Drone;
import ca.mcmaster.se2aa4.island.team222.ScanStatus;
import ca.mcmaster.se2aa4.island.team222.Value;
import ca.mcmaster.se2aa4.island.team222.Actions.*;
import ca.mcmaster.se2aa4.island.team222.Directions.*;
import ca.mcmaster.se2aa4.island.team222.Phases.FindCorner.FindCornerState;
import ca.mcmaster.se2aa4.island.team222.Phases.ScanLine.ScanLineState;
import ca.mcmaster.se2aa4.island.team222.Phases.TravelToIsland.MoveToIsland;
import ca.mcmaster.se2aa4.island.team222.Responses.Response;

public class UTurn implements Phase {

    private final Logger logger = LogManager.getLogger();

    //Phase Variables
    private boolean reachedEnd;
    private UTurnLR currentState;
    private Drone drone;
    private boolean isFinalPhase;
    private boolean reset;
    private AllPOIS creekSpots;
    private Orientation orientation;

    public enum UTurnLR {
        TURN,
        SECOND_TURN,
        ECHO,
    }

    public UTurn(Drone drone, AllPOIS creekSpots, Orientation orientation) {
        logger.info("Find corner phase begins.");
        this.reachedEnd = false;
        this.currentState = UTurnLR.TURN;
        this.drone = drone;
        this.isFinalPhase = false;
        this.reset = false;
        this.creekSpots = creekSpots;
        this.orientation = orientation;
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
            case TURN:
                if(orientation == Orientation.RIGHT) {
                    nextAction = drone.heading(RelativeDirection.RIGHT);
                } else {
                    nextAction = drone.heading(RelativeDirection.LEFT);
                }
                break;
            case SECOND_TURN:
                if(orientation == Orientation.RIGHT) {
                    nextAction = drone.heading(RelativeDirection.RIGHT);
                } else {
                    nextAction = drone.heading(RelativeDirection.LEFT);
                }
                break;
            case ECHO:
                nextAction = drone.echo(RelativeDirection.FORWARD);
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
        logger.info(drone.getCoordinates().getX());
        logger.info(drone.getCoordinates().getY());


        //Get the data from the response
        Map<String, Value> data = response.getData();

        //Updates the current state using the response
        switch(this.currentState) {
            case TURN: 
                this.currentState = UTurnLR.SECOND_TURN;        
                break;
            case SECOND_TURN:
                this.currentState = UTurnLR.ECHO; 
                break;
            case ECHO:
                String found = data.get("found").getStringValue();
                if(found.equals("OUT_OF_RANGE")) {
                    if(drone.getStatus() == ScanStatus.NONE) {
                        this.reset = true;
                    } else {
                        this.isFinalPhase = true;
                        logger.info("Emergency Site: " + creekSpots.getEmergencySite().getID());
                        logger.info(creekSpots.getEmergencySite().getX());
                        logger.info(creekSpots.getEmergencySite().getY());
                        logger.info("Number of creeks: " + creekSpots.getCreeks().size());
                        for(int i = 0; i < creekSpots.getCreeks().size(); i++){
                            logger.info(i + " " + creekSpots.getCreeks().get(i).getID());
                            logger.info(i + " " + creekSpots.getCreeks().get(i).getX());
                            logger.info(i + " " + creekSpots.getCreeks().get(i).getY());

                        }
                        ClosestCreek closestCreek = new ClosestCreek(creekSpots);
                        logger.info("Closest Creek: " + closestCreek.findClosestCreek().getID());
                        logger.info(closestCreek.findClosestCreek().getX());
                        logger.info(closestCreek.findClosestCreek().getY());
                        
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
            return new ResetLR(this.drone, this.creekSpots, drone.getOrientation());
        } else {
            return new ScanLine(this.drone, this.creekSpots);
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

    @Override
    public AllPOIS getCreeks(){
        return creekSpots;
    }
}
