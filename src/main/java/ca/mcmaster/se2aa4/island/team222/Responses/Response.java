package ca.mcmaster.se2aa4.island.team222.Responses;

import java.util.Map;

import ca.mcmaster.se2aa4.island.team222.Value;
import ca.mcmaster.se2aa4.island.team222.Actions.ActionType;

public interface Response {
    int getCost();
    String getStatus();
    ActionType getType();
    Map<String, Value> getData();
}
