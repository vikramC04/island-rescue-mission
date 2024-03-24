package ca.mcmaster.se2aa4.island.team222;


import static org.junit.jupiter.api.Assertions.assertEquals;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team222.actions.ActionType;
import ca.mcmaster.se2aa4.island.team222.responses.EchoResponse;

public class EchoResponseTest {

    @Test
    public void testGetCost() {
        JSONObject responseJson = new JSONObject();
        responseJson.put("cost", 10);
        responseJson.put("status", "OK");
        JSONObject extrasJson = new JSONObject();
        extrasJson.put("found", "something");
        extrasJson.put("range", 5);
        responseJson.put("extras", extrasJson);

        EchoResponse echoResponse = new EchoResponse(responseJson);
        assertEquals(10, echoResponse.getCost());
    }

    @Test
    public void testGetStatus() {
        JSONObject responseJson = new JSONObject();
        responseJson.put("cost", 10);
        responseJson.put("status", "OK");
        JSONObject extrasJson = new JSONObject();
        extrasJson.put("found", "something");
        extrasJson.put("range", 5);
        responseJson.put("extras", extrasJson);

        EchoResponse echoResponse = new EchoResponse(responseJson);
        assertEquals("OK", echoResponse.getStatus());
    }

    @Test
    public void testGetType() {
        JSONObject responseJson = new JSONObject();
        responseJson.put("cost", 10);
        responseJson.put("status", "OK");
        JSONObject extrasJson = new JSONObject();
        extrasJson.put("found", "something");
        extrasJson.put("range", 5);
        responseJson.put("extras", extrasJson);

        EchoResponse echoResponse = new EchoResponse(responseJson);
        assertEquals(ActionType.ECHO, echoResponse.getType());
    }
}

