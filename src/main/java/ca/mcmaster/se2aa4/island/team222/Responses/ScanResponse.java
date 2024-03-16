package ca.mcmaster.se2aa4.island.team222.Responses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team222.Value;
import ca.mcmaster.se2aa4.island.team222.Actions.ActionType;

public class ScanResponse implements Response{
    
    private ActionType responseType = ActionType.SCAN;
    private int cost;
    private String status;
    private Map<String, Value> data;
    private List<String> biomes;
    private List<String> creeks;
    private List<String> sites;
    
    public ScanResponse(JSONObject response) {
        this.cost = response.getInt("cost");
        this.status = response.getString("status");

        JSONObject extraInfo = response.getJSONObject("extras");
        
        //Parse biomes JSON Array
        JSONArray biomesArr = extraInfo.getJSONArray("biomes");
        biomes = new ArrayList<String>();
        for(int i = 0; i < biomesArr.length(); i++) {
            biomes.add(biomesArr.getString(i));
        }

        //Parse creeks JSON Array
        JSONArray creeksArr = extraInfo.getJSONArray("creeks");
        creeks = new ArrayList<String>();
        for(int i = 0; i < creeksArr.length(); i++) {
            creeks.add(creeksArr.getString(i));
        }

        //Parse sites JSON Array
        JSONArray sitesArr = extraInfo.getJSONArray("sites");
        sites = new ArrayList<String>();
        for(int i = 0; i < sitesArr.length(); i++) {
            sites.add(sitesArr.getString(i));
        }

        //Load the data into an hash map
        data = new HashMap<String, Value>();
        data.put("biomes", new Value(biomes));
        data.put("creeks", new Value(creeks));
        data.put("sites", new Value(sites));
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
