package org.dromara.forest.core.test.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import org.dromara.forest.converter.json.ForestFastjsonConverter;
import org.dromara.forest.core.test.http.model.Cause;
import org.dromara.forest.core.test.http.model.FormListParam;
import org.dromara.forest.core.test.model.Coordinate;
import org.dromara.forest.core.test.model.SubCoordinate;
import org.dromara.forest.exceptions.ForestRuntimeException;
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
 * @since 2017-05-08 23:13
 */
public class TestForestFastjsonConverter extends JSONConverter {

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

    public static class TestObj {
        @JSONField(ordinal = 1)
        private int id;

        @JSONField(ordinal = 2)
        private int direction;

        @JSONField(ordinal = 3)
        private String type;

        @JSONField(ordinal = 5)
        private byte crc;

        @JSONField(ordinal = 4)
        private Object body;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getDirection() {
            return direction;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public byte getCrc() {
            return crc;
        }

        public void setCrc(byte crc) {
            this.crc = crc;
        }

        public Object getBody() {
            return body;
        }

        public void setBody(Object body) {
            this.body = body;
        }

    }

    @Test
    public void testConvertToMap() {
        TestObj testObj = new TestObj();
        testObj.setId(1);
        testObj.setBody("xxx");
        testObj.setCrc(new Byte("1"));
        testObj.setDirection(4);
        testObj.setType("yyy");
        ForestFastjsonConverter forestFastjsonConverter = new ForestFastjsonConverter();
        Map<String, Object> map = forestFastjsonConverter.convertObjectToMap(testObj);
        assertEquals("{\"id\":1,\"direction\":4,\"type\":\"yyy\",\"body\":\"xxx\",\"crc\":1}", JSON.toJSONString(map));
    }

    @Test
    public void testConvertToJson() {

        ForestFastjsonConverter forestFastjsonConverter = new ForestFastjsonConverter();
        String text = forestFastjsonConverter.encodeToString(new Integer[] {100, 10});
        assertEquals("[100,10]", text);

        Map map = new LinkedHashMap();
        map.put("a", 1);
        text = forestFastjsonConverter.encodeToString(map);
        assertEquals("{\"a\":1}", text);

        Map sub = new LinkedHashMap();
        sub.put("x", 0);
        map.put("s1", sub);
        map.put("s2", sub);
        text = forestFastjsonConverter.encodeToString(map);
        assertEquals("{\"a\":1,\"s1\":{\"x\":0},\"s2\":{\"x\":0}}", text);

        forestFastjsonConverter.setSerializerFeature(null);
        text = forestFastjsonConverter.encodeToString(new Integer[] {100, 10});
        assertEquals("[100,10]", text);
    }

    @Test
    public void testConvertToJsonError() {
        ForestFastjsonConverter forestFastjsonConverter = new ForestFastjsonConverter();
        Map map = new HashMap();
        map.put("ref", map);

        boolean error = false;
        try {
            forestFastjsonConverter.encodeToString(map);
        } catch (ForestRuntimeException e) {
            error = true;
            assertNotNull(e.getCause());
        }
        assertTrue(error);
    }



    @Test
    public void testConvertToJava() throws ParseException {
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


    @Test
    public void testMapToJSONString() throws ParseException {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "foo");
        map.put("password", "bar");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = dateFormat.parse("2020-10-10 10:10:10");
        map.put("createDate", date);
        ForestFastjsonConverter fastjsonConverter = new ForestFastjsonConverter();
        fastjsonConverter.setDateFormat("yyyy/MM/dd hh:mm:ss");
        String jsonStr = fastjsonConverter.encodeToString(map);
        assertEquals("{\"name\":\"foo\",\"password\":\"bar\",\"createDate\":\"2020/10/10 10:10:10\"}", jsonStr);
    }


    @Test
    public void testJavaObjectToMap() {
        Coordinate coordinate = new Coordinate("11.11111", "22.22222");
        ForestFastjsonConverter forestFastjsonConverter = new ForestFastjsonConverter();
        Map map = forestFastjsonConverter.convertObjectToMap(coordinate);
        assertNotNull(map);
        assertEquals("11.11111", map.get("longitude"));
        assertEquals("22.22222", map.get("latitude"));
    }

    @Test
    public void testJavaObjectToMap2() {
        SubCoordinate coordinate = new SubCoordinate("11.11111", "22.22222");
        ForestFastjsonConverter forestFastjsonConverter = new ForestFastjsonConverter();
        Map map = forestFastjsonConverter.convertObjectToMap(coordinate);
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

        ForestFastjsonConverter forestFastjsonConverter = new ForestFastjsonConverter();
        Map map = forestFastjsonConverter.convertObjectToMap(param);
        assertEquals("foo", map.get("username"));
        assertEquals("123456", map.get("password"));
        assertEquals(idList, map.get("idList"));
        assertEquals(causes, map.get("cause"));
    }


    @Test
    public void testDeepMap() {
        String json = "{\"code\":\"000000\",\"msg\":\"success\",\"timestamp\":\"2020-08-26T12:06:55.498Z\",\"data\":[{\"id\":8,\"channelCode\":\"UMS_MINI_PAY_WEIXIN\",\"channelName\":\"银联商务小程序微信支付\",\"channelParty\":\"1\",\"terminalType\":3,\"payType\":2,\"logoUrl\":null}]}";
        ForestFastjsonConverter forestFastjsonConverter = new ForestFastjsonConverter();
        Map map = forestFastjsonConverter.convertToJavaObject(json, HashMap.class);
        System.out.println(map);
    }

    @Test
    public void testDate() throws ParseException {
        String json = "{\"name\":\"foo\",\"date\":\"2020-10-10 10:12:00\"}";
        ForestFastjsonConverter forestFastjsonConverter = new ForestFastjsonConverter();
        TestJsonObj testJsonObj = forestFastjsonConverter.convertToJavaObject(json, TestJsonObj.class);
        assertNotNull(testJsonObj);
        assertEquals("foo", testJsonObj.getName());
        assertDateEquals("2020-10-10 10:12:00", testJsonObj.getDate(), "yyyy-MM-dd hh:mm:ss");

        json = "{\"name\":\"foo\",\"date\":\"2020/10/10 10:12:00\"}";
        testJsonObj = forestFastjsonConverter.convertToJavaObject(json, TestJsonObj.class);
        assertNotNull(testJsonObj);
        assertEquals("foo", testJsonObj.getName());
        assertDateEquals("2020-10-10 10:12:00", testJsonObj.getDate(), "yyyy-MM-dd hh:mm:ss");
    }
}
