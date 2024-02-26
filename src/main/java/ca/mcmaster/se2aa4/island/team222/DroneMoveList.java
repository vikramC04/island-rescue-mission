package ca.mcmaster.se2aa4.island.team222;

import org.json.JSONObject;

public class DroneMoveList {
    
    //Fly Action
    public JSONObject fly() {
        JSONObject fly = new JSONObject();
        fly.put("action", "fly");
        return fly;
    }

    //Heading Action
    public JSONObject heading(Direction direction) {
        JSONObject heading = new JSONObject();
        heading.put("action", "heading");
        JSONObject parameters = new JSONObject();
        parameters.put("direction", direction.toString());
        heading.put("parameters", parameters);
        return heading;
    }

    //Echo Action
    public JSONObject echo(Direction direction) {
        JSONObject echo = new JSONObject();
        echo.put("action", "echo");
        JSONObject parameters = new JSONObject();
        parameters.put("direction", direction.toString());
        echo.put("parameters", parameters);
        return echo;
    }

    //Scan Action
    public JSONObject scan() {
        JSONObject scan = new JSONObject();
        scan.put("action", "scan");
        return scan;
    }

    //Stop Action
    public JSONObject stop() {
        JSONObject stop = new JSONObject();
        stop.put("action", "stop");
        return stop;
    }
    
}
