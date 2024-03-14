package ca.mcmaster.se2aa4.island.team222;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class Action {

    private final Logger logger = LogManager.getLogger();

    ActionType actionType;
    Map<String, String> parameters = new HashMap<>();

    //Actions with no Parameters
    public Action(ActionType actionType) {
        this.actionType = actionType;
    }

    //Actions with direction Parameter
    public Action(ActionType actionType, Direction direction) {
        this.actionType = actionType;
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
                logger.error("JSON translation error.");
        }

        return obj;
    }
}
