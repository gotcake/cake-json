package com.gotcake.json.parser.minify;

import com.gotcake.json.JSONArrayBuilder;
import com.gotcake.json.parser.JSONArrayHandler;
import com.gotcake.json.parser.JSONObjectHandler;
import static com.gotcake.json.parser.minify.MinifierJSONHandlerFactory.*;

/**
 * Created by aaron on 9/29/14.
 */
public class MinifierJSONArrayHandler implements JSONArrayHandler<Object> {

    private JSONArrayBuilder builder;

    public MinifierJSONArrayHandler(JSONArrayBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void handleElement(Object arrayElement) {
        if (arrayElement != DUMMY_OBJ)
            builder.element(arrayElement);
    }

    @Override
    public Object getObject() {
        builder.end();
        return DUMMY_OBJ;
    }

    @Override
    public JSONObjectHandler<?> getChildObjectHandler(int index) {
        return new MinifierJSONObjectHandler(builder.objectElement());
    }

    @Override
    public JSONArrayHandler<?> getChildArrayHandler(int index) {
        return new MinifierJSONArrayHandler(builder.arrayElement());
    }
}
