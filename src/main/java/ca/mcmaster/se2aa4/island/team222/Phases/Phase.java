package ca.mcmaster.se2aa4.island.team222.Phases;

import ca.mcmaster.se2aa4.island.team222.Actions.Action;
import ca.mcmaster.se2aa4.island.team222.Responses.Response;

public interface Phase {
    Action getNextDecision();
    Phase getNextPhase();
    void react(Response response);
    boolean reachedEnd();
    boolean isFinal();
}
