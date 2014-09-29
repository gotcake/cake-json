package com.gotcake.json.parser.impl;

import com.gotcake.json.parser.JSONArrayHandler;
import com.gotcake.json.parser.JSONObjectHandler;

import java.util.ArrayList;

/**
 * Created by aaron on 9/26/14.
 */
public class JSONArrayListArrayHandler implements JSONArrayHandler<ArrayList<Object>> {

    private ArrayList<Object> list;

    public JSONArrayListArrayHandler() {
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
        return new JSONLinkedHashMapObjectHandler();
    }

    @Override
    public JSONArrayHandler<?> getChildArrayHandler(int index) {
        return new JSONArrayListArrayHandler();
    }
}
