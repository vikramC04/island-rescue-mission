package ca.mcmaster.se2aa4.island.team222;

import java.io.StringReader;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

import ca.mcmaster.se2aa4.island.team222.directions.CardinalDirection;

public class Explorer implements IExplorerRaid {

    private Scan controller;

    @Override
    public void initialize(String s) {
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        String direction = info.getString("heading");
        int batteryLevel = info.getInt("budget");
        controller = new Interlaced(batteryLevel, CardinalDirection.valueOf(direction));
    }

    @Override
    public String takeDecision() {
        JSONObject decision = controller.decide().translate();
        return decision.toString();
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        controller.react(response);
    }

    @Override
    public String deliverFinalReport() {
        return controller.generateReport();
    }
}
