package org.dromara.test.converter;

import com.alibaba.fastjson.TypeReference;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.converter.auto.DefaultAutoConverter;
import org.dromara.forest.utils.ForestDataType;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-03 22:01
 */
public class TestAutoConverter {

    private DefaultAutoConverter getConverter() {
        ForestConfiguration configuration = ForestConfiguration.createConfiguration();
        DefaultAutoConverter autoConverter = (DefaultAutoConverter) configuration.getConverterMap().get(ForestDataType.AUTO);
        assertNotNull(autoConverter);
        return autoConverter;
    }

    @Test
    public void testAutoJsonObject() {
        DefaultAutoConverter autoConverter = getConverter();
        String text = "{\"username\": \"foo\", \"password\": \"bar\"}";
        Map<String, Object> map = autoConverter.convertToJavaObject(text, Map.class);
        assertNotNull(map);
        assertEquals("foo", map.get("username"));
        assertEquals("bar", map.get("password"));
    }

    @Test
    public void testAutoJsonArray() {
        DefaultAutoConverter autoConverter = getConverter();
        String text = "    [{\"username\": \"foo\", \"password\": \"bar\"}, {\"username\": \"xxx\", \"password\": \"yyy\"}] ";
        List<Map<String, Object>> list = autoConverter.convertToJavaObject(text, new TypeReference<List<Map<String, Object>>>() {}.getType());
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("foo", list.get(0).get("username"));
        assertEquals("bar", list.get(0).get("password"));
        assertEquals("xxx", list.get(1).get("username"));
        assertEquals("yyy", list.get(1).get("password"));
    }


/*
    @Test
    public void testAutoXml() {
        DefaultAutoConverter autoConverter = getConverter();
        String xmlText = "  <?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<user>\n" +
                "<name>Peter</name>\n" +
                "<age>32</age>\n" +
                "</user>";
        TestJaxbConverter.User user = autoConverter.convertToJavaObject(xmlText, TestJaxbConverter.User.class);
        assertNotNull(user);
        assertEquals("Peter", user.getName());
        assertEquals(Integer.valueOf(32), user.getAge());

        user = autoConverter.convertToJavaObject(xmlText, new TypeReference<TestJaxbConverter.User>() {}.getType());
        assertNotNull(user);
        assertEquals("Peter", user.getName());
        assertEquals(Integer.valueOf(32), user.getAge());
    }
*/

    @Test
    public void testAutoNumber() {
        DefaultAutoConverter autoConverter = getConverter();
        String expect = "12";
        Integer num1 = autoConverter.convertToJavaObject(expect, Integer.class);
        assertEquals(Integer.valueOf(12), num1);

        expect = "1.2";
        Float num2 = autoConverter.convertToJavaObject(expect, Float.class);
        assertEquals(Float.valueOf(1.2f), num2);

        expect = "2.22";
        Double num3 = autoConverter.convertToJavaObject(expect, Double.class);
        assertEquals(Double.valueOf(2.22D), num3);

        expect = "3.345";
        BigDecimal num4 = autoConverter.convertToJavaObject(expect, BigDecimal.class);
        assertEquals(new BigDecimal(expect), num4);
    }



    @Test
    public void testAutoText() {
        DefaultAutoConverter autoConverter = getConverter();
        String expect = "xxxxx";
        String text = autoConverter.convertToJavaObject(expect, String.class);
        assertEquals(expect, text);

        expect = "{{{{jljfelUF*(";
        text = autoConverter.convertToJavaObject(expect, String.class);
        assertEquals(expect, text);

        expect = "<div>xxx</div>";
        text = autoConverter.convertToJavaObject(expect, String.class);
        assertEquals(expect, text);

        expect = "[{\"username\": \"foo\", \"password\": \"bar\"}, {\"username\": \"xxx\", \"password\": \"yyy\"}]";
        text = autoConverter.convertToJavaObject(expect, String.class);
        assertEquals(expect, text);

        expect = "{\"username\": \"foo\", \"password\": \"bar\"}";
        text = autoConverter.convertToJavaObject(expect, String.class);
        assertEquals(expect, text);
    }

}
