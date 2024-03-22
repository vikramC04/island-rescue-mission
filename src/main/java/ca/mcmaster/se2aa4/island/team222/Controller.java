package ca.mcmaster.se2aa4.island.team222;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team222.Actions.*;
import ca.mcmaster.se2aa4.island.team222.Directions.CardinalDirection;
import ca.mcmaster.se2aa4.island.team222.Phases.*;
import ca.mcmaster.se2aa4.island.team222.Responses.*;

public class Controller {

    private final Logger logger = LogManager.getLogger();
    
    private Phase currentPhase;
    private ActionType previousAction;
    private POI closestCreek;
    
    public Controller(int batteryLevel, CardinalDirection direction) {
        this.currentPhase = new FindCorner(new Drone(batteryLevel, direction), new AllPOIS(new ArrayList<>()));
    }

    public Action decide() {
        
        //Check if the end of the phase has been reached
        if(currentPhase.reachedEnd()) {
            logger.info("Current phase end.");

            //Terminate if the Drone reaches the end of the final phase
            if(currentPhase.isFinal()) {
                logger.info("Final phase end.");
                AllPOIS creekLocations = currentPhase.getCreeks();
                ClosestCreek findCreek = new ClosestCreek(creekLocations);
                closestCreek = findCreek.findClosestCreek();
                previousAction = ActionType.STOP; 
                return new Action(ActionType.STOP);
            }

            //Get the next phase when the end is reached
            currentPhase = currentPhase.getNextPhase();
        } 

        //Get next decision
        Action nextAction = currentPhase.getNextDecision();

        //Save previous action type
        previousAction = nextAction.getType();

        //Return the next action from the phase
        return nextAction;
    }

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

    public String generateReport(){
        return closestCreek.getID();

    }

}
