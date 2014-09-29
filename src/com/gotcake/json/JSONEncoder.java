package com.gotcake.json;

import com.gotcake.json.util.Entry;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by aaron on 9/25/14.
 */
public class JSONEncoder {

    /**
     * Private constructor to prevent construction
     */
    private JSONEncoder() {}

    public static String stringify(Object any) {
        StringWriter writer = new StringWriter();
        write(any, writer);
        return writer.toString();
    }

    public static JSONObjectWriter<StringWriter> objectBuilder() {
        StringWriter writer = new StringWriter();
        return new JSONObjectWriter<StringWriter>(writer, writer);
    }

    public static JSONObjectWriter<Writer> objectWriter(Writer writer) {
        return new JSONObjectWriter<Writer>(writer, writer);
    }

    public static JSONArrayWriter<StringWriter> arrayBuilder() {
        StringWriter writer = new StringWriter();
        return new JSONArrayWriter<StringWriter>(writer, writer);
    }

    public static JSONArrayWriter<Writer> arrayWriter(Writer writer) {
        return new JSONArrayWriter<Writer>(writer, writer);
    }


    public static void write(Object any, Writer writer) {
        try {
            if (any == null) {
                writer.write("null");
            } else if (any.getClass().isArray()) {
                writer.write('[');
                int len = Array.getLength(any);
                for (int i = 0; i < len; ++i) {
                    if (i > 0)
                        writer.write(',');
                    write(Array.get(any, i), writer);
                }
                writer.write(']');
            } else if (any instanceof Collection) {
                writer.write('[');
                boolean first = true;
                for (Object o : (Collection) any) {
                    if (!first)
                        writer.write(',');
                    else
                        first = false;
                    write(o, writer);
                }
                writer.write(']');
            } else if (any instanceof Map) {
                writer.write('{');
                boolean first = true;
                for (Map.Entry entry : ((Map<?, ?>) any).entrySet()) {
                    if (!first)
                        writer.write(',');
                    else
                        first = false;
                    writeEscapedString(entry.getKey().toString(), writer);
                    writer.write(':');
                    write(entry.getValue(), writer);
                }
                writer.write('}');
            } else if (any instanceof String) {
                writeEscapedString((String) any, writer);
            } else if (any instanceof JSONWritable) {
                ((JSONWritable) any).writeJSON(writer);
            } else if (any instanceof Number || any instanceof Boolean) {
                writer.write(any.toString());
            } else {
                writeEscapedString(any.toString(), writer);
            }
        } catch (IOException ex) {
            throw new RuntimeIOException(ex);
        }
    }

    public static void writeEscapedString(String string, Writer writer) {
        try {
            writer.write('"');
            for (int i = 0; i < string.length(); ++i) {
                char c = string.charAt(i);
                switch (c) {
                    case '\\':
                    case '"':
                        writer.write('\\');
                        writer.write(c);
                        break;
                    /*case '/':
                        writer.write('\\');
                        writer.write(c);
                        break;*/
                    case '\b':
                        writer.write("\\b");
                        break;
                    case '\t':
                        writer.write("\\t");
                        break;
                    case '\n':
                        writer.write("\\n");
                        break;
                    case '\f':
                        writer.write("\\f");
                        break;
                    case '\r':
                        writer.write("\\r");
                        break;
                    default:
                        if (c < ' ') {
                            String t = "000" + Integer.toHexString(c);
                            writer.write("\\u" + t.substring(t.length() - 4));
                        } else {
                            writer.write(c);
                        }
                }
            }
            writer.write('"');
        } catch (IOException ex) {
            throw new RuntimeIOException(ex);
        }
    }

    public static class JSONObjectWriter<T> {

        private boolean first;
        private T parent;
        private Writer writer;

        private JSONObjectWriter(T parent, Writer writer) {
            this.writer = writer;
            writeCh('{');
            this.parent = parent;
            first = true;
        }

        private void writeCh(char c) {
            try {
                writer.write(c);
            } catch (IOException ex) {
                throw new RuntimeIOException(ex);
            }
        }

        public JSONObjectWriter<T> entry(String key, Object value) {
            if (!first)
                writeCh(',');
            else
                first = false;
            writeEscapedString(key, writer);
            writeCh(':');
            write(value, writer);
            return this;
        }

        public JSONObjectWriter<T> entries(Map<String, Object> map) {
            for (Map.Entry<String, Object> e: map.entrySet()) {
                if (!first)
                    writeCh(',');
                else
                    first = false;
                writeEscapedString(e.getKey(), writer);
                writeCh(':');
                write(e.getValue(), writer);
            }
            return this;
        }

        public JSONObjectWriter<T> entries(List<Entry> entries) {
            int size = entries.size();
            if (size > 0) {
                Entry e = entries.get(0);
                writeEscapedString(e.key, writer);
                writeCh(':');
                write(e.value, writer);
                for (int i=1; i<size; ++i) {
                    e = entries.get(i);
                    writeCh(',');
                    writeEscapedString(e.key, writer);
                    writeCh(':');
                    write(e.value, writer);
                }
            }
            return this;
        }

        public JSONObjectWriter<JSONObjectWriter<T>> objectEntry(String key) {
            if (!first)
                writeCh(',');
            else
                first = false;
            writeEscapedString(key, writer);
            writeCh(':');
            return new JSONObjectWriter<JSONObjectWriter<T>>(this, writer);
        }

        public JSONArrayWriter<JSONObjectWriter<T>> arrayEntry(String key) {
            if (!first)
                writeCh(',');
            else
                first = false;
            writeEscapedString(key, writer);
            writeCh(':');
            return new JSONArrayWriter<JSONObjectWriter<T>>(this, writer);
        }

        public T end() {
            writeCh('}');
            return parent;
        }


    }

    public static class JSONArrayWriter<T> {

        private boolean first;
        private T parent;
        private Writer writer;

        private JSONArrayWriter(T parent, Writer writer) {
            this.writer = writer;
            writeCh('[');
            this.parent = parent;
            first = true;
        }

        private void writeCh(char c) {
            try {
                writer.write(c);
            } catch (IOException ex) {
                throw new RuntimeIOException(ex);
            }
        }

        public JSONArrayWriter<T> element(Object element) {
            if (!first)
                writeCh(',');
            else
                first = false;
            write(element, writer);
            return this;
        }

        public JSONArrayWriter<T> elements(Object... elements) {
            for (Object el: elements)
                element(el);
            return this;
        }

        public JSONArrayWriter<T> elements(List<Object> elements) {
            int size = elements.size();
            if (size > 0){
                write(elements.get(0), writer);
                for (int i=1; i<size; ++i) {
                    writeCh(',');
                    write(elements.get(i), writer);
                }
            }
            return this;
        }

        public JSONArrayWriter<JSONArrayWriter<T>> arrayElement() {
            if (!first)
                writeCh(',');
            else
                first = false;
            return new JSONArrayWriter<JSONArrayWriter<T>>(this, writer);
        }

        public JSONObjectWriter<JSONArrayWriter<T>> objectElement() {
            if (!first)
                writeCh(',');
            else
                first = false;
            return new JSONObjectWriter<JSONArrayWriter<T>>(this, writer);
        }

        public T end() {
            writeCh(']');
            return parent;
        }

    }

}
