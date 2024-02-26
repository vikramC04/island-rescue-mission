package ca.mcmaster.se2aa4.island.team222;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.Map;
import java.util.Queue;
import java.util.LinkedList;


public class DroneController {

    private Drone drone;
    private DroneMoveList moveList;
    
    private final Logger logger = LogManager.getLogger();

    private Direction dir_index;
    private Direction scan_dir;


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
        this.moveList = new DroneMoveList();

        //determine which direction to go at the start
         //Initialize a move queue for the drone
 
         dir_index = drone.getDirection();
         scan_dir = dir_index.nextLeft();
 
         logger.info("INITIAL DIRECITON INDEX: " + dir_index);


        dir_index = drone.getDirection();
        scan_dir = dir_index.nextLeft();

        logger.info("INITIAL DIRECITON INDEX: " + dir_index);
    }



    //Decides the next moves for the drone
    public JSONObject decide() {
        JSONObject currentAction = new JSONObject();

        if (drone.hasNextMove()) {
            logger.info("Picking move");
            currentAction = drone.nextMove();
            
            //Get direction index
            if(currentAction.getString("action").equals("echo")) {
                JSONObject params = currentAction.getJSONObject("parameters");
                logger.info(params);
                
                //String scan_dir = params.getString("direction");
                scan_dir = Direction.valueOf(params.getString("direction"));
                logger.info("Echo Direction " + scan_dir);

               // logger.info("Echo direction: " + scan_dir);
            } 
            logger.info("Direction: " + String.valueOf(dir_index));
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

    private void echoAll() {

        if(isOnPath){
            drone.addMove(moveList.echo(dir_index));
        }
        else{
            
        for(int i = 0; i < 3; i++){
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
            drone.addMove(moveList.echo(dir_index));
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

    if(!atIsland){
        if (previousAction.equals(e)) {

            //When in front of island scan and return to base
            int range = response.getJSONObject("extras").getInt("range");
            String found = response.getJSONObject("extras").getString("found");
            logger.info("Found " + found);
            if (!found.equals("OUT_OF_RANGE") && range == 0 && landFound) {
                logger.info("STOPPING");
                drone.clearMoves() // add later
                drone.addMove(moveList.scan());
                drone.addMove(moveList.stop());
                atIsland = true;
            }


            //Change heading when the island is found
            if (found.equals("GROUND") && !landFound) {
                if(!scan_dir.equals(dir_index)) {
                    logger.info("Found Ground in Direction: " + String.valueOf(scan_dir));

                    drone.addMove(moveList.scan());
                    drone.addMove(moveList.heading(scan_dir));

                    dir_index = scan_dir;
                    isOnPath = true;

                    logger.info("direction is changed to: " + String.valueOf(dir_index));
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
                logger.info("Checking for Turning Requirements");
                if(previousAction.equals("echo")) {
                    logger.info("Previous echo");
                    String found = response.getJSONObject("extras").getString("found");
                    if(previousAction.equals("echo") && found.equals("OUT_OF_RANGE") && rotate == false) {
                        logger.info("Needs to Turn");
                        moveQueue.clear();
                        if(orientation.equals("right")) {
                            logger.info("Turning Right");
                            Direction right_dir = dir_index.nextRight();
                            JSONObject changeHeadingR = new JSONObject();
                            changeHeadingR.put("action", "heading");
                            JSONObject parameters = new JSONObject();
                            parameters.put("direction", String.valueOf(right_dir));
                            changeHeadingR.put("parameters", parameters);
                            moveQueue.offer(changeHeadingR);
            
                            JSONObject changeHeadingRight = new JSONObject();
                            changeHeadingRight.put("action", "heading");
                            JSONObject parametersRight = new JSONObject();
                            parametersRight.put("direction", String.valueOf(right_dir.nextRight()));
                            changeHeadingRight.put("parameters", parametersRight);
                            moveQueue.offer(changeHeadingRight);
                            orientation = "left";
                            
                        } else if(orientation.equals("left")) {
                            logger.info("Turning Left");
                            Direction left_dir = dir_index.nextLeft();
                            JSONObject changeHeadingL = new JSONObject();
                            changeHeadingL.put("action", "heading");
                            JSONObject parameters = new JSONObject();
                            parameters.put("direction", String.valueOf(left_dir));
                            changeHeadingL.put("parameters", parameters);
                            moveQueue.offer(changeHeadingL);
            
                            JSONObject changeHeadingLeft= new JSONObject();
                            changeHeadingLeft.put("action", "heading");
                            JSONObject parametersLeft= new JSONObject();
                            parametersLeft.put("direction", String.valueOf(left_dir.nextLeft()));
                            changeHeadingLeft.put("parameters", parametersLeft);
                            moveQueue.offer(changeHeadingLeft);
                            orientation = "right";
                        }
                        dir_index = dir_index.nextRight().nextRight();
                        rotate = true;
                    }  else if(previousAction.equals("echo") && rotate == true) {
                        logger.info("Checking for island");
                        if(found.equals("OUT_OF_RANGE")) {
                            logger.info("Complete Stop");
                            moveQueue.clear();
                            JSONObject stop = new JSONObject();
                            stop.put("action", "stop");
                            moveQueue.offer(stop);
                        }
                        rotate = false;
                    }
                }
            }   
        }
    }
}
