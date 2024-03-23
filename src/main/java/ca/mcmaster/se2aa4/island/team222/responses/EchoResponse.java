package ca.mcmaster.se2aa4.island.team222.responses;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team222.Value;
import ca.mcmaster.se2aa4.island.team222.actions.ActionType;

public class EchoResponse implements Response {
    
    private ActionType responseType = ActionType.ECHO;
    private int cost;
    private String status;
    private Map<String, Value> data;

    public EchoResponse(JSONObject response) {
        this.cost = response.getInt("cost");
        this.status = response.getString("status");

        JSONObject extraInfo = response.getJSONObject("extras");
        String found = extraInfo.getString("found");
        int range = extraInfo.getInt("range");

        data = new HashMap<String, Value>();
        data.put("found", new Value(found));
        data.put("range", new Value(range));

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
