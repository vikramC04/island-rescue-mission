package ca.mcmaster.se2aa4.island.team222;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.rolling.DirectFileRolloverStrategy;

public class Drone {

    private int batteryLevel;
    private Direction currentDirection;

    public Drone(String currentDirection, int batteryLevel){
        
        switch(currentDirection){
            case("N"):
                this.currentDirection = Direction.N;
                break;
            case("E"):
                this.currentDirection = Direction.E;
                break;
            case("S"):
                this.currentDirection = Direction.S;
                break;
            case("W"):
                this.currentDirection = Direction.W;
                break;
        }
        
        this.batteryLevel = batteryLevel;
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

}
