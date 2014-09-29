package com.gotcake.json.parser;

/**
 * Created by aaron on 9/26/14.
 */
public interface JSONArrayHandler<T> {

    public void handleElement(Object arrayElement);

    public T getObject();

    public JSONObjectHandler<?> getChildObjectHandler(int index);

    public JSONArrayHandler<?> getChildArrayHandler(int index);

}
