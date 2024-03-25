package ca.mcmaster.se2aa4.island.team222.responses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team222.Value;
import ca.mcmaster.se2aa4.island.team222.actions.ActionType;

public class ScanResponse implements Response{
    
    private ActionType responseType = ActionType.SCAN;
    private int cost;
    private String status;
    private Map<String, Value> data;

    private enum POIS{
        sites,creeks,biomes
    }
    
    public ScanResponse(JSONObject response) {

        this.cost = response.getInt("cost");
        this.status = response.getString("status");
        this.data = new HashMap<String, Value>();

        addToArr(response, POIS.sites);
        addToArr(response, POIS.creeks);
        addToArr(response, POIS.biomes);

    }

    private void addToArr(JSONObject response, POIS poi){
        
        JSONObject extraInfo = response.getJSONObject("extras");
        JSONArray arr = extraInfo.getJSONArray(String.valueOf(poi));
        List<String> pois = new ArrayList<String>();
        for(int i = 0; i < arr.length(); i++) {
            pois.add(arr.getString(i));
        }
        data.put(String.valueOf(poi), new Value(pois));

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
