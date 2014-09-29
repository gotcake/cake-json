package com.gotcake.json.parser.schema;

import com.gotcake.json.parser.JSONArrayHandler;
import com.gotcake.json.parser.JSONObjectHandler;

/**
 * Created by aaron on 9/28/14.
 */
public class SchemaJSONUpperArrayPropHandler implements JSONArrayHandler<Object> {

    private final int[] indexes;
    private final int arrayDepth;
    private int index;
    private final Schema schema;
    private final SchemaJSONObjectHandler<?> parent;
    private final String propName;

    public SchemaJSONUpperArrayPropHandler(String propName, Schema schema, int arrayDimensions, SchemaJSONObjectHandler<?> parent) {
        this.schema = schema;
        this.propName = propName;
        this.arrayDepth = 0;
        this.indexes = new int[arrayDimensions];
        this.index = 0;
        this.parent = parent;
        // zero out the array
        for (int i=0; i<arrayDimensions; ++i)
            this.indexes[i] = 0;
    }

    public SchemaJSONUpperArrayPropHandler(String propName, Schema schema, int arrayDepth, int[] indexes, SchemaJSONObjectHandler<?> parent) {
        this.schema = schema;
        this.propName = propName;
        this.arrayDepth = arrayDepth;
        this.indexes = indexes;
        this.index = 0;
        this.parent = parent;
    }

    @Override
    public void handleElement(Object arrayElement) {
        this.indexes[this.arrayDepth] = ++index;
    }

    @Override
    public JSONObjectHandler<?> getChildObjectHandler(int index) {
        // TODO: support this
        throw new SchemaDecoderException("Expected an array, but found an object");
    }

    @Override
    public JSONArrayHandler<?> getChildArrayHandler(int index) {
        if (arrayDepth == indexes.length - 2) {
            return new SchemaJSONArrayPropHandler(propName, schema, indexes.length-1, indexes, parent);
        } else {
            return new SchemaJSONUpperArrayPropHandler(propName, schema, arrayDepth+1, indexes, parent);
        }
    }

    @Override
    public Object getObject() {
        return null;
    }
}
