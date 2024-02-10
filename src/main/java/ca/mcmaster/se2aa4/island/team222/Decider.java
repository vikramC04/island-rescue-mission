package ca.mcmaster.se2aa4.island.team222;

import org.json.JSONObject;
import org.json.JSONTokener;


public class Decider {
    
    JSONObject previous;

    public JSONObject decide() {
        JSONObject decision = new JSONObject();
        decision.put("action", "stop"); // we stop the exploration immediately
        return decision;
    }
}
