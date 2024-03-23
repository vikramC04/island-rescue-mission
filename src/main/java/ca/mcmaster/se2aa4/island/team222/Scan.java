package ca.mcmaster.se2aa4.island.team222;

import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team222.actions.Action;

public interface Scan {
    Action decide();
    void react(JSONObject responseObj);
    String generateReport();
}

