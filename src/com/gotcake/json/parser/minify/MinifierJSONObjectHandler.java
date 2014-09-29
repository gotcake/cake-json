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
public class MinifierJSONObjectHandler implements JSONObjectHandler<Object> {

    private Writer writer;
    private boolean first;

    public MinifierJSONObjectHandler(Writer writer) {
        this.writer = writer;
        first = true;
        try{
            writer.write('{');
        } catch(IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    @Override
    public void handleEntry(String key, Object value) {

        if (value != DUMMY_OBJ) {
            try {
                if (first) {
                    first = false;
                } else {
                    writer.write(',');
                }
                JSONEncoder.writeEscapedString(key, writer);
                writer.write(':');
                JSONEncoder.write(value, writer);
            } catch (IOException e) {
                throw new RuntimeIOException(e);
            }
        }
    }

    @Override
    public JSONObjectHandler<?> getChildObjectHandler(String key) {
        try {
            if (first) {
                first = false;
            } else {
                writer.write(',');
            }
            JSONEncoder.writeEscapedString(key, writer);
            writer.write(':');
        } catch(IOException e) {
            throw new RuntimeIOException(e);
        }
        return new MinifierJSONObjectHandler(writer);
    }

    @Override
    public JSONArrayHandler<?> getChildArrayHandler(String key) {
        try {
            if (first) {
                first = false;
            } else {
                writer.write(',');
            }
            JSONEncoder.writeEscapedString(key, writer);
            writer.write(':');
        } catch(IOException e) {
            throw new RuntimeIOException(e);
        }
        return new MinifierJSONArrayHandler(writer);
    }

    @Override
    public Object getObject() {
        try {
            writer.write('}');
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
        return DUMMY_OBJ;
    }
}
