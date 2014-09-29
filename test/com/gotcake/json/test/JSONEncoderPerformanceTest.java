package com.gotcake.json.test;

import com.gotcake.json.JSONDecoder;
import com.gotcake.json.JSONEncoder;
import org.junit.Test;

import java.io.*;

/**
 * Created by aaron on 9/29/14.
 */
public class JSONEncoderPerformanceTest {

    @Test
    public void testEncoderMassiveString() throws IOException {
        testEncodeStringWriter("massive.json", 10);
    }

    @Test
    public void testEncoderMassiveFile() throws IOException {
        testEncodeFileWriter("massive.json", 10);
    }

    private void testEncodeStringWriter(String source, int count) throws IOException {
        Object data = JSONDecoder.parse(new FileReader("resources/test/"+source));
        StringWriter writer;
        Timer t = new Timer();
        for (int i=0; i<count; i++) {
            try {
                Thread.sleep(100);
                System.gc();
                Thread.sleep(300);
            } catch (InterruptedException e) { }
            writer = new StringWriter();
            t.start();
            JSONEncoder.write(data, writer);
            writer.flush();
            t.stop();
            writer.close();
        }
        t.logAverage("Encode "+source+" (StringWriter, avg " + count +" runs): ");
    }

    private void testEncodeFileWriter(String source, int count) throws IOException {
        Object data = JSONDecoder.parse(new FileReader("resources/test/"+source));
        FileWriter writer;
        Timer t = new Timer();
        for (int i=0; i<count; i++) {
            try {
                Thread.sleep(100);
                System.gc();
                Thread.sleep(300);
            } catch (InterruptedException e) { }
            writer = new FileWriter("tmp/tmp.json");
            t.start();
            JSONEncoder.write(data, writer);
            writer.flush();
            t.stop();
            writer.close();
        }
        t.logAverage("Encode "+source+" (FileWriter, avg " + count +" runs): ");
    }

}
