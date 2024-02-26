package ca.mcmaster.se2aa4.island.team222;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.rolling.DirectFileRolloverStrategy;
import org.json.JSONObject;

public class Drone {

    private int batteryLevel;
    private Direction currentDirection;
    private Queue<JSONObject> moveQueue;

    public Drone(String currentDirection, int batteryLevel){
        
        //Set current direction and battery level
        this.currentDirection = Direction.valueOf(currentDirection);
        this.batteryLevel = batteryLevel;

        //Initialize a move queue of the drone
        this.moveQueue = new LinkedList<>();

    }

    public Direction getDirection(){
        return this.currentDirection;
    }

    public int getBattery(){
        return this.batteryLevel;
    } 

    public void updateBatteryLevel(int cost){
        this.batteryLevel = this.batteryLevel - cost;
    }

    public void updateDirection(Direction move){
        this.currentDirection = move;
    }

    public JSONObject nextMove() {
        return moveQueue.poll();
    }

    public void addMove(JSONObject move) {
        this.moveQueue.offer(move);
    }

    public Boolean hasNextMove() {
        return !moveQueue.isEmpty();
    }

    public void clearMoves(){
        this.moveQueue.clear();
    }

}
