package com.gotcake.json;

import java.util.Collection;

/**
 * Created by aaron on 9/29/14.
 */
public interface JSONArrayBuilder<T> {

    public JSONArrayBuilder<T> element(Object element);

    public JSONArrayBuilder<T> elements(Object... elements);

    public JSONArrayBuilder<T> elements(Collection<?> elements);

    public JSONArrayBuilder<JSONArrayBuilder<T>> arrayElement();

    public JSONObjectBuilder<JSONArrayBuilder<T>> objectElement();

    public T end();

}
