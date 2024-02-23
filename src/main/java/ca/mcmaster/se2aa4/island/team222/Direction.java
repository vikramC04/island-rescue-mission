package ca.mcmaster.se2aa4.island.team222;

public enum Direction {
    
    NORTH, EAST, SOUTH, WEST;



    public Direction nextRight() {
        switch (this) {
            case NORTH -> {
                return EAST;
            }
            case SOUTH -> {
                return WEST;
            }
            case WEST -> {
                return NORTH;
            }
            case EAST -> {
                return SOUTH;
            }
        }
        throw new IllegalStateException("Unexpected value: " + this);
    }

    public Direction nextLeft() {
        switch (this) {
            case NORTH -> {
                return WEST;
            }
            case SOUTH -> {
                return EAST;
            }
            case WEST -> {
                return SOUTH;
            }
            case EAST -> {
                return NORTH;
            }
        }
        throw new IllegalStateException("Unexpected value: " + this);
    }

}
