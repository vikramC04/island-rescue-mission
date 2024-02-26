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

    String previousAction;
    Boolean landFound = false;
    Boolean isOnPath = false;

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
            currentAction = drone.nextMove();
            
            //Get direction index
            if(currentAction.getString("action").equals("echo")) {
                JSONObject params = currentAction.getJSONObject("parameters");
                logger.info(params);
                
                //String scan_dir = params.getString("direction");
                scan_dir = Direction.valueOf(params.getString("direction"));
                logger.info("Echo Direction " + scan_dir);
            }
        } else {
            //Otherwise echo in all directions, scan, and then fly
            echoAll();

            //Get direcion index
            currentAction = drone.nextMove();
            if(currentAction.getString("action").equals("echo")) {
                JSONObject params = currentAction.getJSONObject("parameters");
                logger.info(params);
                
                scan_dir = Direction.valueOf(params.getString("direction"));
                logger.info("Echo direction: " + scan_dir);
            }

            drone.addMove(moveList.scan());
            drone.addMove(moveList.fly());
            
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
        if (previousAction.equals(e)) {

            //When in front of island scan and return to base
            int range = response.getJSONObject("extras").getInt("range");
            String found = response.getJSONObject("extras").getString("found");
            logger.info("Found " + found);
            if (!found.equals("OUT_OF_RANGE") && range == 0 && landFound) {
                logger.info("STOPPING");
                drone.addMove(moveList.scan());
                drone.addMove(moveList.stop());
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
        
    }
}
