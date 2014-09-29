package com.gotcake.json.parser.impl;

import com.gotcake.json.parser.JSONArrayHandler;
import com.gotcake.json.parser.JSONObjectHandler;

import java.util.LinkedHashMap;

/**
 * Created by aaron on 9/26/14.
 */
public class JSONLinkedHashMapObjectHandler implements JSONObjectHandler<LinkedHashMap<String, Object>> {

    private LinkedHashMap<String, Object> map;

    public JSONLinkedHashMapObjectHandler() {
        map = new LinkedHashMap<String, Object>();
    }

    @Override
    public void handleEntry(String key, Object value) {
        map.put(key, value);
    }

    @Override
    public LinkedHashMap<String, Object> getObject() {
        return map;
    }

    @Override
    public JSONObjectHandler<?> getChildObjectHandler(String key) {
        return new JSONLinkedHashMapObjectHandler();
    }

    @Override
    public JSONArrayHandler<?> getChildArrayHandler(String key) {
        return new JSONArrayListArrayHandler();
    }
}
