package com.gotcake.json;

import com.gotcake.json.parser.*;
import com.gotcake.json.parser.impl.DefaultJSONHandlerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * A buffered JSON decoder implementation
 * @author Aaron Cake
 */
public class JSONDecoder<A, O> {

    /**
     * Parse a single value from a JSON string
     * @param string a valid JSON string
     * @return the parsed value
     */
    public static Object parse(String string) {
        return createDefaultDecoder(new StringReader(string)).parse();
    }

    /**
     * Parse a single value from a Reader containing a valid JSON stream
     * @param reader a Reader containing a valid JSON stream
     * @return the parsed value
     */
    public static Object parse(Reader reader) {
        return createDefaultDecoder(reader).parse();
    }

    /**
     * Parse a single value from a file containing JSON
     * @param file the path to the file to parse
     * @return the parsed value
     */
    public static Object parseFile(String file) {
        try {
            FileReader reader = new FileReader(file);
            ArrayList<Object> data = createDefaultDecoder(reader).parseArray();
            reader.close();
            return data;
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    /**
     * Creates a default JSONDecoder
     * @param r the Reader for the decoder to read from
     * @return a new JSONDecoder
     */
    public static JSONDecoder<ArrayList<Object>, LinkedHashMap<String, Object>> createDefaultDecoder(Reader r) {
        return new JSONDecoder<ArrayList<Object>, LinkedHashMap<String, Object>>(r, DefaultJSONHandlerFactory.INSTANCE);
    }

    private final Reader reader;
    private final char[] buffer;
    private int line, col, pos, numChars;
    private JSONHandlerFactory<? extends A, ? extends O> handlerFactory;

    public JSONDecoder(Reader reader, JSONHandlerFactory<? extends A, ? extends O> handlerFactory) {
        this(reader, handlerFactory, 1024);
    }

    public JSONDecoder(Reader reader, JSONHandlerFactory<? extends A, ? extends O> handlerFactory, int bufferSize) {
        this.reader = new BufferedReader(reader);
        buffer = new char[bufferSize];
        numChars = pos = 0;
        line = col = 1;
        this.handlerFactory = handlerFactory;
    }

    private void readMore() {
        try {
            pos = 0;
            numChars = reader.read(buffer);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    private void skipWhitespace() {

        // if we are EOF, return
        if (numChars == -1)
            return;

        // read whitespace
        while (true) {

            // if we are at the end of the buffer, read more
            if (pos == numChars)
                readMore();

            // if we are EOF, return
            if (numChars == -1)
                return;

            switch (buffer[pos]) {
                // if it's whitespace, but not a newline, advance the read position and column counter
                case ' ': case '\t': case '\f': case '\r': ++pos; ++col; break;
                // if it's a newline, advance the read position and the line counter, and reset the column counter
                case '\n': ++pos; ++line; col=1; break;
                // not whitespace, return
                default: return;
            }

        }
    }

    private void expect(char ch, String what) {

        // if we are at the end of the buffer, read more
        if (pos == numChars)
            readMore();

        // if we are EOF, throw exception
        if (numChars == -1)
            throw unexpectedEof(what, String.valueOf(ch));

        // if the chars do not match, throw exception
        if (buffer[pos] != ch)
            throw unexpectedChar(buffer[pos], what, String.valueOf(ch));

        // else advance the position
        ++col;
        ++pos;

    }

    private void expect(char ch, String what, String expected) {


        // if we are at the end of the buffer, read more
        if (pos == numChars)
            readMore();

        // if we are EOF, throw exception
        if (numChars == -1)
            throw unexpectedEof(what, expected);

        // if the chars do not match, throw exception
        if (buffer[pos] != ch)
            throw unexpectedChar(buffer[pos], what, expected);

        // else advance the position
        ++col;
        ++pos;

    }

    private void expectLiteral(String literal, String what) {
        int len = literal.length();
        for (int i=0; i<len; ++i) {

            // if we are at the end of the buffer, read more
            if (pos == numChars)
                readMore();

            // if we are EOF, throw exception
            if (numChars == -1)
                throw unexpectedEof(what, String.valueOf(literal.charAt(i)));

            // if the chars do not match, throw exception
            if (buffer[pos] != literal.charAt(i))
                throw unexpectedChar(buffer[pos], what, String.valueOf(literal.charAt(i)));

            // else advance the position
            ++col;
            ++pos;

        }
    }

    public Object parse() {
        return parse(null, null);
    }

    @SuppressWarnings("unchecked")
    private Object parse(Object parent, Object parentKey) {

        skipWhitespace();

        // if we are at the end of the buffer, read more
        if (pos == numChars)
            readMore();

        // if we are EOF, throw exception
        if (numChars == -1)
            throw unexpectedEof("parsing json value");

        switch (buffer[pos]) {
            case '{':
                if (parent == null) {
                    return parseObject(handlerFactory.getObjectHandler(), false);
                } else if (parent instanceof JSONArrayHandler) {
                    return parseObject(((JSONArrayHandler)parent).getChildObjectHandler((Integer) parentKey), false);
                } else {
                    return parseObject(((JSONObjectHandler)parent).getChildObjectHandler((String)parentKey), false);
                }
            case '[':
                if (parent == null) {
                    return parseArray(handlerFactory.getArrayHandler(), false);
                } else if (parent instanceof JSONArrayHandler) {
                    return parseArray(((JSONArrayHandler) parent).getChildArrayHandler((Integer) parentKey), false);
                } else {
                    return parseArray(((JSONObjectHandler) parent).getChildArrayHandler((String) parentKey), false);
                }
            case '"':
                return parseString();
            case '-': case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9': case '.':
                return parseNumber();
            case 't':
                expectLiteral("true", "parsing boolean value: true");
                return true;
            case 'f':
                expectLiteral("false", "parsing boolean value: false");
                return false;
            case 'n':
                expectLiteral("null", "parsing null value");
                return null;
            default:
                throw unexpectedChar(buffer[pos], "parsing json value", "beginning of json value");
        }
    }

    public O parseObject() {
        return parseObject(handlerFactory.getObjectHandler(), true);
    }

    public A parseArray() {
        return parseArray(handlerFactory.getArrayHandler(), true);
    }

    public Number parseNumber() {

        StringBuilder sb = new StringBuilder();
        char ch = ' ';
        boolean isInt = true;


        while (true) {

            // if we are at the end of the buffer, read more
            if (pos == numChars)
                readMore();

            // if we are EOF, break
            if (numChars == -1)
                break;

            ch = buffer[pos];

            if ((ch < '0' || ch > '9') && ch != '-' && ch != '.' && ch != 'e' && ch != 'E') {
                break;
            } else {
                if (isInt && ch == '.')
                    isInt = false;
                sb.append(ch);

                ++pos;
                ++col;

            }
        }

        if (sb.length() == 0) {
            if (numChars == -1)
                throw unexpectedEof("parsing number");
            else
                throw unexpectedChar(ch, "parsing number", "valid number character");
        }

        try {
            if (isInt) {
                long longVal = Long.parseLong(sb.toString());
                if (longVal > Integer.MAX_VALUE || longVal < Integer.MIN_VALUE)
                    return longVal;
                else
                    return (int)longVal;
            } else {
                double doubleVal = Double.parseDouble(sb.toString());
                if (doubleVal > Float.MAX_VALUE || doubleVal < Float.MIN_VALUE)
                    return doubleVal;
                else
                    return (float)doubleVal;
            }
        } catch (NumberFormatException e) {
            throw new JSONParserException("Malformed number: " + sb.toString());
        }
    }

    private O parseObject(JSONObjectHandler<? extends O> handler, boolean skip) {
        if (skip)
            skipWhitespace();
        expect('{', "parsing object", "opening object bracket");
        boolean first = true;
        while (true) {
            skipWhitespace();

            // if we are at the end of the buffer, read more
            if (pos == numChars)
                readMore();

            // if we are EOF, throw exception
            if (numChars == -1)
                throw unexpectedEof("parsing object", "comma or object closing bracket");

            char ch = buffer[pos];

            // if it's an end bracket, consume and break
            if (ch == '}') {
                ++pos;
                ++col;
                break;
            }

            if (first) {
                first = false;
            } else if (ch == ',') {
                // consume the comma
                ++pos;
                ++col;
                skipWhitespace();
            } else {
                throw unexpectedChar(ch, "parsing object", "comma or object closing bracket");
            }

            String key = parseString();
            skipWhitespace();
            expect(':', "parsing object");
            Object val = parse(handler, key);
            handler.handleEntry(key, val);
        }
        return handler.getObject();
    }

    private A parseArray(JSONArrayHandler<? extends A> handler, boolean skip) {
        if (skip)
            skipWhitespace();
        expect('[', "parsing array", "opening array bracket");
        int i=0;
        while (true) {

            skipWhitespace();
            // if we are at the end of the buffer, read more
            if (pos == numChars)
                readMore();

            // if we are EOF, throw exception
            if (numChars == -1)
                throw unexpectedEof("parsing array", "comma or array closing bracket");

            char ch = buffer[pos];

            // if it's an end bracket, consume and break
            if (ch == ']') {
                ++pos;
                ++col;
                break;
            }

            if (i > 0) {
                if (ch == ',') {
                    ++pos;
                    ++col;
                    skipWhitespace();
                } else {
                    throw unexpectedChar(ch, "parsing array", "comma or array closing bracket");
                }
            }

            Object val = parse(handler, i);
            handler.handleElement(val);
            ++i;
        }
        return handler.getObject();
    }

    public String parseString() {

        expect('"', "parsing string", "opening quote");

        StringBuilder sb = new StringBuilder();
        boolean escaped = false, loop = true;
        char ch = ' ';

        while (loop) {

            // if we are at the end of the buffer, read more
            if (pos == numChars)
                readMore();

            // if we are EOF, break
            if (numChars == -1)
                break;

            ch = buffer[pos];

            // advance
            ++pos;
            ++col;

            if (escaped) {

                switch (ch) {
                    case 'u':
                        // read 4 characters
                        StringBuilder sb2 = new StringBuilder(4);
                        for (int i=0; i<4; ++i) {

                            // if we are at the end of the buffer, read more
                            if (pos == numChars)
                                readMore();

                            // if we are EOF, throw exception
                            if (numChars == -1)
                                throw unexpectedEof("parsing hex encoded character", "valid hex character");

                            ch = buffer[pos];

                            if ((ch < '0' || ch > '9') && (ch < 'A' || ch > 'F'))
                                throw unexpectedChar(ch, "parsing hex encoded character", "valid hex character");

                            sb2.append(ch);
                            ++pos;
                            ++col;

                        }
                        try {
                            sb.appendCodePoint(Integer.parseInt(sb2.toString(), 16));
                        } catch (NumberFormatException e) {
                            throw new JSONParserException("Malformed hex value: "+sb2.toString());
                        }
                        break;
                    case '\\': sb.append('\\'); break;
                    case 'b': sb.append('\b'); break;
                    case 't': sb.append('\t'); break;
                    case 'n': sb.append('\n'); break;
                    case 'f': sb.append('\f'); break;
                    case 'r': sb.append('\r'); break;
                    default: sb.append(ch);
                }

                // reset the escaped flag
                escaped = false;
            } else {
                switch (ch) {
                    case '\\': escaped = true; break;
                    case '"': loop = false; break;
                    default: sb.append(ch);
                }
            }

        }

        if (numChars == -1 && ch != '"')
            throw unexpectedEof("parsing string", "closing quote");

        return sb.toString();

    }

    private JSONParserException unexpectedEof(String what, String expected) {
        return new JSONParserException("Unexpected EOF while " + what + ". Expected "+expected+'.');
    }

    private JSONParserException unexpectedEof(String what) {
        return new JSONParserException("Unexpected EOF while " + what + '.');
    }

    private JSONParserException unexpectedChar(char ch, String what, String expected) {
        StringBuilder sb = new StringBuilder();
        sb.append("Unexpected ").append(ch).append(" at (").append(line).append(", ").append(col)
                .append(") while ").append(what).append(". Expected ").append(expected).append('.');
        return new JSONParserException(sb.toString());
    }

}