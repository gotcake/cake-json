package com.gotcake.json.parser;

/**
 * Created by aaron on 9/27/14.
 */
public interface JSONHandlerFactory<A, O> {

    public JSONObjectHandler<O> getObjectHandler();

    public JSONArrayHandler<A> getArrayHandler();

}
