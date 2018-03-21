package org.forest.test.converter;

import com.google.gson.reflect.TypeToken;
import junit.framework.Assert;
import org.forest.converter.json.ForestGsonConverter;
import org.forest.converter.json.ForestJacksonConverter;
import org.forest.exceptions.ForestRuntimeException;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-18 13:53
 */
public class TestGsonConverter {

    @Test
    public void testConvertToJson() {
        ForestGsonConverter gsonConverter = new ForestGsonConverter();
        String text = gsonConverter.convertToJson(new Integer[] {100, 10});
        Assert.assertEquals("[100,10]", text);
    }


    public static class Data {
        private Integer a;
        private Integer b;
        public Integer getA() {
            return a;
        }
        public void setA(Integer a) {
            this.a = a;
        }
        public Integer getB() {
            return b;
        }
        public void setB(Integer b) {
            this.b = b;
        }
    }

    @Test
    public void testConvertToJava() {
        String jsonText = "{\"a\":1, \"b\":2}";
        ForestGsonConverter gsonConverter = new ForestGsonConverter();
        Map result = gsonConverter.convertToJavaObject(jsonText, Map.class);
        assertNotNull(result);
        assertEquals(1, result.get("a"));
        assertEquals(2, result.get("b"));

        result = gsonConverter.convertToJavaObject(jsonText, new TypeToken<Map>() {}.getType());
        assertNotNull(result);
        assertEquals(1, result.get("a"));
        assertEquals(2, result.get("b"));

        Data data = gsonConverter.convertToJavaObject(jsonText, Data.class);
        assertNotNull(data);
        assertEquals(Integer.valueOf(1), data.getA());
        assertEquals(Integer.valueOf(2), data.getB());


        jsonText = "[1, 2, 3]";
        List list = gsonConverter.convertToJavaObject(jsonText, List.class);
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));

        jsonText = "[1, 2, 3]";
        list = gsonConverter.convertToJavaObject(jsonText, new TypeToken<List>() {}.getType());
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));

        jsonText = "[1, 2, 3, {\"a\":1, \"b\":2}]";
        list = gsonConverter.convertToJavaObject(jsonText, List.class);
        assertNotNull(list);
        assertEquals(4, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
        Object obj = list.get(3);
        assertNotNull(obj);
        assertTrue(obj instanceof Map);
        Map map = (Map) obj;
        assertEquals(1, map.get("a"));
        assertEquals(2, map.get("b"));

    }


    @Test
    public void testConvertToJavaError() {
        String jsonText = "{\"a\":1, ";
        boolean error = false;
        ForestGsonConverter gsonConverter = new ForestGsonConverter();
        try {
            gsonConverter.convertToJavaObject(jsonText, Map.class);
        } catch (ForestRuntimeException e) {
            error = true;
            assertNotNull(e.getCause());
        }
        assertTrue(error);

        error = false;
        try {
            gsonConverter.convertToJavaObject(jsonText, new TypeToken<Map>() {}.getType());
        } catch (ForestRuntimeException e) {
            error = true;
            assertNotNull(e.getCause());
        }
        assertTrue(error);

        jsonText = "[1, 2,";
        try {
            gsonConverter.convertToJavaObject(jsonText, Map.class);
        } catch (ForestRuntimeException e) {
            error = true;
            assertNotNull(e.getCause());
        }
        assertTrue(error);
    }


}
