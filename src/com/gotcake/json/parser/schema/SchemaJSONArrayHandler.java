package com.gotcake.json.parser.schema;

import com.gotcake.json.parser.JSONArrayHandler;
import com.gotcake.json.parser.JSONObjectHandler;

import java.util.ArrayList;

/**
 * Created by aaron on 9/28/14.
 */
public class SchemaJSONArrayHandler<O extends SchemaDecoder> implements JSONArrayHandler<ArrayList<Object>> {

    protected final Schema<O> schema;
    private final ArrayList<Object> list;

    public SchemaJSONArrayHandler(Schema<O> schema) {
        this.schema = schema;
        list = new ArrayList<Object>();
    }

    @Override
    public void handleElement(Object arrayElement) {
        list.add(arrayElement);
    }

    @Override
    public ArrayList<Object> getObject() {
        return list;
    }

    @Override
    public JSONObjectHandler<?> getChildObjectHandler(int index) {
        return new SchemaJSONObjectHandler<O>(schema);
    }

    @Override
    public JSONArrayHandler<?> getChildArrayHandler(int index) {
        return new SchemaJSONArrayHandler<O>(schema);
    }
}
