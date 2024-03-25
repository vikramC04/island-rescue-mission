package ca.mcmaster.se2aa4.island.team222;

import java.util.ArrayList;

import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team222.actions.*;
import ca.mcmaster.se2aa4.island.team222.directions.CardinalDirection;
import ca.mcmaster.se2aa4.island.team222.phases.*;
import ca.mcmaster.se2aa4.island.team222.pois.AllPOIS;
import ca.mcmaster.se2aa4.island.team222.pois.ClosestCreek;
import ca.mcmaster.se2aa4.island.team222.pois.POI;
import ca.mcmaster.se2aa4.island.team222.responses.*;

public class Interlaced implements Scan {
    
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
        
        if(currentPhase.reachedEnd() && !currentPhase.isFinal()) {
            currentPhase = currentPhase.getNextPhase();
        } else if (drone.getBattery() <= 100 || (currentPhase.reachedEnd() && currentPhase.isFinal())) {
            AllPOIS allPOIS = currentPhase.getAllPOIS();
            emergencySite = allPOIS.getEmergencySite();
            creeks = new ClosestCreek(allPOIS);
            previousAction = ActionType.STOP; 
            return new Action(ActionType.STOP);
        }

        Action nextAction = currentPhase.getNextDecision();

        previousAction = nextAction.getType();

        return nextAction;
    }

    @Override
    public void react(JSONObject responseObj) {
        
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
        
        if(this.previousAction != ActionType.STOP) {
            currentPhase.react(response);
        }
    }

    @Override
    public String generateReport(){
        POI closestCreek = creeks.findClosestCreek();
        if(emergencySite == null){
            return "Closest Creek: " + closestCreek.getID();
        }
        else{
            return "Closest Creek: " + closestCreek.getID() + " Emergency Site: " + emergencySite.getID();
        }
    }

}
