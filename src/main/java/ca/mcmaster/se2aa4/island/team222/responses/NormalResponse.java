package ca.mcmaster.se2aa4.island.team222.responses;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team222.Value;
import ca.mcmaster.se2aa4.island.team222.actions.ActionType;

public class NormalResponse implements Response {
    
    private ActionType responseType;
    private int cost;
    private String status;
    private Map<String, Value> data;

    public NormalResponse(JSONObject response, ActionType previousAction) {
        this.responseType = previousAction;
        this.cost = response.getInt("cost");
        this.status = response.getString("status");
        this.data = new HashMap<String, Value>();
    }

    @Override
    public int getCost() {
        return this.cost;
    }

    @Override
    public String getStatus() {
        return this.status;
    }

    @Override
    public ActionType getType() {
        return this.responseType;
    }

    @Override
    public Map<String, Value> getData() {
        return this.data;
    }
}
