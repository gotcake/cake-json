package com.gotcake.json.test;

import com.gotcake.json.JSONDecoder;
import com.gotcake.json.JSONMinifier;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by aaron on 9/29/14.
 */
public class JSONEndToEndTest {

    @Test
    public void testMinifierMassive() {
        JSONMinifier.minifyFile("resources/test/massive.json", "tmp/tmp.json");
        assertEquals(JSONDecoder.parseFile("resources/test/massive.json"), JSONDecoder.parseFile("tmp/tmp.json"));
    }

}
