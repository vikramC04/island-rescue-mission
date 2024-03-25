package ca.mcmaster.se2aa4.island.team222.phases;

import java.util.Map;

import ca.mcmaster.se2aa4.island.team222.Drone;
import ca.mcmaster.se2aa4.island.team222.ScanStatus;
import ca.mcmaster.se2aa4.island.team222.Value;
import ca.mcmaster.se2aa4.island.team222.actions.*;
import ca.mcmaster.se2aa4.island.team222.directions.*;
import ca.mcmaster.se2aa4.island.team222.pois.AllPOIS;
import ca.mcmaster.se2aa4.island.team222.responses.Response;

public class UTurn implements Phase {

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
        this.drone = drone;
        this.allPOIS = allPOIS;
        this.orientation = orientation;
    }

    @Override
    public Action getNextDecision() {
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
        this.drone.useBattery(response.getCost());
        Map<String, Value> data = response.getData();
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
                    } 
                }
                drone.switchOrientation();
                this.reachedEnd = true;
                break;
            default:
                throw new IllegalStateException("Undefined state: " + this.currentState);
        }
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
