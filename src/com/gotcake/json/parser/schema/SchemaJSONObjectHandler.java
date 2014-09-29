package com.gotcake.json.parser.schema;

import com.gotcake.json.parser.JSONArrayHandler;
import com.gotcake.json.parser.JSONObjectHandler;
import com.gotcake.json.parser.impl.JSONArrayListArrayHandler;
import com.gotcake.json.parser.impl.JSONLinkedHashMapObjectHandler;

import java.util.HashSet;

/**
 * Created by aaron on 9/28/14.
 */
public class SchemaJSONObjectHandler<O extends SchemaDecoder> implements JSONObjectHandler<O> {

    protected final Schema<O> schema;
    protected final O decoder;
    private HashSet<String> props;

    public SchemaJSONObjectHandler(Schema<O> schema) {
        this.schema = schema;
        this.decoder = schema.newDecoderInstance();
    }

    @Override
    public void handleEntry(String key, Object value) {
        if (schema.shouldValidate()) {
            if (props == null)
                props = new HashSet<String>();
            if (!props.add(key))
                throw new SchemaDecoderException("Duplicate property " + key + " for object of schema " + schema.getClass().getCanonicalName());
            String error = schema.validateProperty(key, value);
            if (error != null && !decoder.handleInvalidProperty(key, value))
                throw new SchemaDecoderException(error);
        } else {
            decoder.handleProperty(key, value);
        }
    }

    @Override
    public O getObject() {
        if (schema.shouldValidate()) {
            String error = schema.validateRequiredProperties(props);
            if (error != null)
                throw new SchemaDecoderException(error);
        }
        return decoder;
    }

    @Override
    public JSONObjectHandler<?> getChildObjectHandler(String key) {

        Schema<O>.Property prop = schema.getProperty(key);

        // if there is no prop defined, return a map object handler
        if (prop == null)
            return new JSONLinkedHashMapObjectHandler();

        // if the property is an array type, throw an exception
        if (prop.arrayDimensions > 0)
            // TODO: support this
            throw new SchemaDecoderException("Property " + key + " of schema " + schema.getName()
                    + " is an array property, but decoder encountered an object type.");

        if (prop.schema == null)
            return new JSONLinkedHashMapObjectHandler();

        return new SchemaJSONObjectHandler(prop.schema);

    }

    @Override
    public JSONArrayHandler<?> getChildArrayHandler(String key) {

        Schema<O>.Property prop = schema.getProperty(key);

        // if there is no prop defined, return a map object handler
        if (prop == null)
            return new JSONArrayListArrayHandler();

        // if the property is not an array type, throw an exception
        if (prop.arrayDimensions == 0)
            // TODO: support this
            throw new SchemaDecoderException("Property " + key + " of schema " + schema.getName()
                    + " is not an array property, but decoder encountered an array type.");

        if (prop.schema == null)
            return new JSONArrayListArrayHandler();

        if (prop.arrayDimensions > 1)
            return new SchemaJSONUpperArrayPropHandler(key, prop.schema, prop.arrayDimensions, this);

        return new SchemaJSONArrayPropHandler(key, prop.schema, this);

    }
}
