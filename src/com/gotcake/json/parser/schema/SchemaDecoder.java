package com.gotcake.json.parser.schema;

import java.util.Set;

/**
 * Created by aaron on 9/28/14.
 */
public interface SchemaDecoder {

    public void handleProperty(String property, Object value);

    public void handleArrayProperty(String property, int[] index, Object value);

    public boolean handleInvalidProperty(String property, Object value);

    public boolean handleInvalidArrayProperty(String property, int[] index, Object value);

    public boolean handleMissingProperties(Set<String> missingProperties);

}
