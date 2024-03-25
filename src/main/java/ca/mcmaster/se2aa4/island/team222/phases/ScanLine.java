package ca.mcmaster.se2aa4.island.team222.phases;

import java.util.List;
import java.util.Map;

import ca.mcmaster.se2aa4.island.team222.Drone;
import ca.mcmaster.se2aa4.island.team222.Value;
import ca.mcmaster.se2aa4.island.team222.actions.*;
import ca.mcmaster.se2aa4.island.team222.directions.*;
import ca.mcmaster.se2aa4.island.team222.pois.AllPOIS;
import ca.mcmaster.se2aa4.island.team222.pois.POI;
import ca.mcmaster.se2aa4.island.team222.pois.POIType;
import ca.mcmaster.se2aa4.island.team222.responses.Response;

public class ScanLine implements Phase {

    private boolean reachedEnd = false;
    private ScanLineState currentState = ScanLineState.ECHO;
    private Drone drone;
    private AllPOIS allPOIS;
    private int groundRange;
    private boolean finalScan = false;

    public enum ScanLineState {
        ECHO,
        SCAN,
        ECHO_NEIGHBOUR,
        FLY,
        FLY_SINGULAR,
    }

    public ScanLine(Drone drone, AllPOIS allPOIS) {
        this.allPOIS = allPOIS;
        this.drone = drone;
    }

    @Override
    public Action getNextDecision() {
        Action nextAction;
        switch(this.currentState) {
            case ECHO:
                nextAction = drone.echo(RelativeDirection.FORWARD);
                break;
            case SCAN:
                nextAction = drone.scan();
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
                throw new IllegalStateException(String.format("Undefined state: %s", this.currentState));
        }
        return nextAction;
    }

    @Override
    public void react(Response response) {
        this.drone.useBattery(response.getCost());
        Map<String, Value> data = response.getData();
        switch(this.currentState) {
            case ECHO:     
                String found = data.get("found").getStringValue();  
                this.groundRange = data.get("range").getIntValue();                          
                if(found.equals("OUT_OF_RANGE")) {
                    finalScan = true;
                    this.currentState = ScanLineState.FLY;  
                } 
                this.currentState = ScanLineState.FLY;   
                break;
            case SCAN:
                List<String> biomes = data.get("biomes").getArrayValue();
                if(finalScan) {
                    this.currentState = ScanLineState.ECHO_NEIGHBOUR;
                } else if(!biomes.contains("OCEAN")) {
                    this.currentState = ScanLineState.FLY;
                } else {
                    this.currentState = ScanLineState.ECHO;
                }
                List<String> creeks = data.get("creeks").getArrayValue();
                List<String> sites = data.get("sites").getArrayValue();
                if(!creeks.isEmpty()){
                    POI newCreek = new POI(drone.getCoordinates(), creeks.get(0), POIType.CREEKS);
                    allPOIS.addPoi(newCreek, POIType.CREEKS);
                }
                if(!sites.isEmpty()){
                    POI emergencySite = new POI(drone.getCoordinates(), sites.get(0), POIType.SITES);
                    allPOIS.addPoi(emergencySite, POIType.SITES);
                }
                break;
            case ECHO_NEIGHBOUR:   
                String foundLand= data.get("found").getStringValue();
                int range = data.get("range").getIntValue();                                  
                if(foundLand.equals("OUT_OF_RANGE") || range > 3)  {
                    this.reachedEnd = true;
                } else {
                    this.currentState = ScanLineState.FLY_SINGULAR;
                } 
                break;    
            case FLY:
                this.groundRange -= 1;
                if(finalScan) {
                    this.currentState = ScanLineState.SCAN;
                } else if(this.groundRange > 1) {
                    this.currentState = ScanLineState.FLY;
                } else {
                    this.currentState = ScanLineState.SCAN;
                }
                break;
            case FLY_SINGULAR:
                this.currentState = ScanLineState.ECHO_NEIGHBOUR;
                break;
            default:
                throw new IllegalStateException(String.format("Undefined state: %s", this.currentState));

        }
    }

    @Override
    public Phase getNextPhase() {
        return new UTurn(this.drone, this.allPOIS,drone.getOrientation());
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
