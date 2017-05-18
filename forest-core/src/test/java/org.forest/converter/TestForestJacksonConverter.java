package org.forest.converter;

import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.Assert;
import org.forest.converter.json.ForestFastjsonConverter;
import org.forest.converter.json.ForestJacksonConverter;
import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

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


}
