package com.gotcake.json.parser.schema;

import com.gotcake.json.parser.JSONArrayHandler;
import com.gotcake.json.parser.JSONHandlerFactory;
import com.gotcake.json.parser.JSONObjectHandler;

import java.util.ArrayList;

/**
 * A handler factory for parsing with a schema
 * @author Aaron Cake
 */
public class SchemaJSONHandlerFactory<O extends SchemaDecoder> implements JSONHandlerFactory<ArrayList<Object>, O> {

    private Schema<O> baseSchema;

    public SchemaJSONHandlerFactory(Schema<O> schema) {
        baseSchema = schema;
    }

    @Override
    public JSONObjectHandler<O> getObjectHandler() {
        return new SchemaJSONObjectHandler<O>(baseSchema);
    }

    @Override
    public JSONArrayHandler<ArrayList<Object>> getArrayHandler() {
        return new SchemaJSONArrayHandler<O>(baseSchema);
    }

}