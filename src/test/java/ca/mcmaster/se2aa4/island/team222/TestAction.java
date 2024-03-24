package ca.mcmaster.se2aa4.island.team222;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.mcmaster.se2aa4.island.team222.actions.Action;
import ca.mcmaster.se2aa4.island.team222.actions.ActionType;
import ca.mcmaster.se2aa4.island.team222.directions.CardinalDirection;

public class TestAction {
    public static void testAction(Action action, CardinalDirection dir, ActionType type) {
        assertEquals(dir, action.getDirection());
        assertEquals(type, action.getType());
    }

    public static void testAction(Action action, ActionType type) {
        assertEquals(type, action.getType());
    }
}
