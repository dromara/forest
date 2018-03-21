package org.forest.test.converter;

import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.Assert;
import org.forest.converter.json.ForestFastjsonConverter;
import org.forest.converter.json.ForestJacksonConverter;
import org.forest.exceptions.ForestRuntimeException;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-05-08 23:26
 */
public class TestForestJacksonConverter {

    private ObjectMapper mapper = new ObjectMapper();
    {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    }


    @Test
    public void testConvertToJson() {
        ForestJacksonConverter forestJacksonConverter = new ForestJacksonConverter();
        String text = forestJacksonConverter.convertToJson(new Integer[] {100, 10});
        Assert.assertEquals("[100,10]", text);
    }

    @Test
    public void testConvertToJsonError() {
        ForestJacksonConverter forestJacksonConverter = new ForestJacksonConverter();
        Map map = new HashMap();
        map.put("ref", map);

        boolean error = false;
        try {
            forestJacksonConverter.convertToJson(map);
        } catch (ForestRuntimeException e) {
            error = true;
            assertNotNull(e.getCause());
        }
        assertTrue(error);
    }


    @Test
    public void testConvertToJava() {
        String jsonText = "{\"a\":1, \"b\":2}";
        ForestJacksonConverter forestJacksonConverter = new ForestJacksonConverter();
        Map result = forestJacksonConverter.convertToJavaObject(jsonText, Map.class);
        assertNotNull(result);
        assertEquals(1, result.get("a"));
        assertEquals(2, result.get("b"));

        result = forestJacksonConverter.convertToJavaObject(jsonText, mapper.constructType(Map.class));
        assertNotNull(result);
        assertEquals(1, result.get("a"));
        assertEquals(2, result.get("b"));
    }


    @Test
    public void testConvertToJavaError() {
        String jsonText = "{\"a\":1, ";
        boolean error = false;
        ForestJacksonConverter forestJacksonConverter = new ForestJacksonConverter();
        try {
            forestJacksonConverter.convertToJavaObject(jsonText, Map.class);
        } catch (ForestRuntimeException e) {
            error = true;
            assertNotNull(e.getCause());
        }
        assertTrue(error);

        error = false;
        try {
            forestJacksonConverter.convertToJavaObject(jsonText, mapper.constructType(Map.class));
        } catch (ForestRuntimeException e) {
            error = true;
            assertNotNull(e.getCause());
        }
        assertTrue(error);

        jsonText = "[1, 2,";
        try {
            forestJacksonConverter.convertToJavaObject(jsonText, List.class);
        } catch (ForestRuntimeException e) {
            error = true;
            assertNotNull(e.getCause());
        }
        assertTrue(error);

        jsonText = "[1, 2,";
        try {
            forestJacksonConverter.convertToJavaObject(jsonText, List.class, Integer.class);
        } catch (ForestRuntimeException e) {
            error = true;
            assertNotNull(e.getCause());
        }
        assertTrue(error);

    }



}
