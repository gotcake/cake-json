package com.gotcake.json;

/**
 * Created by aaron on 9/29/14.
 */
public interface JSONObjectWriter<T> {

    public <O extends T> void writeJSON(JSONEncoder encoder, O obj);

    public Class<T> getSuperSupportClass();

    public boolean supportsSubclass(Class<? extends T> subclass);

}
