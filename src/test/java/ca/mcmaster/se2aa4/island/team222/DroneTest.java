package ca.mcmaster.se2aa4.island.team222;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team222.actions.*;
import ca.mcmaster.se2aa4.island.team222.directions.*;

public class DroneTest {
    
    @Test
    public void testDroneInitialization() {
        for (CardinalDirection direction : CardinalDirection.values()) {
            Drone drone = new Drone(100, direction);
            assertEquals(100, drone.getBattery());
            assertEquals(direction, drone.getDirection());
            assertEquals(ScanStatus.NONE, drone.getStatus());
            assertEquals(Orientation.LEFT, drone.getOrientation());
            Coordinate newCoordinate = new Coordinate(0,0);
            Coordinate droneCoordinate = drone.getCoordinates();
            assertEquals(newCoordinate.getX(), droneCoordinate.getX());
            assertEquals(newCoordinate.getY(), droneCoordinate.getY());
        }  
    }

    @Test
    public void testFly() {
        for (CardinalDirection direction : CardinalDirection.values()) {
            Drone drone = new Drone(100, direction);
            Action action = drone.fly();
            assertEquals(ActionType.FLY, action.getType());
            Coordinate coordinate;
            switch(direction) {
                case N:
                    coordinate = new Coordinate(0, 1);
                    break;
                case E:
                    coordinate = new Coordinate(1, 0);
                    break;
                case S:
                    coordinate = new Coordinate(0, -1);
                    break;
                case W:
                    coordinate = new Coordinate(-1, 0);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + direction);
            }
            assertEquals(coordinate.getX(), drone.getCoordinates().getX());
            assertEquals(coordinate.getY(), drone.getCoordinates().getY());
        }
    }

    @Test
    public void testEcho() {
        for (CardinalDirection cardDirection : CardinalDirection.values()) {
            Drone drone = new Drone(100, cardDirection);
            for (RelativeDirection relDirection : RelativeDirection.values()) {
                Action action = drone.echo(relDirection);
                assertEquals(ActionType.ECHO, action.getType());
                switch(relDirection) {
                    case LEFT:
                        assertEquals(cardDirection.nextLeft(), action.getDirection());
                        break;
                    case RIGHT:
                        assertEquals(cardDirection.nextRight(), action.getDirection());
                        break;
                    case FORWARD:
                        assertEquals(cardDirection, action.getDirection());
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + relDirection);
                }
            }    
        }
    }

    @Test
    public void testHeading() {
        for (CardinalDirection cardDirection : CardinalDirection.values()) {
            RelativeDirection relDirection = RelativeDirection.RIGHT;
            Drone drone = new Drone(100, cardDirection);
            Action action = drone.heading(relDirection);
            assertEquals(ActionType.HEADING, action.getType());
            Coordinate correctCoordinate;
            switch(relDirection) {
                case LEFT:
                    switch(cardDirection) {
                        case N:
                            correctCoordinate = new Coordinate(-1, 1);
                            break;
                        case E:
                            correctCoordinate = new Coordinate(1,1);
                            break;
                        case S:
                            correctCoordinate = new Coordinate(1, -1);
                            break;
                        case W:
                            correctCoordinate = new Coordinate(-1, -1);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + cardDirection);
                    }
                    break;
                case RIGHT:
                    switch(cardDirection) {
                        case N:
                            correctCoordinate = new Coordinate(1, 1);
                            break;
                        case E:
                            correctCoordinate = new Coordinate(1,-1);
                            break;
                        case S:
                            correctCoordinate = new Coordinate(-1, -1);
                            break;
                        case W:
                            correctCoordinate = new Coordinate(-1, 1);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + cardDirection);
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + relDirection);
            }
            Coordinate droneCoordinates = drone.getCoordinates();
            assertEquals(correctCoordinate.getX(), droneCoordinates.getX());
            assertEquals(correctCoordinate.getY(), droneCoordinates.getY());
            
        }
    }
}
