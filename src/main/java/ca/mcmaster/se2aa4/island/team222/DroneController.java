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
    private String[] direction;
    private int dir_index;

    private String scan_dir;

    Queue<JSONObject> moveQueue;
    String previousAction;
    Boolean landFound = false;



    public DroneController(Drone drone) {
        //Initialize a move queue for the drone

        this.drone = drone;
        this.moveQueue = new LinkedList<>();
        direction = new String[]{"N", "E", "S", "W"};
        //determine which direction to go at the start
        for(int i=0; i < direction.length; i++) {
            if(direction[i].equals(drone.getDirection())) {
                this.dir_index = i;
                if(dir_index == 0)  {
                    scan_dir = direction[3];
                } else {
                    scan_dir = direction[dir_index-1]; 
                }
                break;
            }
        }
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

                scan_dir = params.getString("direction");
                logger.info("Echo direction: " + scan_dir);
            }
        } else {
            //Otherwise echo, scan and fly
            echoAll();
            currentAction = moveQueue.poll();
            if(currentAction.getString("action").equals("echo")) {
                JSONObject params = currentAction.getJSONObject("parameters");
                logger.info(params);

                scan_dir = params.getString("direction");
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
        for(int i=-1; i < 2; i++) {
            JSONObject currentAction = new JSONObject();
            currentAction.put("action", "echo");
            JSONObject parameters = new JSONObject();
            if(dir_index + i < 0) {
                parameters.put("direction", direction[3]);
            } else {
                parameters.put("direction", direction[(dir_index + i) % 4]);
            }
            currentAction.put("parameters", parameters);
            moveQueue.offer(currentAction);
        }
    }

    //Reacts to information returned by the game engine
    public void react(JSONObject response) {
        //Update battery level
        int cost = response.getInt("cost");
        drone.batteryLevel -= cost;
        logger.info(drone.batteryLevel);
        logger.info("Previous: " + previousAction);
        String e = "echo";
        if (previousAction.equals(e)) {

            //When in front of island scan and return to base
            int range = response.getJSONObject("extras").getInt("range");
            String found = response.getJSONObject("extras").getString("found");
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
                if(!scan_dir.equals(direction[dir_index])) {
                    logger.info("Found Ground in Direction: " + scan_dir);
                    JSONObject scan = new JSONObject();
                    scan.put("action", "scan");
                    moveQueue.offer(scan);
                    JSONObject changeHeading = new JSONObject();
                    changeHeading.put("action", "heading");
                    JSONObject parameters = new JSONObject();
                    parameters.put("direction", scan_dir);
                    changeHeading.put("parameters", parameters);
                    moveQueue.offer(changeHeading);

                    for(int i=0; i < direction.length; i++) {
                        if(direction[i].equals(scan_dir)) {
                            this.dir_index = i;
                            break;
                        }
                    }
                    logger.info("direction is changed to: " + dir_index);

                    }
                logger.info("Land is found");
                landFound = true;
            }

        }
    }
}

