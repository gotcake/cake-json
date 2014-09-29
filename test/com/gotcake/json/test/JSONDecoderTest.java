package com.gotcake.json.test;

import com.gotcake.json.JSONDecoder;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class JSONDecoderTest {

    private static LinkedHashMap<Object, Object> map(Object... keysAndValues) {
        LinkedHashMap<Object, Object> map = new LinkedHashMap<Object, Object>();
        if (keysAndValues.length % 2 != 0)
            throw new RuntimeException("Must provide an even number of key/value arguments.");
        for (int i=0; i<keysAndValues.length; i+=2) {
            map.put(keysAndValues[i], keysAndValues[i+1]);
        }
        return map;
    }

    @Test
    public void testDecodeSimpleArray() {
        ArrayList<Object> expected = new ArrayList<Object>();
        expected.addAll(Arrays.asList(1, false, 3, "bar", null));
        String json = "[1, false, 3, \"bar\", null]";
        Object actual = JSONDecoder.parse(json);
        assertEquals(expected, actual);
    }

    @Test
    public void testDecodeNestedArray() {
        ArrayList<Object> expected = new ArrayList<Object>();
        expected.addAll(Arrays.asList("foo", 1, false, Arrays.asList("bar", 2, Arrays.asList(3, 4, null), 5), true,  6));
        String json = "[\"foo\", 1, false, [\"bar\", 2, [3, 4, null], 5], true, 6]";
        Object actual = JSONDecoder.parse(json);
        assertEquals(expected, actual);
    }

    @Test
    public void testDecodeSimpleObject() {
        LinkedHashMap expected = map(
                "foo", "bar",
                "age", 3,
                "yes", true,
                "no", false,
                "nil", null
        );
        String json = "{\"foo\": \"bar\", \"age\": 3, \"yes\": true, \"no\": false, \"nil\": null}";
        Object actual = JSONDecoder.parse(json);
        assertEquals(expected, actual);
    }

    @Test
    public void testDecodeNestedObject() {
        LinkedHashMap expected = map(
                "foo", "bar",
                "map", map (
                        "baz", 123.0f,
                        "map", map(
                                "123", 123
                        )
                ),
                "yes", true,
                "no", false,
                "nil", null
        );
        String json = "{\"foo\": \"bar\", \"map\": {\"baz\": 123.0, \"map\": {\"123\":123}}, \"yes\": true, \"no\": false, \"nil\": null}";
        Object actual = JSONDecoder.parse(json);
        assertEquals(expected, actual);
    }

    @Test
    public void testComplexObject() {
        LinkedHashMap expected = map(
                "foo", Arrays.asList(true, false, 1, 2, 3.0f),
                "arr", Arrays.asList(
                        map (
                            "baz", 123.0f,
                            "map", map( "123", 123 )
                        ),
                        null, 1, 5
                    ),
                "yes", true,
                "no", false,
                "nil", null
        );
        String json = "{\"foo\": [true, false, 1, 2, 3.0], \"arr\": [{\"baz\": 123.0, \"map\": {\"123\":123}}, null, 1, 5], \"yes\": true, \"no\": false, \"nil\": null}";
        Object actual = JSONDecoder.parse(json);
        assertEquals(expected, actual);
    }

}