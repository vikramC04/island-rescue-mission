package ca.mcmaster.se2aa4.island.team222;

import java.util.List;

import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team222.actions.ActionType;
import ca.mcmaster.se2aa4.island.team222.responses.EchoResponse;
import ca.mcmaster.se2aa4.island.team222.responses.NormalResponse;
import ca.mcmaster.se2aa4.island.team222.responses.Response;
import ca.mcmaster.se2aa4.island.team222.responses.ScanResponse;

public class CreateResponse {
    public static Response createEcho(int cost, String status, String found, int range) {
        JSONObject obj = new JSONObject();
        JSONObject parameters = new JSONObject();
        obj.put("cost", cost);
        obj.put("status", status);
        parameters.put("found", found);
        parameters.put("range", range);
        obj.put("extras", parameters);
        Response ecResponse = new EchoResponse(obj);
        return ecResponse;
    }

    public static Response createNormal(int cost, String status) {
        JSONObject obj = new JSONObject();
        obj.put("cost", cost);
        obj.put("status", status);
        Response nrResponse = new NormalResponse(obj, ActionType.HEADING);
        return nrResponse;
    }

    public static Response createScan(int cost, String status, List<String> creeks, List<String> biomes, List<String> sites) {
        JSONObject obj = new JSONObject();
        JSONObject parameters = new JSONObject();
        obj.put("cost", cost);
        obj.put("status", status);
        parameters.put("creeks", creeks);
        parameters.put("biomes", creeks);
        parameters.put("sites", creeks);
        obj.put("extras", parameters);
        Response nrResponse = new ScanResponse(obj);
        return nrResponse;
    }
    
}
