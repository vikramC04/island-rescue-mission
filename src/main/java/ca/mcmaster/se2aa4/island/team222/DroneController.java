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
                currentAction = drone.nextMove();
                if(currentAction.getString("action").equals("echo")) {
                    JSONObject params = currentAction.getJSONObject("parameters");
                    logger.info(params);
                    
                    scan_dir = Direction.valueOf(params.getString("direction"));
                    logger.info("Echo direction: " + scan_dir);
                }


                drone.addMove(moveList.scan());
                drone.addMove(moveList.fly());

            } else {
                if(orientation.equals("")) {
                    drone.addMove(moveList.echo(dir_index.nextLeft()));
                } else {
                    drone.addMove(moveList.echo(dir_index));

                    drone.addMove(moveList.scan());

                    drone.addMove(moveList.fly());
                }
                currentAction = drone.nextMove();
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
                drone.clearMoves(); 
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
                        drone.clearMoves();
                        if(orientation.equals("right")) {
                            logger.info("Turning Right");
                            Direction right_dir = dir_index.nextRight();
                            drone.addMove(moveList.heading(right_dir));
                            drone.addMove(moveList.heading(right_dir.nextRight()));
                            orientation = "left";
                            
                        } else if(orientation.equals("left")) {
                            logger.info("Turning Left");
                            Direction left_dir = dir_index.nextLeft();
                            drone.addMove(moveList.heading(left_dir));
                            drone.addMove(moveList.heading(left_dir.nextLeft()));
                            orientation = "right";
                        }
                        dir_index = dir_index.nextRight().nextRight();
                        rotate = true;
                    }  else if(previousAction.equals("echo") && rotate == true) {
                        logger.info("Checking for island");
                        if(found.equals("OUT_OF_RANGE")) {
                            logger.info("Complete Stop");
                            drone.clearMoves();
                            drone.addMove(moveList.stop());
                        }
                        rotate = false;
                    }
                }
            }   
        }
    }
}
