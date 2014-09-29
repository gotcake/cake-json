package com.gotcake.json;

import java.io.IOException;

/**
 * Created by aaron on 9/26/14.
 */
public class RuntimeIOException extends RuntimeException {
    public final IOException original;
    public RuntimeIOException(IOException ex) {
        super(ex);
        original = ex;
    }
}
