package ca.mcmaster.se2aa4.island.team222;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import ca.mcmaster.se2aa4.island.team222.actions.*;
import ca.mcmaster.se2aa4.island.team222.directions.*;

import static org.junit.jupiter.api.Assertions.*;

public class ActionTest {

    @Test
    public void testActionTypeConstructor() {
        Action action = new Action(ActionType.FLY);
        assertNotNull(action);
        assertEquals(ActionType.FLY, action.getType());
    }

    @Test
    public void testActionTypeWithDirectionConstructor() {
        for (CardinalDirection direction : CardinalDirection.values()) {
            Action action = new Action(ActionType.HEADING, direction);
            assertNotNull(action);
            assertEquals(ActionType.HEADING, action.getType());
        }
    }

    @Test
    public void testTranslateFlyAction() {
        Action flyAction = new Action(ActionType.FLY);
        JSONObject flyJson = flyAction.translate();
        assertNotNull(flyJson);
        assertEquals("fly", flyJson.getString("action"));
    }

    @Test
    public void testTranslateScanAction() {
        Action scanAction = new Action(ActionType.SCAN);
        JSONObject scanJson = scanAction.translate();
        assertNotNull(scanJson);
        assertEquals("scan", scanJson.getString("action"));
    }

    @Test
    public void testTranslateStopAction() {
        Action stopAction = new Action(ActionType.STOP);
        JSONObject stopJson = stopAction.translate();
        assertNotNull(stopJson);
        assertEquals("stop", stopJson.getString("action"));
    }

    @Test
    public void testTranslateHeadingAction() {
        for (CardinalDirection direction : CardinalDirection.values()) {
            Action headingAction = new Action(ActionType.HEADING, direction);
            JSONObject headingJson = headingAction.translate();
            assertNotNull(headingJson);
            assertEquals("heading", headingJson.getString("action"));
            JSONObject headingParams = headingJson.getJSONObject("parameters");
            assertNotNull(headingParams);
            assertEquals(direction.toString(), headingParams.getString("direction"));
        }
    }

    @Test
    public void testTranslateEchoAction() {
        for (CardinalDirection direction : CardinalDirection.values()) {
            Action echoAction = new Action(ActionType.ECHO, direction);
            JSONObject echoJson = echoAction.translate();
            assertNotNull(echoJson);
            assertEquals("echo", echoJson.getString("action"));
            JSONObject echoParams = echoJson.getJSONObject("parameters");
            assertNotNull(echoParams);
            assertEquals(direction.toString(), echoParams.getString("direction"));
        }
    }
}
