package com.gotcake.json.parser.minify;

import com.gotcake.json.JSONEncoder;
import com.gotcake.json.JSONObjectBuilder;
import com.gotcake.json.RuntimeIOException;
import com.gotcake.json.parser.JSONArrayHandler;
import com.gotcake.json.parser.JSONObjectHandler;
import static com.gotcake.json.parser.minify.MinifierJSONHandlerFactory.*;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by aaron on 9/29/14.
 */
public class MinifierJSONObjectHandler implements JSONObjectHandler<Object> {

    private JSONObjectBuilder builder;

    public MinifierJSONObjectHandler(JSONObjectBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void handleEntry(String key, Object value) {
        if (value != DUMMY_OBJ)
            builder.entry(key, value);
    }

    @Override
    public JSONObjectHandler<?> getChildObjectHandler(String key) {
        return new MinifierJSONObjectHandler(builder.objectEntry(key));
    }

    @Override
    public JSONArrayHandler<?> getChildArrayHandler(String key) {
        return new MinifierJSONArrayHandler(builder.arrayEntry(key));
    }

    @Override
    public Object getObject() {
        builder.end();
        return DUMMY_OBJ;
    }
}
