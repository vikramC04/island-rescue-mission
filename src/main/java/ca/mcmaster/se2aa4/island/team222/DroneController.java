package ca.mcmaster.se2aa4.island.team222;

import org.json.JSONObject;
import java.util.Queue;
import java.util.LinkedList;


public class DroneController {
    
    String currentHeading;
    int batteryLevel;
    Queue<JSONObject> moveQueue;
    String previousAction;
    
    public DroneController(String initialHeading, int intialBatteryLevel) {
        
        //Set initial heading and battery level for the drone
        this.currentHeading = initialHeading;
        this.batteryLevel = intialBatteryLevel;

        //Initialize a move queue for the drone
        this.moveQueue = new LinkedList<>();

    }

    //Decides the next moves for the drone
    public JSONObject decide() {
        JSONObject decision = new JSONObject();
        decision.put("action", "stop"); // we stop the exploration immediately
        return decision;
    }

    //Reacts to information returned by the game engine
    public void react(JSONObject response) {
        
    }
}
