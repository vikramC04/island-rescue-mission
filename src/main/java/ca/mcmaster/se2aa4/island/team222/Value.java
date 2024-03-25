package ca.mcmaster.se2aa4.island.team222;

import java.util.List;

public class Value {
    
    private int intValue;
    private String stringValue;
    private List<String> stringListValue;

    public Value(int intValue) {
        this.intValue = intValue;
    }

    public Value(String stringValue) {
        this.stringValue = stringValue;
    }

    public Value(List<String> stringListValue) {
        this.stringListValue = stringListValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public List<String> getArrayValue() {
        return stringListValue;
    }
}
