package ca.mcmaster.se2aa4.island.team222;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.Map;
import java.util.Queue;
import java.util.LinkedList;


public class DroneController {

    private Drone drone;
    
    private final Logger logger = LogManager.getLogger();

    private Direction dir_index;
    private Direction scan_dir;

    Queue<JSONObject> moveQueue;
    String previousAction;
    Boolean landFound = false;
    Boolean isOnPath = false;


    public DroneController(Drone drone) {

        //Initialize a move queue for the drone
        this.drone = drone;
        this.moveQueue = new LinkedList<>();

        dir_index = drone.getDirection();
        scan_dir = dir_index.nextLeft();

        logger.info("INITIAL DIRECITON INDEX: " + dir_index);
    }



    //Decides the next moves for the drone
    public JSONObject decide() {
        JSONObject currentAction = new JSONObject();
        if (!moveQueue.isEmpty()) {
            //Take a move from the queue
            currentAction = moveQueue.poll();   
            if(currentAction.getString("action").equals("echo")) {
                JSONObject params = currentAction.getJSONObject("parameters");
                logger.info(params);
                
                //String scan_dir = params.getString("direction");
                scan_dir = Direction.valueOf(params.getString("direction"));
                logger.info("Echo Direction " + scan_dir);
               // logger.info("Echo direction: " + scan_dir);
            }
        } else {
            //Otherwise echo, scan and fly
            echoAll();
            currentAction = moveQueue.poll();
            if(currentAction.getString("action").equals("echo")) {
                JSONObject params = currentAction.getJSONObject("parameters");
                logger.info(params);
                
                scan_dir = Direction.valueOf(params.getString("direction"));
                logger.info("Echo direction: " + scan_dir);
            }

            JSONObject scan = new JSONObject();
            scan.put("action", "scan");
            moveQueue.offer(scan);

            JSONObject fly = new JSONObject();
            fly.put("action", "fly");
            moveQueue.offer(fly);
            
        }
        this.previousAction = currentAction.getString("action");
        return currentAction;
    }

    public void echoAll() {
        // if on path to island only echo infront
        if(isOnPath){

            JSONObject currentAction = new JSONObject();
            currentAction.put("action", "echo");
            JSONObject parameters = new JSONObject();
            logger.info(dir_index);
            parameters.put("direction",String.valueOf(dir_index));
            currentAction.put("parameters", parameters);
            moveQueue.offer(currentAction);

        }
        else{
            for(int i = 0; i < 3; i++){
                JSONObject currentAction = new JSONObject();
                currentAction.put("action", "echo");
                JSONObject parameters = new JSONObject();
                // Echo to the Left 
                if(i == 1){
                    dir_index = dir_index.nextLeft();
                }
                //Echo to the Right
                if(i == 2){
                    dir_index = dir_index.nextRight();
                    dir_index = dir_index.nextRight();
                }
                logger.info(dir_index);
                parameters.put("direction",String.valueOf(dir_index));
                currentAction.put("parameters", parameters);
                moveQueue.offer(currentAction);
            }
            // Reset to current direction
            dir_index = dir_index.nextLeft();

        }

        
    }

    //Reacts to information returned by the game engine
    public void react(JSONObject response) {
        //Update battery level
        int cost = response.getInt("cost");
        drone.updateBatteryLevel(cost); 
        logger.info(drone.getBattery());
        logger.info("Previous: " + previousAction);
        String e = "echo";
        if (previousAction.equals(e)) {

            //When in front of island scan and return to base
            int range = response.getJSONObject("extras").getInt("range");
            String found = response.getJSONObject("extras").getString("found");
            logger.info("Found " + found);
            if (!found.equals("OUT_OF_RANGE") && range == 0 && landFound) {
                logger.info("STOPPING");
                JSONObject scan = new JSONObject();
                scan.put("action", "scan");
                moveQueue.offer(scan);
                JSONObject stop = new JSONObject();
                stop.put("action", "stop");
                moveQueue.offer(stop);
            }


            //Change heading when the island is found

            if (found.equals("GROUND") && !landFound) {
                if(!scan_dir.equals(dir_index)) {
                    logger.info("Found Ground in Direction: " + scan_dir);
                    JSONObject scan = new JSONObject();
                    scan.put("action", "scan");
                    moveQueue.offer(scan);
                    JSONObject changeHeading = new JSONObject();
                    changeHeading.put("action", "heading");
                    JSONObject parameters = new JSONObject();
                    parameters.put("direction", String.valueOf(scan_dir));
                    changeHeading.put("parameters", parameters);
                    moveQueue.offer(changeHeading);
                    
                    dir_index = scan_dir;
                    logger.info("direction is changed to: " + dir_index);
                    isOnPath = true;

                    }
                logger.info("Land is found");
                landFound = true;
            }

        }
        
    }
}
