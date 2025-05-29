package com.dtflys.forest.test.converter;

import com.dtflys.forest.test.http.model.Cause;
import com.dtflys.forest.test.http.model.FormListParam;
import com.dtflys.forest.test.model.Coordinate;
import com.dtflys.forest.test.model.SubCoordinate;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import junit.framework.Assert;
import com.dtflys.forest.converter.json.ForestGsonConverter;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-18 13:53
 */
public class TestGsonConverter extends JSONConverter {

    @Test
    public void testConvertToJson() {
        ForestGsonConverter gsonConverter = new ForestGsonConverter();
        String text = gsonConverter.encodeToString(new Integer[] {100, 10});
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

    @Test
    public void testMapToJSONString() throws ParseException {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "foo");
        map.put("password", "bar");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = dateFormat.parse("2020-10-10 10:10:10");
        map.put("createDate", date);
        ForestGsonConverter forestGsonConverter = new ForestGsonConverter();
        forestGsonConverter.setDateFormat("yyyy/MM/dd hh:mm:ss");
        String jsonStr = forestGsonConverter.encodeToString(map);
        assertEquals("{\"name\":\"foo\",\"password\":\"bar\",\"createDate\":\"2020/10/10 10:10:10\"}", jsonStr);
    }


    @Test
    public void testJavaObjectToMap() {
        Coordinate coordinate = new Coordinate("11.11111", "22.22222");
        ForestGsonConverter gsonConverter = new ForestGsonConverter();
        Map map = gsonConverter.convertObjectToMap(coordinate);
        assertNotNull(map);
        assertEquals("11.11111", map.get("longitude"));
        assertEquals("22.22222", map.get("latitude"));
    }

    @Test
    public void testJavaObjectToMap2() {
        SubCoordinate coordinate = new SubCoordinate("11.11111", "22.22222");
        ForestGsonConverter gsonConverter = new ForestGsonConverter();
        Map map = gsonConverter.convertObjectToMap(coordinate);
        assertNotNull(map);
        assertEquals("11.11111", map.get("longitude"));
        assertEquals("22.22222", map.get("latitude"));
    }

    @Test
    public void testJavaObjectToMap3() {
        FormListParam param = new FormListParam();
        List<Integer> idList = Lists.newArrayList(1, 2, 3);
        param.setUsername("foo");
        param.setPassword("123456");
        param.setIdList(idList);
        Cause cause1 = new Cause();
        cause1.setId(1);
        cause1.setScore(87);
        Cause cause2 = new Cause();
        cause2.setId(2);
        cause2.setScore(73);
        List<Cause> causes = Lists.newArrayList(cause1, cause2);
        param.setCause(causes);

        ForestGsonConverter gsonConverter = new ForestGsonConverter();
        Map map = gsonConverter.convertObjectToMap(param);
        assertEquals("foo", map.get("username"));
        assertEquals("123456", map.get("password"));
        assertEquals(idList, map.get("idList"));
//        assertThat(map.get("cause")).isEqualTo(causes);
//        assertEquals(causes, map.get("cause"));
    }



    @Test
    public void testDate() throws ParseException {
        ForestGsonConverter gsonConverter = new ForestGsonConverter();
        String json = "{\"name\":\"foo\",\"date\":\"2020-10-10 10:12:00\"}";
        gsonConverter.setDateFormat("yyyy-MM-dd hh:mm:ss");
        TestJsonObj testJsonObj = gsonConverter.convertToJavaObject(json, TestJsonObj.class);
        assertNotNull(testJsonObj);
        assertEquals("foo", testJsonObj.getName());
        assertDateEquals("2020-10-10 10:12:00", testJsonObj.getDate(), "yyyy-MM-dd hh:mm:ss");

        json = "{\"name\":\"foo\",\"date\":\"2020/10/10 10:12:00\"}";
        gsonConverter.setDateFormat("yyyy/MM/dd hh:mm:ss");
        testJsonObj = gsonConverter.convertToJavaObject(json, TestJsonObj.class);
        assertNotNull(testJsonObj);
        assertEquals("foo", testJsonObj.getName());
        assertDateEquals("2020-10-10 10:12:00", testJsonObj.getDate(), "yyyy-MM-dd hh:mm:ss");

    }


}
