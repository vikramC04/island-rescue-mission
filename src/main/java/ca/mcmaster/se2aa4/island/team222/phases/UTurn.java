package ca.mcmaster.se2aa4.island.team222.phases;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.mcmaster.se2aa4.island.team222.AllPOIS;
import ca.mcmaster.se2aa4.island.team222.ClosestCreek;
import ca.mcmaster.se2aa4.island.team222.Drone;
import ca.mcmaster.se2aa4.island.team222.ScanStatus;
import ca.mcmaster.se2aa4.island.team222.Value;
import ca.mcmaster.se2aa4.island.team222.actions.*;
import ca.mcmaster.se2aa4.island.team222.directions.*;
import ca.mcmaster.se2aa4.island.team222.responses.Response;

public class UTurn implements Phase {

    private final Logger logger = LogManager.getLogger();

    //Phase Variables
    private boolean reachedEnd = false;
    private UTurnLR currentState = UTurnLR.TURN;
    private Drone drone;
    private boolean isFinalPhase = false;
    private boolean reset = false;
    private AllPOIS allPOIS;
    private Orientation orientation;

    public enum UTurnLR {
        TURN,
        SECOND_TURN,
        ECHO,
    }

    public UTurn(Drone drone, AllPOIS allPOIS, Orientation orientation) {
        logger.info("Find corner phase begins.");
        this.drone = drone;
        this.allPOIS = allPOIS;
        this.orientation = orientation;
    }

    @Override
    public Action getNextDecision() {

        //Get the next action based on the current state and the drone
        Action nextAction;
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
                        logger.info("Emergency Site: " + allPOIS.getEmergencySite().getID());
                        logger.info(allPOIS.getEmergencySite().getX());
                        logger.info(allPOIS.getEmergencySite().getY());
                        logger.info("Number of creeks: " + allPOIS.getCreeks().size());
                        for(int i = 0; i < allPOIS.getCreeks().size(); i++){
                            logger.info(i + " " + allPOIS.getCreeks().get(i).getID());
                            logger.info(i + " " + allPOIS.getCreeks().get(i).getX());
                            logger.info(i + " " + allPOIS.getCreeks().get(i).getY());

                        }
                        ClosestCreek closestCreek = new ClosestCreek(allPOIS);
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
            return new ResetLR(this.drone, this.allPOIS, drone.getOrientation());
        } else {
            return new ScanLine(this.drone, this.allPOIS);
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
    public AllPOIS getAllPOIS(){
        return this.allPOIS;
    }
}
