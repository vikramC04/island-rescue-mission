package ca.mcmaster.se2aa4.island.team222.phases;

import ca.mcmaster.se2aa4.island.team222.actions.Action;
import ca.mcmaster.se2aa4.island.team222.pois.AllPOIS;
import ca.mcmaster.se2aa4.island.team222.responses.Response;

public interface Phase {
    Action getNextDecision();
    Phase getNextPhase();
    void react(Response response);
    boolean reachedEnd();
    boolean isFinal();
    AllPOIS getAllPOIS();
}
