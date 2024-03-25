package ca.mcmaster.se2aa4.island.team222.phases;

import java.util.Map;

import ca.mcmaster.se2aa4.island.team222.Drone;
import ca.mcmaster.se2aa4.island.team222.Value;
import ca.mcmaster.se2aa4.island.team222.actions.*;
import ca.mcmaster.se2aa4.island.team222.directions.*;
import ca.mcmaster.se2aa4.island.team222.pois.AllPOIS;
import ca.mcmaster.se2aa4.island.team222.responses.Response;

public class FindCorner implements Phase {

    private boolean reachedEnd = false;
    private FindCornerState currentState = FindCornerState.CHECKING_LEFT;
    private Drone drone;
    private AllPOIS allPOIS;
    private int range;

    public enum FindCornerState {
        CHECKING_LEFT,
        TURNING_LEFT,
        FLYING_FORWARD,
        TURNING_RIGHT;
    }

    public FindCorner(Drone drone, AllPOIS allPOIS) {
        this.drone = drone;
        this.allPOIS = allPOIS;
    }

    @Override
    public Action getNextDecision() {
        Action nextAction;
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
                throw new IllegalStateException(String.format("Undefined state: %s", this.currentState));
        }
        return nextAction;
    }

    @Override
    public void react(Response response) {
        this.drone.useBattery(response.getCost());
        Map<String, Value> data = response.getData();
        switch(this.currentState) {
            case CHECKING_LEFT:
                this.range = data.get("range").getIntValue();
                if (this.range > 2) {                                                         
                    this.currentState = FindCornerState.TURNING_LEFT;
                } else {
                    this.reachedEnd = true;
                }
                break;
            case TURNING_LEFT:
                this.range -= 1;
                this.currentState = FindCornerState.FLYING_FORWARD;
                break;
            case FLYING_FORWARD:
                if (this.range > 2) {
                    this.currentState = FindCornerState.FLYING_FORWARD;
                    this.range -= 1;
                } else {     
                    this.currentState = FindCornerState.TURNING_RIGHT;
                }
                break;
            case TURNING_RIGHT:
                this.reachedEnd = true;
                break;
            default:
                throw new IllegalStateException(String.format("Undefined state: %s", this.currentState));
        }
    }

    @Override
    public Phase getNextPhase() {
        return new FindIsland(this.drone, this.allPOIS);
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
