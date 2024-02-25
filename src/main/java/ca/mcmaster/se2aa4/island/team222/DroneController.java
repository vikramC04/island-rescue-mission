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
    private Direction[] direction;
    private int dirIndex;

    private Direction scanDir;

    String previousAction;
    Boolean landFound = false;



    public DroneController(Drone drone) {

        this.drone = drone;
        this.moveList = new DroneMoveList();

        //determine which direction to go at the start
        direction = new Direction[]{Direction.N, Direction.E, Direction.S, Direction.W};
        for(int i=0; i < direction.length; i++) {
            if(direction[i].equals(drone.getDirection())) {
                this.dirIndex = i;
                if(dirIndex == 0)  {
                    scanDir = direction[3];
                } else {
                    scanDir = direction[dirIndex-1]; 
                }
                break;
            }
        }
        logger.info("INITIAL DIRECITON INDEX: " + String.valueOf(dirIndex));

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
                scanDir = Direction.valueOf(params.getString("direction"));
                logger.info("INITIAL DIRECITON INDEX: " + String.valueOf(dirIndex));
            }
        } else {
            //Otherwise echo in all directions, scan, and then fly
            echoAll();

            //Get direcion index
            currentAction = drone.nextMove();
            if(currentAction.getString("action").equals("echo")) {
                JSONObject params = currentAction.getJSONObject("parameters");
                logger.info(params);
                scanDir = Direction.valueOf(params.getString("direction"));
                logger.info("INITIAL DIRECITON INDEX: " + String.valueOf(dirIndex));
            }

            drone.addMove(moveList.scan());
            drone.addMove(moveList.fly());
            
        }

        this.previousAction = currentAction.getString("action");
        return currentAction;
    }

    private void echoAll() {
        for(int i = -1; i < 2; i++) {
            if(dirIndex + i < 0) {
                drone.addMove(moveList.echo(direction[3]));
            } else {
                drone.addMove(moveList.echo(direction[(dirIndex + i) % 4]));
            }
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
            if (!found.equals("OUT_OF_RANGE") && range == 0 && landFound) {
                logger.info("STOPPING");
                drone.addMove(moveList.scan());
                drone.addMove(moveList.stop());
            }


            //Change heading when the island is found
            if (found.equals("GROUND") && !landFound) {
                if(!scanDir.equals(direction[dirIndex])) {
                    logger.info("Found Ground in Direction: " + String.valueOf(scanDir));

                    drone.addMove(moveList.scan());
                    drone.addMove(moveList.heading(scanDir));

                    for(int i=0; i < direction.length; i++) {
                        if(direction[i].equals(scanDir)) {
                            this.dirIndex = i;
                            break;
                        }
                    }

                    logger.info("direction is changed to: " + String.valueOf(direction[dirIndex]));
                }
                
                logger.info("Land is found");
                landFound = true;
            }

        }
    }
}

