package ca.mcmaster.se2aa4.island.team222.phases;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.mcmaster.se2aa4.island.team222.Drone;
import ca.mcmaster.se2aa4.island.team222.Value;
import ca.mcmaster.se2aa4.island.team222.actions.*;
import ca.mcmaster.se2aa4.island.team222.directions.RelativeDirection;
import ca.mcmaster.se2aa4.island.team222.pois.AllPOIS;
import ca.mcmaster.se2aa4.island.team222.responses.Response;

public class TravelToIsland implements Phase {
    
    private final Logger logger = LogManager.getLogger();

    private boolean reachedEnd = false;
    private MoveToIsland currentState  = MoveToIsland.TURN_TO_ISLAND;
    private Drone drone;
    private AllPOIS allPOIS;
    private int groundRange;

    public enum MoveToIsland {
        TURN_TO_ISLAND,
        ECHOING,
        FLYING
    }

    public TravelToIsland(Drone drone, AllPOIS allPOIS) {
        logger.info("Move To Island phase begins.");
        this.drone = drone;
        this.allPOIS = allPOIS;
    }

    @Override
    public Action getNextDecision() {

        //Get the next action based on the current state and the drone
        Action nextAction;
        logger.info("Current State: " + this.currentState);
        switch(this.currentState) {
            case TURN_TO_ISLAND:
                nextAction = drone.heading(RelativeDirection.RIGHT);
                break;
            case ECHOING:
                nextAction = drone.echo(RelativeDirection.FORWARD);
                break;
            case FLYING:
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
        //Get the data from the response
        Map<String, Value> data = response.getData();

        //Updates the current state using the response
        switch(this.currentState) {
            case TURN_TO_ISLAND:
                this.currentState = MoveToIsland.ECHOING;
                break;
            case ECHOING:
                String found = data.get("found").getStringValue();
                int range = data.get("range").getIntValue();
                if(found.equals("GROUND") && range == 0) {
                    this.reachedEnd = true;
                } else {
                    this.currentState = MoveToIsland.FLYING;
                }
                this.groundRange= range;   
                break;
            case FLYING:
                this.groundRange -= 1;
                if(this.groundRange < 2) {
                    this.reachedEnd = true;
                }
                this.currentState = MoveToIsland.FLYING;        
                break;
            default:
                throw new IllegalStateException(String.format("Undefined state: %s", this.currentState));

        }
    }

    @Override
    public Phase getNextPhase() {
        logger.info("SCANNING LINE");
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