package com.gotcake.json.test;

import com.gotcake.json.RuntimeIOException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by aaron on 9/29/14.
 */
public class FileComparer {

    public static boolean compare(String fileA, String fileB) {
        return new FileComparer(fileA, fileB, 1024).compare();
    }

    private final FileReader a, b;
    private final char[] buffA, buffB;

    private FileComparer(String fileA, String fileB, int bufferSize) {
        try {
            a = new FileReader(fileA);
            b = new FileReader(fileB);
            buffA = new char[bufferSize];
            buffB = new char[bufferSize];
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    public boolean compare() {
        try {
            while (true) {
                int readA = a.read(buffA), readB = b.read(buffB);
                if (readA != readB) return false;
                if (readA == -1)
                    return true;
                for (int i=0; i<readA; i++)
                    if (buffA[i] != buffB[i])
                        return false;
            }
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }



}
