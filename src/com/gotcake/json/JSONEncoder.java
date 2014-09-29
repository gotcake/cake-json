package com.gotcake.json;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aaron Cake
 */
public class JSONEncoder {

    private static final HashMap<Class<?>, JSONObjectWriter<?>> writerMap = new HashMap<Class<?>, JSONObjectWriter<?>>();

    public static void registerWriter(JSONObjectWriter<?> writer) {
        writerMap.put(writer.getSuperSupportClass(), writer);
    }

    @SuppressWarnings("unchecked")
    private static JSONObjectWriter<?> getWriterForClass(Class<?> cls) {
        Class clazz = cls;
        while (clazz != Object.class) {
            JSONObjectWriter<?> writer = writerMap.get(clazz);
            if (writer != null && (clazz == cls || writer.supportsSubclass(clazz)))
                return writer;
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    private static final char[] NULL_LITERAL = new char[] {'n', 'u', 'l', 'l'};
    private static final char[] TRUE_LITERAL = new char[] {'t', 'r', 'u', 'e'};
    private static final char[] FALSE_LITERAL = new char[] {'f', 'a', 'l', 's', 'e'};

    private final Writer writer;
    private final char[] buffer;
    private int pos;

    public static void write(Object any, Writer writer) {
        JSONEncoder encoder = new JSONEncoder(writer);
        encoder.write(any);
        encoder.flush();
    }

    public static void writeFile(Object any, String file) {
        try {
            FileWriter writer = new FileWriter(file);
            JSONEncoder encoder = new JSONEncoder(writer);
            encoder.write(any);
            encoder.flush();
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    public static String stringify(Object any) {
        StringWriter writer = new StringWriter();
        JSONEncoder encoder = new JSONEncoder(writer);
        encoder.write(any);
        encoder.flush();
        return writer.toString();
    }

    public static JSONObjectBuilder<DeferredString> objectBuilder() {
        StringWriter writer = new StringWriter();
        JSONEncoder encoder = new JSONEncoder(writer);
        return encoder.object(new DeferredString(writer, encoder));
    }

    public static JSONObjectBuilder<Writer> objectBuilder(Writer writer) {
        JSONEncoder encoder = new JSONEncoder(writer);
        return encoder.object(writer);
    }

    public static JSONArrayBuilder<DeferredString> arrayBuilder() {
        StringWriter writer = new StringWriter();
        JSONEncoder encoder = new JSONEncoder(writer);
        return encoder.array(new DeferredString(writer, encoder));
    }

    public static JSONArrayBuilder<Writer> arrayBuilder(Writer writer) {
        JSONEncoder encoder = new JSONEncoder(writer);
        return encoder.array(writer);
    }

    /**
     * Create a JSONEncoder with a custom buffer size and writer
     * @param writer the writer to write JSON to
     * @param bufferSize the size of the buffer (min size = 128)
     */
    public JSONEncoder(Writer writer, int bufferSize) {
        this.writer = writer;
        this.buffer = new char[Math.max(128, bufferSize)];
    }

    /**
     * Create a new JSONEncoder with the given writer
     * @param writer the writer to write JSON to
     */
    public JSONEncoder(Writer writer) {
        this(writer, 1024);
    }

    /**
     * Flushes the buffer to the underlying writer. Does not flush the writer.
     */
    public void flush() {
        try {
            writer.write(buffer, 0, pos);
            pos = 0;
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    /**
     * Writes the characters in the given array
     * @param chars characters to write
     */
    private void writeLiteral(char[] chars) {
        if (chars.length + pos >= buffer.length)
            flush();
        for (char ch: chars)
            buffer[pos++] = ch;
    }

    /**
     * Writes the given string literal
     * @param str the string to write
     */
    private void writeLiteral(String str) {
        int len = str.length();
        if (len + pos >= buffer.length)
            flush();
        for (int i=0; i<len; ++i)
            buffer[pos++] = str.charAt(i);
    }

    /**
     * Creates a JSONArrayBuilder that will write to this encoder
     * @return a new JSONArrayBuilder
     */
    public JSONArrayBuilder<JSONEncoder> array() {
        return new JSONArrayBuilderImpl<JSONEncoder>(this);
    }

    /**
     * Creates a JSONObjectBuilder that will write to this encoder
     * @return a new JSONObjectBuilder
     */
    public JSONObjectBuilder<JSONEncoder> object() {
        return new JSONObjectBuilderImpl<JSONEncoder>(this);
    }

    private <T> JSONArrayBuilder<T> array(T thing) {
        return new JSONArrayBuilderImpl<T>(thing);
    }

    private <T> JSONObjectBuilder<T> object(T thing) {
        return new JSONObjectBuilderImpl<T>(thing);
    }

    /**
     * Write an object to this encoder
     * @param any any object or value
     */
    @SuppressWarnings("unchecked")
    public void write(Object any) {
        try {
            if (any == null) {
                writeLiteral(NULL_LITERAL);
            } else if (any.getClass().isArray()) {
                if (pos == buffer.length)
                    flush();
                buffer[pos++] = '[';
                int len = Array.getLength(any);
                for (int i = 0; i < len; ++i) {
                    if (i > 0) {
                        if (pos == buffer.length)
                            flush();
                        buffer[pos++] = ',';
                    }
                    write(Array.get(any, i));
                }
                if (pos == buffer.length)
                    flush();
                buffer[pos++] = ']';
            } else if (any instanceof Collection) {
                if (pos == buffer.length)
                    flush();
                buffer[pos++] = '[';
                boolean first = true;
                for (Object o : (Collection) any) {
                    if (!first) {
                        if (pos == buffer.length)
                            flush();
                        buffer[pos++] = ',';
                    } else
                        first = false;
                    write(o);
                }
                if (pos == buffer.length)
                    flush();
                buffer[pos++] = ']';
            } else if (any instanceof Map) {
                if (pos == buffer.length)
                    flush();
                buffer[pos++] = '{';
                boolean first = true;
                for (Map.Entry entry : ((Map<?, ?>) any).entrySet()) {
                    if (!first) {
                        if (pos == buffer.length)
                            flush();
                        buffer[pos++] = ',';
                    } else
                        first = false;
                    writeEscapedString(entry.getKey().toString());
                    if (pos == buffer.length)
                        flush();
                    buffer[pos++] = ':';
                    write(entry.getValue());
                }
                if (pos == buffer.length)
                    flush();
                buffer[pos++] = '}';
            } else if (any instanceof String) {
                writeEscapedString((String) any);
            } else if (any instanceof JSONWritable) {
                ((JSONWritable) any).writeJSON(this);
            } else if (any instanceof Boolean) {
                writeLiteral(Boolean.TRUE.equals(any) ? TRUE_LITERAL : FALSE_LITERAL);
            } else if (any instanceof Number) {
                writeLiteral(any.toString());
            } else {
                JSONObjectWriter writer = getWriterForClass(any.getClass());
                if (writer != null) {
                    writer.writeJSON(this, any);
                } else {
                    writeEscapedString(any.toString());
                }
            }
        } catch (IOException ex) {
            throw new RuntimeIOException(ex);
        }
    }

    /**
     * Writes a string value
     * @param string the string value to write
     */
    private void writeEscapedString(String string) {
        if (pos == buffer.length)
            flush();
        buffer[pos++] = '"';
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i), out = 0;
            switch (c) {
                case '\\': case '"': out = c; break;
                case '\b': out = 'b'; break;
                case '\t': out = 't'; break;
                case '\n': out = 'n'; break;
                case '\f': out = 'f'; break;
                case '\r': out = 'r'; break;
                default:
                    if (c < ' ') {
                        String t = Integer.toHexString(c);
                        int len = t.length();
                        if (pos >= buffer.length - 6)
                            flush();
                        buffer[pos++] = '\\';
                        buffer[pos++] = 'u';
                        for (int a=len; a<4; ++a)
                            buffer[pos++] = '0';
                        if (len <= 4) {
                            for (int a=0; a<len; ++a)
                                buffer[pos++] = t.charAt(a);
                        } else if (len > 4) {
                            for (int a=len-4; a<len; ++a)
                                buffer[pos++] = t.charAt(a);
                        }
                    } else {
                        if (pos == buffer.length)
                            flush();
                        buffer[pos++] = c;
                    }
            }
            if (out != 0) {
                if (pos >= buffer.length - 1)
                    flush();
                buffer[pos++] = '\\';
                buffer[pos++] = out;
            }
        }
        if (pos == buffer.length)
            flush();
        buffer[pos++] = '"';
    }

    /**
     * An implementation of JSONObjectBuilder that writes to a JSONEncoder
     * @param <T> the parent type
     */
    private class JSONObjectBuilderImpl<T> implements JSONObjectBuilder<T> {

        private boolean first;
        private T parent;

        private JSONObjectBuilderImpl(T parent) {
            if (pos == buffer.length)
                flush();
            buffer[pos++] = '{';
            this.parent = parent;
            first = true;
        }

        public JSONObjectBuilderImpl<T> entry(String key, Object value) {
            if (!first) {
                if (pos == buffer.length)
                    flush();
                buffer[pos++] = ',';
            } else
                first = false;
            writeEscapedString(key);
            if (pos == buffer.length)
                flush();
            buffer[pos++] = ':';
            write(value);
            return this;
        }

        public JSONObjectBuilderImpl<T> entries(Map<String, Object> map) {
            for (Map.Entry<String, Object> e: map.entrySet())
                entry(e.getKey(), e.getValue());
            return this;
        }

        public JSONObjectBuilder<JSONObjectBuilder<T>> objectEntry(String key) {
            if (!first) {
                if (pos == buffer.length)
                    flush();
                buffer[pos++] = ',';
            } else
                first = false;
            writeEscapedString(key);
            if (pos == buffer.length)
                flush();
            buffer[pos++] = ':';
            return new JSONObjectBuilderImpl<JSONObjectBuilder<T>>(this);
        }

        public JSONArrayBuilder<JSONObjectBuilder<T>> arrayEntry(String key) {
            if (!first) {
                if (pos == buffer.length)
                    flush();
                buffer[pos++] = ',';
            } else
                first = false;
            writeEscapedString(key);
            if (pos == buffer.length)
                flush();
            buffer[pos++] = ':';
            return new JSONArrayBuilderImpl<JSONObjectBuilder<T>>(this);
        }

        public T end() {
            if (pos == buffer.length)
                flush();
            buffer[pos++] = '}';
            return parent;
        }

    }

    /**
     * An implementation of JSONArrayBuilder that writes to a JSONEncoder
     * @param <T> the parent type
     */
    private class JSONArrayBuilderImpl<T> implements JSONArrayBuilder<T> {

        private boolean first;
        private T parent;

        private JSONArrayBuilderImpl(T parent) {
            if (pos == buffer.length)
                flush();
            buffer[pos++] = '[';
            this.parent = parent;
            first = true;
        }

        public JSONArrayBuilderImpl<T> element(Object element) {
            if (!first) {
                if (pos == buffer.length)
                    flush();
                buffer[pos++] = ',';
            } else
                first = false;
            write(element);
            return this;
        }

        public JSONArrayBuilderImpl<T> elements(Object... elements) {
            for (Object el: elements)
                element(el);
            return this;
        }

        public JSONArrayBuilderImpl<T> elements(Collection<?> elements) {
            for (Object el: elements) {
                if (!first) {
                    if (pos == buffer.length)
                        flush();
                    buffer[pos++] = ',';
                } else
                    first = false;
                write(el);
            }
            return this;
        }

        public JSONArrayBuilder<JSONArrayBuilder<T>> arrayElement() {
            if (!first) {
                if (pos == buffer.length)
                    flush();
                buffer[pos++] = ',';
            } else
                first = false;
            return new JSONArrayBuilderImpl<JSONArrayBuilder<T>>(this);
        }

        public JSONObjectBuilder<JSONArrayBuilder<T>> objectElement() {
            if (!first) {
                if (pos == buffer.length)
                    flush();
                buffer[pos++] = ',';
            } else
                first = false;
            return new JSONObjectBuilderImpl<JSONArrayBuilder<T>>(this);
        }

        public T end() {
            if (pos == buffer.length)
                flush();
            buffer[pos++] = ']';
            return parent;
        }

    }

    /**
     * A class which handles flushing of the JSONEncoder when toString is called
     */
    public static class DeferredString {

        private final StringWriter writer;
        private final JSONEncoder encoder;

        DeferredString(StringWriter writer, JSONEncoder encoder) {
            this.writer = writer;
            this.encoder = encoder;
        }

        public String toString() {
            encoder.flush();
            return writer.toString();
        }
    }

}
