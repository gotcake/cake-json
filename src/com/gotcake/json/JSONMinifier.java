package com.gotcake.json;

import com.gotcake.json.parser.minify.MinifierJSONHandlerFactory;

import java.io.*;

/**
 * Created by aaron on 9/29/14.
 */
public class JSONMinifier {

    private static JSONDecoder<Object, Object> createMinifier(Reader reader, Writer writer) {
        MinifierJSONHandlerFactory factory = new MinifierJSONHandlerFactory(writer);
        return new JSONDecoder<Object, Object>(reader, factory);
    }

    public static void minify(Reader reader, Writer writer) {
        createMinifier(reader, writer).parse();
        try {
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    public static String minifyString(String json) {
        StringWriter writer = new StringWriter();
        createMinifier(new StringReader(json), writer).parse();
        writer.flush();
        return writer.toString();
    }

    public static void minifyFile(String sourceFile, String targetFile) {
        try {
            FileWriter writer = new FileWriter(targetFile);
            createMinifier(new FileReader(sourceFile), writer).parse();
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

}
