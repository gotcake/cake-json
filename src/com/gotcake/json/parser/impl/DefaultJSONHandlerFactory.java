package com.gotcake.json.parser.impl;

import com.gotcake.json.parser.JSONArrayHandler;
import com.gotcake.json.parser.JSONHandlerFactory;
import com.gotcake.json.parser.JSONObjectHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by aaron on 9/27/14.
 */
public class DefaultJSONHandlerFactory implements JSONHandlerFactory<ArrayList<Object>, LinkedHashMap<String, Object>> {

    public static final DefaultJSONHandlerFactory INSTANCE = new DefaultJSONHandlerFactory();

    private DefaultJSONHandlerFactory() { }

    @Override
    public JSONObjectHandler<LinkedHashMap<String, Object>> getObjectHandler() {
        return new JSONLinkedHashMapObjectHandler();
    }

    @Override
    public JSONArrayHandler<ArrayList<Object>> getArrayHandler() {
        return new JSONArrayListArrayHandler();
    }
}
