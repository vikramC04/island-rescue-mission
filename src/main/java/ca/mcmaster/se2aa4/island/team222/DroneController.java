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
    private String previousAction;
    private Boolean landFound = false;
    private Boolean isOnPath = false;
    private Boolean atIsland = false;
    private String orientation = "";
    private Boolean rotate = false;
    private Boolean rotated = false;


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
            if(!atIsland) {
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

            } else {
                if(orientation.equals("")) {
                    JSONObject echo = new JSONObject();
                    echo.put("action", "echo");
                    JSONObject parameters = new JSONObject();
                    parameters.put("direction",String.valueOf(dir_index.nextLeft()));
                    echo.put("parameters", parameters);
                    moveQueue.offer(echo);
                } else {
                    JSONObject echo = new JSONObject();
                    echo.put("action", "echo");
                    JSONObject parameters = new JSONObject();
                    parameters.put("direction",String.valueOf(dir_index));
                    echo.put("parameters", parameters);
                    moveQueue.offer(echo);

                    JSONObject scan = new JSONObject();
                    scan.put("action", "scan");
                    moveQueue.offer(scan);

                    JSONObject fly = new JSONObject();
                    fly.put("action", "fly");
                    moveQueue.offer(fly);
                }
                currentAction = moveQueue.poll();
            }
            
            
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
        if(!atIsland) {
            if (previousAction.equals(e)) {

                //When in front of island scan and return to base
                int range = response.getJSONObject("extras").getInt("range");
                String found = response.getJSONObject("extras").getString("found");
                logger.info("Found Island" + found);
                if (!found.equals("OUT_OF_RANGE") && range == 0 && landFound) {
                    logger.info("STOPPING");
                    moveQueue.clear();
                    // JSONObject stop = new JSONObject();
                    
                    // stop.put("action", "stop");
                    // moveQueue.offer(stop);
                    logger.info("Direction: " + String.valueOf(dir_index));
                    //logger.info("Direction: " + moveQueue);
                    atIsland = true;
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

        } else {
            if(orientation.equals("")) {
                String found = response.getJSONObject("extras").getString("found");
                if(found.equals("GROUND")) {
                    orientation = "left";
                } else {
                    orientation = "right";
                }
                logger.info("Orientation: " + orientation);
                
            } else {
                String found = response.getJSONObject("extras").getString("found");
                if(previousAction.equals("echo") && found.equals("OUT_OF_RANGE") && rotate == false) {
                    if(orientation.equals("right")) {
                        Direction right_dir = dir_index.nextRight();
                        JSONObject changeHeading = new JSONObject();
                        changeHeading.put("action", "heading");
                        JSONObject parameters = new JSONObject();
                        parameters.put("direction", String.valueOf(right_dir));
                        changeHeading.put("parameters", parameters);
                        moveQueue.offer(changeHeading);
        
                        JSONObject changeHeadingRight = new JSONObject();
                        changeHeadingRight.put("action", "heading");
                        JSONObject parametersRight = new JSONObject();
                        parametersRight.put("direction", String.valueOf(right_dir.nextRight()));
                        changeHeadingRight.put("parameters", parametersRight);
                        moveQueue.offer(changeHeading);
                        orientation = "left";
                        
                    } else if(orientation.equals("left")) {
                        Direction left_dir = dir_index.nextLeft();
                        JSONObject changeHeading = new JSONObject();
                        changeHeading.put("action", "heading");
                        JSONObject parameters = new JSONObject();
                        parameters.put("direction", String.valueOf(left_dir));
                        changeHeading.put("parameters", parameters);
                        moveQueue.offer(changeHeading);
        
                        JSONObject changeHeadingLeft= new JSONObject();
                        changeHeadingLeft.put("action", "heading");
                        JSONObject parametersLeft= new JSONObject();
                        parametersLeft.put("direction", String.valueOf(left_dir.nextLeft()));
                        changeHeadingLeft.put("parameters", parametersLeft);
                        moveQueue.offer(changeHeading);
                        orientation = "right";
                    }
                    dir_index = dir_index.nextRight().nextRight();
                    rotate = true;
                } else if(previousAction.equals("echo") && rotate == true) {
                    rotate = false;
                    rotated = true;
                } else if(previousAction.equals("echo") && rotated == true) {
                    if(found.equals("OUT_OF_RANGE")) {
                        JSONObject stop = new JSONObject();
                        stop.put("action", "stop");
                        moveQueue.offer(stop);
                    }
                    rotated = false;
                }
            }   
        }
    }
}
