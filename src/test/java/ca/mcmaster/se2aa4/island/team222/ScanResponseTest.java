package ca.mcmaster.se2aa4.island.team222;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team222.actions.ActionType;
import ca.mcmaster.se2aa4.island.team222.responses.ScanResponse;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ScanResponseTest {

    @Test
    public void testGetCost() {
        JSONObject extras = new JSONObject();
        extras.put("biomes", new JSONArray());
        extras.put("creeks", new JSONArray());
        extras.put("sites", new JSONArray());

        JSONObject responseJson = new JSONObject();
        responseJson.put("cost", 15);
        responseJson.put("status", "OK");
        responseJson.put("extras", extras);

        ScanResponse scanResponse = new ScanResponse(responseJson);

        assertEquals(15, scanResponse.getCost());
    }

    @Test
    public void testGetStatus() {
        JSONObject extras = new JSONObject();
        extras.put("biomes", new JSONArray());
        extras.put("creeks", new JSONArray());
        extras.put("sites", new JSONArray());

        JSONObject responseJson = new JSONObject();
        responseJson.put("cost", 20);
        responseJson.put("status", "Error");
        responseJson.put("extras", extras);

        ScanResponse scanResponse = new ScanResponse(responseJson);

        assertEquals("Error", scanResponse.getStatus());
    }

    @Test
    public void testGetType() {
        JSONObject extras = new JSONObject();
        extras.put("biomes", new JSONArray());
        extras.put("creeks", new JSONArray());
        extras.put("sites", new JSONArray());

        JSONObject responseJson = new JSONObject();
        responseJson.put("cost", 20);
        responseJson.put("status", "OK");
        responseJson.put("extras", extras);

        ScanResponse scanResponse = new ScanResponse(responseJson);

        assertEquals(ActionType.SCAN, scanResponse.getType());
    }

    @Test
    public void testGetData() {
        JSONArray biomesJson = new JSONArray();
        biomesJson.put("forest");
        biomesJson.put("mountain");

        JSONArray creeksJson = new JSONArray();
        creeksJson.put("creek1");
        creeksJson.put("creek2");

        JSONArray sitesJson = new JSONArray();
        sitesJson.put("site1");

        JSONObject extrasJson = new JSONObject();
        extrasJson.put("biomes", biomesJson);
        extrasJson.put("creeks", creeksJson);
        extrasJson.put("sites", sitesJson);

        JSONObject responseJson = new JSONObject();
        responseJson.put("cost", 10);
        responseJson.put("status", "OK");
        responseJson.put("extras", extrasJson);

        ScanResponse scanResponse = new ScanResponse(responseJson);

        Map<String, Value> data = scanResponse.getData();

        assertNotNull(data);
        assertTrue(data.containsKey("biomes"));
        assertTrue(data.containsKey("creeks"));
        assertTrue(data.containsKey("sites"));

        List<String> biomes = data.get("biomes").getArrayValue();
        assertNotNull(biomes);
        assertEquals(2, biomes.size());
        assertTrue(biomes.contains("forest"));
        assertTrue(biomes.contains("mountain"));

        List<String> creeks = data.get("creeks").getArrayValue();
        assertNotNull(creeks);
        assertEquals(2, creeks.size());
        assertTrue(creeks.contains("creek1"));
        assertTrue(creeks.contains("creek2"));

        List<String> sites = data.get("sites").getArrayValue();
        assertNotNull(sites);
        assertEquals(1, sites.size());
        assertTrue(sites.contains("site1"));
    }
}
