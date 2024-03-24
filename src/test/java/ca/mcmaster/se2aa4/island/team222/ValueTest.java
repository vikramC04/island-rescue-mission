package ca.mcmaster.se2aa4.island.team222;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValueTest {

    @Test
    public void testConstructorWithIntValue() {
        int intValue = 42;
        Value value = new Value(intValue);
        assertEquals(intValue, value.getIntValue());
        assertNull(value.getStringValue());
        assertNull(value.getArrayValue());
    }

    @Test
    public void testConstructorWithStringValue() {
        String stringValue = "test";
        Value value = new Value(stringValue);
        assertEquals(stringValue, value.getStringValue());
        assertEquals(0, value.getIntValue());
        assertNull(value.getArrayValue());
    }

    @Test
    public void testConstructorWithStringListValue() {
        List<String> stringListValue = Arrays.asList("item1", "item2", "item3");
        Value value = new Value(stringListValue);
        assertEquals(stringListValue, value.getArrayValue());
        assertEquals(0, value.getIntValue());
        assertNull(value.getStringValue());
    }

    @Test
    public void testGetters() {
        List<String> stringListValue = new ArrayList<>(Arrays.asList("item1", "item2", "item3"));
        Value value = new Value(0);
        Value v2 = new Value("Hello");
        Value v3 = new Value(stringListValue);
        assertEquals(0, value.getIntValue());
        assertEquals("Hello", v2.getStringValue());
        assertEquals(stringListValue, v3.getArrayValue());
    }
}

