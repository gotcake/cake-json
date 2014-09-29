package com.gotcake.json.parser.minify;

import com.gotcake.json.JSONEncoder;
import com.gotcake.json.parser.JSONArrayHandler;
import com.gotcake.json.parser.JSONHandlerFactory;
import com.gotcake.json.parser.JSONObjectHandler;

import java.io.Writer;

/**
 * Created by aaron on 9/29/14.
 */
public class MinifierJSONHandlerFactory implements JSONHandlerFactory<Object, Object> {

    protected static final Object DUMMY_OBJ = new Object();

    private final Writer writer;

    public MinifierJSONHandlerFactory(Writer writer) {
        this.writer = writer;
    }

    @Override
    public JSONObjectHandler<Object> getObjectHandler() {
        return new MinifierJSONObjectHandler(writer);
    }

    @Override
    public JSONArrayHandler<Object> getArrayHandler() {
        return new MinifierJSONArrayHandler(writer);
    }
}
