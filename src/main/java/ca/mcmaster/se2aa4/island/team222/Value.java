package ca.mcmaster.se2aa4.island.team222;

import java.util.List;

public class Value {
    
    private int intValue;
    private String stringValue;
    private List<String> stringListValue;

    // Constructor for integer value
    public Value(int intValue) {
        this.intValue = intValue;
    }

    // Constructor for string value
    public Value(String stringValue) {
        this.stringValue = stringValue;
    }

    // Constructor for string list value
    public Value(List<String> stringListValue) {
        this.stringListValue = stringListValue;
    }

    // Getter methods for accessing the values
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
