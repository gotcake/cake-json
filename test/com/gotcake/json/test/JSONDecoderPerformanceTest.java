package com.gotcake.json.test;

import com.gotcake.json.JSONDecoder;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.json.simple.parser.JSONParser;


import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Created by aaron on 9/29/14.
 */
public class JSONDecoderPerformanceTest {

    private static String readFile(String file) throws IOException {
        FileReader reader = new FileReader(file);
        StringBuilder sb = new StringBuilder();
        int ch;
        while ((ch = reader.read()) != -1)
            sb.appendCodePoint(ch);
        return sb.toString();
    }

    private void testReader(int prime, int count, String file) throws IOException{
        Reader reader;
        Timer t = new Timer();
        for (int i=0; i<prime; i++) {
            reader = new FileReader("resources/test/"+file);
            JSONDecoder.parse(reader);
            reader.close();
        }
        for (int i=0; i<count; i++) {
            try {
                Thread.sleep(100);
                System.gc();
                Thread.sleep(300);
            } catch (InterruptedException e) { }
            reader = new FileReader("resources/test/"+file);
            t.start();
            JSONDecoder.parse(reader);
            t.stop();
            reader.close();
        }
        t.logAverage("Decode "+file+" (FileReader, avg " + count +" runs): ");
    }

    private void testString(int prime, int count, String file) throws IOException{
        Reader reader;
        Timer t = new Timer();
        String text = readFile("resources/test/"+file);
        for (int i=0; i<prime; i++) {
            JSONDecoder.parse(text);
        }
        for (int i=0; i<count; i++) {
            try {
                Thread.sleep(100);
                System.gc();
                Thread.sleep(300);
            } catch (InterruptedException e) { }
            t.start();
            JSONDecoder.parse(text);
            t.stop();
        }
        t.logAverage("Decode "+file+" (StringReader, avg " + count +" runs): ");
    }

    private void testString_lib(int prime, int count, String file) throws IOException, ParseException {
        Reader reader;
        Timer t = new Timer();
        String text = readFile("resources/test/"+file);
        for (int i=0; i<prime; i++) {
            JSONParser parser = new JSONParser();
            parser.parse(text);
        }
        for (int i=0; i<count; i++) {
            try {
                Thread.sleep(100);
                System.gc();
                Thread.sleep(300);
            } catch (InterruptedException e) { }
            t.start();
            JSONParser parser = new JSONParser();
            parser.parse(text);
            t.stop();
        }
        t.logAverage("Decode "+file+" (JSONSimple, StringReader, avg " + count +" runs): ");
    }

    private void testReader_lib(int prime, int count, String file) throws IOException, ParseException {
        FileReader reader;
        Timer t = new Timer();
        for (int i=0; i<prime; i++) {
            reader = new FileReader("resources/test/"+file);
            JSONParser parser = new JSONParser();
            parser.parse(reader);
            reader.close();
        }
        for (int i=0; i<count; i++) {
            try {
                Thread.sleep(100);
                System.gc();
                Thread.sleep(300);
            } catch (InterruptedException e) { }
            t.start();
            reader = new FileReader("resources/test/"+file);
            JSONParser parser = new JSONParser();
            parser.parse(reader);
            reader.close();
            t.stop();
        }
        t.logAverage("Decode "+file+" (JSONSimple, FileReader, avg " + count +" runs): ");
    }

    @Test
    public void testHugeReader() throws IOException {
        testReader(3, 20, "huge.json");
    }

    @Test
    public void testHugeString() throws IOException {
        testString(3, 20, "huge.json");
    }

    @Test
    public void testMassiveSimpleReader() throws IOException, ParseException {
        testReader_lib(2, 10, "massive.json");
    }

    @Test
    public void testMassiveSimpleString() throws IOException, ParseException {
        testString_lib(2, 10, "massive.json");
    }

    @Test
    public void testMassiveReader() throws IOException {
        testReader(2, 10, "massive.json");
    }

    @Test
    public void testMassiveString() throws IOException {
        testString(2, 10, "massive.json");
    }

}
