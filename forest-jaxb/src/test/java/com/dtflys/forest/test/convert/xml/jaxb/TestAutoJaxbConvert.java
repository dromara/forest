package com.dtflys.forest.test.convert.xml.jaxb;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.auto.DefaultAutoConverter;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.TypeReference;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

public class TestAutoJaxbConvert {

    private DefaultAutoConverter getConverter() {
        ForestConfiguration configuration = ForestConfiguration.createConfiguration();
        DefaultAutoConverter autoConverter = (DefaultAutoConverter) configuration.getConverterMap().get(ForestDataType.AUTO);
        assertNotNull(autoConverter);
        return autoConverter;
    }

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

}
