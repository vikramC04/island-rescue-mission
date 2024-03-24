package ca.mcmaster.se2aa4.island.team222;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team222.actions.ActionType;
import ca.mcmaster.se2aa4.island.team222.responses.NormalResponse;

public class NormalResponseTest {

    @Test
    public void testConstructor() {
        JSONObject responseJson = new JSONObject();
        responseJson.put("cost", 10);
        responseJson.put("status", "OK");

        NormalResponse normalResponse = new NormalResponse(responseJson, ActionType.SCAN);

        assertEquals(10, normalResponse.getCost());
        assertEquals("OK", normalResponse.getStatus());
        assertEquals(ActionType.SCAN, normalResponse.getType());
        assertNotNull(normalResponse.getData());
    }

    @Test
    public void testGetCost() {
        JSONObject responseJson = new JSONObject();
        responseJson.put("cost", 15);
        responseJson.put("status", "OK");

        NormalResponse normalResponse = new NormalResponse(responseJson, ActionType.FLY);

        assertEquals(15, normalResponse.getCost());
    }

    @Test
    public void testGetType() {
        JSONObject responseJson = new JSONObject();
        responseJson.put("cost", 10);
        responseJson.put("status", "OK");

        NormalResponse normalResponse = new NormalResponse(responseJson, ActionType.STOP);

        assertEquals(ActionType.STOP, normalResponse.getType());
    }

    @Test
    public void testGetStatus() {
        JSONObject responseJson = new JSONObject();
        responseJson.put("cost", 20);
        responseJson.put("status", "OK");

        NormalResponse normalResponse = new NormalResponse(responseJson, ActionType.HEADING);

        assertEquals("OK", normalResponse.getStatus());
    }
}

