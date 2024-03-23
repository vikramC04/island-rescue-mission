package ca.mcmaster.se2aa4.island.team222.actions;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

import ca.mcmaster.se2aa4.island.team222.directions.CardinalDirection;

public class Action {

    private ActionType actionType;
    private CardinalDirection actionDirection;
    private Map<String, String> parameters = new HashMap<>();

    //Actions with no Parameters
    public Action(ActionType actionType) {
        this.actionType = actionType;
    }

    //Actions with direction Parameter
    public Action(ActionType actionType, CardinalDirection direction) {
        this.actionType = actionType;
        this.actionDirection = direction;
        this.parameters.put("direction", String.valueOf(direction));
    }


    //Translates action objects back to JSON
    public JSONObject translate() {
        JSONObject obj = new JSONObject();
        JSONObject parameters = new JSONObject();

        switch(this.actionType) {
            case FLY:
                obj.put("action", "fly");
                break;

            case SCAN:
                obj.put("action", "scan");
                break;

            case STOP:
                obj.put("action", "stop");
                break;

            case HEADING:
                obj.put("action", "heading");
                parameters.put("direction", this.parameters.get("direction"));
                obj.put("parameters", parameters);
                break;

            case ECHO:
                obj.put("action", "echo");
                parameters.put("direction", this.parameters.get("direction"));
                obj.put("parameters", parameters);
                break;

            default:
                throw new IllegalStateException("Undefined response type: " + this.actionType);
        }

        return obj;
    }

    public ActionType getType() {
        return this.actionType;
    }

    public CardinalDirection getDirection() {
        return this.actionDirection;
    }
}
