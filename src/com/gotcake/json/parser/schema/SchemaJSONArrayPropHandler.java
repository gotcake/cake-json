package com.gotcake.json.parser.schema;

import com.gotcake.json.parser.JSONArrayHandler;
import com.gotcake.json.parser.JSONObjectHandler;
import com.gotcake.json.parser.impl.JSONLinkedHashMapObjectHandler;

/**
 * A lowest-level array handler that reports array elements to a ancestor object handler as an array property
 * @author Aaron Cake
 */
public class SchemaJSONArrayPropHandler implements JSONArrayHandler<Object> {

    private final SchemaJSONObjectHandler<?> parent;
    protected final Schema<?> schema;
    protected final int arrayDepth;
    private final int[] indexes;
    private int index;
    private final String propName;

    public SchemaJSONArrayPropHandler(String propName, Schema schema, SchemaJSONObjectHandler<?> parent) {
        this.parent = parent;
        this.propName = propName;
        this.schema = schema;
        this.arrayDepth = 0;
        this.indexes = new int[1];
        this.index = 0;
    }

    public SchemaJSONArrayPropHandler(String propName, Schema schema, int arrayDepth, int[] indexes, SchemaJSONObjectHandler<?> parent) {
        this.parent = parent;
        this.propName = propName;
        this.schema = schema;
        this.arrayDepth = arrayDepth;
        this.indexes = indexes;
        this.index = 0;
    }

    @Override
    public void handleElement(Object arrayElement) {
        indexes[arrayDepth] = index++;
        if (parent.schema.shouldValidate()) {
            String error = parent.schema.validateProperty(propName, arrayElement);
            if (error != null && !parent.decoder.handleInvalidArrayProperty(propName, indexes, arrayElement))
                throw new SchemaDecoderException(error);
        }
        parent.decoder.handleArrayProperty(propName, indexes, arrayElement);
    }


    @Override
    public Object getObject() {
        return null;
    }

    @Override
    public JSONObjectHandler<?> getChildObjectHandler(int index) {
        if (schema == null)
            return new JSONLinkedHashMapObjectHandler();
        return new SchemaJSONObjectHandler(schema);
    }

    @Override
    public JSONArrayHandler<?> getChildArrayHandler(int index) {
        throw new SchemaDecoderException("Expected an object, but found an array.");
    }
}
