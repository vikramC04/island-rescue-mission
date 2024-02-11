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
        JSONObject currentAction = new JSONObject();
        if (!moveQueue.isEmpty()) {
            //Take a move from the queue
            currentAction = moveQueue.poll();
        } else {
            //Otherwise scan then add fly to queue
            currentAction.put("action", "echo");
            JSONObject parameters = new JSONObject();
            parameters.put("direction", "S");
            currentAction.put("parameters", parameters);
            JSONObject fly = new JSONObject();
            fly.put("action", "fly");
            moveQueue.offer(fly);
        }
        this.previousAction = currentAction.getString("action");
        return currentAction;
    }

    //Reacts to information returned by the game engine
    public void react(JSONObject response) {
        
    }
}
