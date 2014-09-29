package com.gotcake.json;

import java.util.Map;

/**
 * Created by aaron on 9/29/14.
 */
public interface JSONObjectBuilder<T> {

    public JSONObjectBuilder<T> entry(String key, Object value);

    public JSONObjectBuilder<T> entries(Map<String, Object> entries);

    public JSONObjectBuilder<JSONObjectBuilder<T>> objectEntry(String key);

    public JSONArrayBuilder<JSONObjectBuilder<T>> arrayEntry(String key);

    public T end();

}
