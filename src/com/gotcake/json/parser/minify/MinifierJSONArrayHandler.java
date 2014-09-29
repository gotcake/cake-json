package com.gotcake.json.parser.minify;

import com.gotcake.json.JSONEncoder;
import com.gotcake.json.RuntimeIOException;
import com.gotcake.json.parser.JSONArrayHandler;
import com.gotcake.json.parser.JSONObjectHandler;
import static com.gotcake.json.parser.minify.MinifierJSONHandlerFactory.*;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by aaron on 9/29/14.
 */
public class MinifierJSONArrayHandler implements JSONArrayHandler<Object> {

    private Writer writer;
    private boolean first;

    public MinifierJSONArrayHandler(Writer writer) {
        this.writer = writer;
        first = true;
        try{
            writer.write('[');
        } catch(IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    @Override
    public void handleElement(Object arrayElement) {
        if (arrayElement != DUMMY_OBJ) {
            if (first) {
                first = false;
            } else {
                try {
                    writer.write(',');
                } catch(IOException e) {
                    throw new RuntimeIOException(e);
                }
            }
            JSONEncoder.write(arrayElement, writer);
        }
    }

    @Override
    public Object getObject() {
        try {
            writer.write(']');
        } catch(IOException e) {
            throw new RuntimeIOException(e);
        }
        return DUMMY_OBJ;
    }

    @Override
    public JSONObjectHandler<?> getChildObjectHandler(int index) {
        if (first) {
            first = false;
        } else {
            try {
                writer.write(',');
            } catch(IOException e) {
                throw new RuntimeIOException(e);
            }
        }
        return new MinifierJSONObjectHandler(writer);
    }

    @Override
    public JSONArrayHandler<?> getChildArrayHandler(int index) {
        if (first) {
            first = false;
        } else {
            try {
                writer.write(',');
            } catch(IOException e) {
                throw new RuntimeIOException(e);
            }
        }
        return new MinifierJSONArrayHandler(writer);
    }
}
