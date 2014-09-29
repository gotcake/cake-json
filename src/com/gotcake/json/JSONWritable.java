package com.gotcake.json;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by aaron on 9/25/14.
 */
public interface JSONWritable {

    public void writeJSON(JSONEncoder encoder) throws IOException;

}
