package ca.mcmaster.se2aa4.island.team222.Phases;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.mcmaster.se2aa4.island.team222.Drone;
import ca.mcmaster.se2aa4.island.team222.Actions.*;
import ca.mcmaster.se2aa4.island.team222.Responses.Response;

public class FindIsland implements Phase {
    
    private final Logger logger = LogManager.getLogger();

    private boolean reachedEnd = false;
    private FindIslandState currentState;
    private Drone drone;

    public enum FindIslandState {
        TEMP
    }

    public FindIsland(Drone drone) {
        logger.info("FindIsland phase begins.");
        this.reachedEnd = false;
        this.currentState = FindIslandState.TEMP;
        this.drone = drone;
    }

    @Override
    public Action getNextDecision() {
        return new Action(ActionType.SCAN);
    }

    @Override
    public void react(Response response) {
        reachedEnd = true;
    }

    @Override
    public Phase getNextPhase() {
        return new FindIsland(this.drone);
    }

    @Override
    public boolean reachedEnd() {
        return this.reachedEnd;
    }

    @Override
    public boolean isFinal() {
        return true;
    }
}
