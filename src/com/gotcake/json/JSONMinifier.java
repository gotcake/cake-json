package com.gotcake.json;

import com.gotcake.json.parser.minify.MinifierJSONHandlerFactory;

import java.io.*;

/**
 * Created by aaron on 9/29/14.
 */
public class JSONMinifier {

    private JSONMinifier() {}

    public static void minify(Reader reader, Writer writer) {
        MinifierJSONHandlerFactory factory = new MinifierJSONHandlerFactory(writer);
        JSONDecoder<Object, Object> decoder = new JSONDecoder<Object, Object>(reader, factory);
        decoder.parse();
        factory.flushEncoder();
    }

    public static String minifyString(String json) {
        StringWriter writer = new StringWriter();
        minify(new StringReader(json), writer);
        return writer.toString();
    }

    public static void minifyFile(String sourceFile, String targetFile) {
        try {
            FileWriter writer = new FileWriter(targetFile);
            FileReader reader = new FileReader(sourceFile);
            minify(reader, writer);
            reader.close();
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

}
