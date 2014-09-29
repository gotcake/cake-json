package com.gotcake.json.test;

import com.gotcake.json.JSONDecoder;
import com.gotcake.json.JSONMinifier;
import com.gotcake.json.parser.schema.Schema;
import com.gotcake.json.parser.schema.SchemaDecoder;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by aaron on 9/29/14.
 */
public class SchemaTest {

    @Test
    public void testBasicSchema() {
        Schema<UserDecoder> userSchema = new Schema<UserDecoder>(UserDecoder.class, true);
        userSchema.defineArrayProperty()
    }

    private class UserDecoder implements SchemaDecoder {

        @Override
        public void handleProperty(String property, Object value) {

        }

        @Override
        public void handleArrayProperty(String property, int[] index, Object value) {

        }

        @Override
        public boolean handleInvalidProperty(String property, Object value) {
            return false;
        }

        @Override
        public boolean handleInvalidArrayProperty(String property, int[] index, Object value) {
            return false;
        }

        @Override
        public boolean handleMissingProperties(Set<String> missingProperties) {
            return false;
        }
    }

}
