package ca.mcmaster.se2aa4.island.team222;

public enum Direction {
    
    N, E, S, W;



    public Direction nextRight() {
        switch (this) {
            case N -> {
                return E;
            }
            case S -> {
                return W;
            }
            case W -> {
                return N;
            }
            case E -> {
                return S;
            }
        }
        throw new IllegalStateException("Unexpected value: " + this);
    }

    public Direction nextLeft() {
        switch (this) {
            case N -> {
                return W;
            }
            case S -> {
                return E;
            }
            case W -> {
                return S;
            }
            case E -> {
                return N;
            }
        }
        throw new IllegalStateException("Unexpected value: " + this);
    }

}
