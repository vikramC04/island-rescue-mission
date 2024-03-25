package ca.mcmaster.se2aa4.island.team222.responses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team222.Value;
import ca.mcmaster.se2aa4.island.team222.actions.ActionType;
import ca.mcmaster.se2aa4.island.team222.pois.POIType;

public class ScanResponse implements Response{
    
    private ActionType responseType = ActionType.SCAN;
    private int cost;
    private String status;
    private Map<String, Value> data;
    private List<String> biomes = new ArrayList<>();
    private List<String> creeks = new ArrayList<>();
    private List<String> sites = new ArrayList<>();
    
    public ScanResponse(JSONObject response) {
        this.cost = response.getInt("cost");
        this.status = response.getString("status");
        this.data = new HashMap<>();
        addToArr(response, POIType.SITES, sites);
        addToArr(response, POIType.CREEKS, creeks);
        addToArr(response, POIType.BIOMES, biomes);
    }

    private void addToArr(JSONObject response, POIType poi, List<String> type){
        
        JSONObject extraInfo = response.getJSONObject("extras");
        String poiType = String.valueOf(poi).toLowerCase();
        JSONArray arr = extraInfo.getJSONArray(poiType);
        for(int i = 0; i < arr.length(); i++) {
            type.add(arr.getString(i));
        }
        data.put(poiType, new Value(type));

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
