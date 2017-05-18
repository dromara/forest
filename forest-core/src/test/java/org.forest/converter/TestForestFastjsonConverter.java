package org.forest.converter;

import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.Assert;
import org.forest.converter.json.ForestFastjsonConverter;
import org.forest.exceptions.ForestRuntimeException;
import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-05-08 23:13
 */
public class TestForestFastjsonConverter {

    @Test
    public void testSerializerFeature() {
        ForestFastjsonConverter forestFastjsonConverter = new ForestFastjsonConverter();
        String defaultSerializerFeatureName = forestFastjsonConverter.getSerializerFeatureName();
        SerializerFeature defaultSerializerFeature = forestFastjsonConverter.getSerializerFeature();
        assertEquals(SerializerFeature.DisableCircularReferenceDetect.name(),
                defaultSerializerFeatureName);
        assertEquals(defaultSerializerFeature.name(),
                defaultSerializerFeatureName);

        forestFastjsonConverter.setSerializerFeatureName(SerializerFeature.WriteClassName.name());
        assertEquals(SerializerFeature.WriteClassName.name(),
                forestFastjsonConverter.getSerializerFeatureName());
        assertEquals(SerializerFeature.WriteClassName,
                forestFastjsonConverter.getSerializerFeature());

        forestFastjsonConverter.setSerializerFeature(SerializerFeature.BeanToArray);
        assertEquals(SerializerFeature.BeanToArray.name(),
                forestFastjsonConverter.getSerializerFeatureName());
        assertEquals(SerializerFeature.BeanToArray,
                forestFastjsonConverter.getSerializerFeature());
    }

    @Test
    public void testConvertToJson() {
        ForestFastjsonConverter forestFastjsonConverter = new ForestFastjsonConverter();
        String text = forestFastjsonConverter.convertToJson(new Integer[] {100, 10});
        assertEquals("[100,10]", text);
    }

    @Test
    public void testConvertToJava() {
        String jsonText = "{\"a\":1, \"b\":2}";
        ForestFastjsonConverter forestFastjsonConverter = new ForestFastjsonConverter();
        Map result = forestFastjsonConverter.convertToJavaObject(jsonText, Map.class);
        assertNotNull(result);
        assertEquals(1, result.get("a"));
        assertEquals(2, result.get("b"));

        result = forestFastjsonConverter.convertToJavaObject(jsonText, new TypeReference<Map>() {}.getType());
        assertNotNull(result);
        assertEquals(1, result.get("a"));
        assertEquals(2, result.get("b"));

        result = forestFastjsonConverter.convertToJavaObject(jsonText, new TypeReference<Map>() {});
        assertNotNull(result);
        assertEquals(1, result.get("a"));
        assertEquals(2, result.get("b"));

    }

    @Test
    public void testConvertToJavaError() {
        String badJsonText = "{\"a\":1";
        ForestFastjsonConverter forestFastjsonConverter = new ForestFastjsonConverter();
        boolean error = false;
        try {
            forestFastjsonConverter.convertToJavaObject(badJsonText, Map.class);
        } catch (ForestRuntimeException e) {
            error = true;
            assertNotNull(e.getCause());
        }
        assertTrue(error);

        error = true;
        try {
            forestFastjsonConverter.convertToJavaObject(badJsonText, new TypeReference<Map>() {}.getType());
        } catch (ForestRuntimeException e) {
            error = true;
            assertNotNull(e.getCause());
        }
        assertTrue(error);

        error = true;
        try {
            forestFastjsonConverter.convertToJavaObject(badJsonText, new TypeReference<Map>() {});
        } catch (ForestRuntimeException e) {
            error = true;
            assertNotNull(e.getCause());
        }
        assertTrue(error);

    }

}
