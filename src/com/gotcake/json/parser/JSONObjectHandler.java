package com.gotcake.json.parser;

/**
 * Created by aaron on 9/26/14.
 */
public interface JSONObjectHandler<T> {

    public void handleEntry(String key, Object value);

    public JSONObjectHandler<?> getChildObjectHandler(String key);

    public JSONArrayHandler<?> getChildArrayHandler(String key);

    public T getObject();

}
