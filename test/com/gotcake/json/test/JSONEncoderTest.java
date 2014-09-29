package com.gotcake.json.test;

import com.gotcake.json.JSONEncoder;
import com.gotcake.json.JSONWritable;
import org.junit.Test;
import com.gotcake.json.JSONEncoder.*;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static org.junit.Assert.*;

public class JSONEncoderTest {

    @Test
    public void writeEmptyArrayTest() throws IOException {
        String json = JSONEncoder.arrayBuilder()
                .end().toString();
        assertEquals("[]", json);
    }

    @Test
    public void writeEmptyObjectTest() {
        String json = JSONEncoder.objectBuilder()
                .end().toString();
        assertEquals("{}", json);
    }

    @Test
    public void writeSimpleArrayTest() {
        String json = JSONEncoder.arrayBuilder()
                .element(1)
                .element(3.14)
                .element("foo")
                .element(true)
                .end().toString();
        assertEquals("[1,3.14,\"foo\",true]", json);
    }

    @Test
    public void writeNestedTest() {
        String json = JSONEncoder.objectBuilder()
                .arrayEntry("foo")
                    .element(1)
                    .element(2)
                    .objectElement()
                        .entry("name", "Aaron Cake")
                        .entry("age", 22)
                        .end()
                    .end()
                .entry("pi", 3.14)
                .entry("bar", null)
                .end().toString();
        assertEquals("{\"foo\":[1,2,{\"name\":\"Aaron Cake\",\"age\":22}],\"pi\":3.14,\"bar\":null}", json);
    }

    @Test
    public void stringifyMapTest() {
        LinkedHashMap<Object, Object> map = new LinkedHashMap<Object, Object>();
        map.put("foo", "bar");
        map.put("bar", null);
        map.put(1, 3);
        String json = JSONEncoder.stringify(map);
        assertEquals("{\"foo\":\"bar\",\"bar\":null,\"1\":3}", json);
    }

    @Test
    public void stringifyArrayTest() {
        Object[] array = new Object[] {  1, "foo", null, true };
        String json = JSONEncoder.stringify(array);
        assertEquals("[1,\"foo\",null,true]", json);
    }

    @Test
    public void stringifyListTest() {
        List list = Arrays.asList(1, "foo", null, true);
        String json = JSONEncoder.stringify(list);
        assertEquals("[1,\"foo\",null,true]", json);
    }

    @Test
    public void complexTest() {

        LinkedHashMap<Object, Object> map = new LinkedHashMap<Object, Object>();
        map.put(true, false);
        map.put("two", new TestWritable(2));
        map.put("three", new TestWritable(3));

        String json = JSONEncoder.arrayBuilder()
                .element(1)
                .element("foo")
                .element(map)
                .element(null)
                .end().toString();

        String expected = "[1,\"foo\",{\"true\":false,\"two\":[1,4],\"three\":[1,4,9]},null]";

        assertEquals(expected, json);

    }

    private static class TestWritable implements JSONWritable {

        private int n;

        public TestWritable(int n) {
            this.n = n;
        }

        @Override
        public void writeJSON(Writer writer) throws IOException {
            JSONArrayWriter arrayWriter = JSONEncoder.arrayWriter(writer);
            for (int i=1; i<=n; ++i)
                arrayWriter.element(i*i);
            arrayWriter.end();
        }
    }




}