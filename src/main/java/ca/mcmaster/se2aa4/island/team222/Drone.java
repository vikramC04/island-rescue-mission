package ca.mcmaster.se2aa4.island.team222;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Drone {

    private String currentDirection;
    public int batteryLevel;

    public Drone(String currentDirection, int batteryLevel){
        this.currentDirection = currentDirection;
        this.batteryLevel = batteryLevel;
    }

    public String getDirection(){
        return this.currentDirection;
    }

    public int getBattery(){
        return this.batteryLevel;
    } 

    public void updateBatteryLevel(int cost){
        this.batteryLevel -= cost;
    }

    public void updateDirection(String move){
        this.currentDirection = move;
    }



}
