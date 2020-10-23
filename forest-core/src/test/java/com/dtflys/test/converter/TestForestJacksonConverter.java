package com.dtflys.test.converter;

import com.dtflys.forest.converter.json.ForestFastjsonConverter;
import com.dtflys.forest.utils.ReflectUtils;
import com.dtflys.test.http.model.Cause;
import com.dtflys.test.http.model.FormListParam;
import com.dtflys.test.model.Coordinate;
import com.dtflys.test.model.SubCoordinate;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import junit.framework.Assert;
import com.dtflys.forest.converter.json.ForestJacksonConverter;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
        String text = forestJacksonConverter.encodeToString(new Integer[] {100, 10});
        Assert.assertEquals("[100,10]", text);
    }

    @Test
    public void testConvertToJsonError() {
        ForestJacksonConverter forestJacksonConverter = new ForestJacksonConverter();
        Map map = new HashMap();
        map.put("ref", map);

        boolean error = false;
        try {
            forestJacksonConverter.encodeToString(map);
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

    @Test
    public void testMapToJSONString() throws ParseException {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "foo");
        map.put("password", "bar");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = dateFormat.parse("2020-10-10 10:10:10");
        map.put("createDate", date);
        ForestJacksonConverter forestJacksonConverter = new ForestJacksonConverter();
        forestJacksonConverter.setDateFormat("yyyy/MM/dd hh:mm:ss");
        String jsonStr = forestJacksonConverter.encodeToString(map);
        assertEquals("{\"name\":\"foo\",\"password\":\"bar\",\"createDate\":\"2020/10/10 10:10:10\"}", jsonStr);
    }


    @Test
    public void testJavaObjectToMap() {
        Coordinate coordinate = new Coordinate("11.11111", "22.22222");
        ForestJacksonConverter forestJacksonConverter = new ForestJacksonConverter();
        Map map = forestJacksonConverter.convertObjectToMap(coordinate);
        assertNotNull(map);
        assertEquals("11.11111", map.get("longitude"));
        assertEquals("22.22222", map.get("latitude"));
    }

    @Test
    public void testJavaObjectToMap2() {
        SubCoordinate coordinate = new SubCoordinate("11.11111", "22.22222");
        ForestJacksonConverter forestJacksonConverter = new ForestJacksonConverter();
        Map map = forestJacksonConverter.convertObjectToMap(coordinate);
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

        Map map = ReflectUtils.convertObjectToMap(param);
        assertEquals("foo", map.get("username"));
        assertEquals("123456", map.get("password"));
        assertEquals(idList, map.get("idList"));
        assertEquals(causes, map.get("cause"));
    }


}
