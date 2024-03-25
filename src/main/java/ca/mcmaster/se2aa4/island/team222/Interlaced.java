package ca.mcmaster.se2aa4.island.team222;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team222.actions.*;
import ca.mcmaster.se2aa4.island.team222.directions.CardinalDirection;
import ca.mcmaster.se2aa4.island.team222.phases.*;
import ca.mcmaster.se2aa4.island.team222.responses.*;

public class Interlaced implements Scan {

    private final Logger logger = LogManager.getLogger();
    
    private Phase currentPhase;
    private ActionType previousAction;
    private ClosestCreek creeks;
    private POI emergencySite;
    private Drone drone;
    
    public Interlaced(int batteryLevel, CardinalDirection direction) {
        this.drone = new Drone(batteryLevel, direction);
        this.currentPhase = new FindCorner(drone, new AllPOIS(new ArrayList<>()));
    }

    @Override
    public Action decide() {
        
        //Check if the end of the phase has been reached
        if(currentPhase.reachedEnd() && !currentPhase.isFinal()) {
            logger.info("Current phase end.");
            //Get the next phase when the end is reached
            currentPhase = currentPhase.getNextPhase();
        } else if(drone.getBattery() <= 100 || (currentPhase.reachedEnd() && currentPhase.isFinal())) {
            logger.info("Final phase end.");
            AllPOIS allPOIS = currentPhase.getAllPOIS();
            emergencySite = allPOIS.getEmergencySite();
            creeks = new ClosestCreek(allPOIS);

            logger.info("Number of creeks: " + allPOIS.getCreeks().size());
                    for(int i = 0; i < allPOIS.getCreeks().size(); i++){
                        logger.info(i + " " + allPOIS.getCreeks().get(i).getID());
                        logger.info(i + " " + allPOIS.getCreeks().get(i).getX());
                        logger.info(i + " " + allPOIS.getCreeks().get(i).getY());

                    }

            previousAction = ActionType.STOP; 
            return new Action(ActionType.STOP);
        }

        //Get next decision
        Action nextAction = currentPhase.getNextDecision();

        //Save previous action type
        previousAction = nextAction.getType();

        //Return the next action from the phase
        return nextAction;
    }

    @Override
    public void react(JSONObject responseObj) {
        
        //Use previous action to generate the correct response
        Response response;
        switch(this.previousAction) {
            case FLY:
            case HEADING:
            case STOP:
                response = new NormalResponse(responseObj, previousAction);
                break;
            case ECHO:
                response = new EchoResponse(responseObj);
                break;
            case SCAN:
                response = new ScanResponse(responseObj);
                break;
            default:
                throw new IllegalStateException("Undefined response type: " + this.previousAction);
        }
        
        //React to the response
        if(this.previousAction != ActionType.STOP) {
            logger.info("Reacting to response");
            currentPhase.react(response);
        }
    }

    @Override
    public String generateReport(){
        POI closestCreek = creeks.findClosestCreek();
        logger.info("closest creek: " + closestCreek.getID());
        if(emergencySite.equals(null)){
            return "Closest Creek: " + closestCreek.getID();
        }
        else{
            logger.info("emergency site: " + emergencySite.getID());
            return "Closest Creek: " + closestCreek.getID() + " Emergency Site: " + emergencySite.getID();
        }
    }

}
