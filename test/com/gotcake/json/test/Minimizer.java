package com.gotcake.json.test;

import com.gotcake.json.JSONMinifier;

import java.io.IOException;

/**
 * Created by aaron on 9/29/14.
 */
public class Minimizer {

    public static void main(String[] args) throws IOException {
        Timer t = new Timer();
        t.start();
        JSONMinifier.minifyFile("resources/test/massive.json", "resources/test/massive.min.json");
        t.stopAndLog("Minify massive.json: ");
        t.start();
        JSONMinifier.minifyFile("resources/test/huge.json", "resources/test/huge.min.json");
        t.stopAndLog("Minify huge.json: ");
        t.start();
        JSONMinifier.minifyFile("resources/test/large.json", "resources/test/large.min.json");
        t.stopAndLog("Minify large.json: ");
        t.start();
        JSONMinifier.minifyFile("resources/test/small.json", "resources/test/small.min.json");
        t.stopAndLog("Minify small.json: ");
    }

}
