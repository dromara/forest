package org.forest.converter;

import org.forest.converter.xml.ForestJaxbConverter;
import org.junit.Test;

import javax.xml.bind.annotation.*;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-18 15:01
 */
public class TestJaxbConverter {

    @XmlRootElement
    @XmlType(name = "user")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class User {
        @XmlElement(name="name")
        private String name;
        @XmlElement(name="age")
        private Integer age;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public Integer getAge() {
            return age;
        }
        public void setAge(Integer age) {
            this.age = age;
        }
    }

    @Test
    public void convertToJavaObject() {
        ForestJaxbConverter forestJaxbConverter = new ForestJaxbConverter();
        String xmlText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<user>\n" +
                "<name>Peter</name>\n" +
                "<age>32</age>\n" +
                "</user>";
        User user = forestJaxbConverter.convertToJavaObject(xmlText, User.class);
        assertNotNull(user);
        assertEquals("Peter", user.getName());
        assertEquals(Integer.valueOf(32), user.getAge());
    }

    @Test
    public void testConvertToXml() {
        User user = new User();
        user.setName("Peter");
        user.setAge(32);

        ForestJaxbConverter forestJaxbConverter = new ForestJaxbConverter();
        String xml = forestJaxbConverter.convertToXml(user);
        assertNotNull(xml);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<user>\n" +
                "    <name>Peter</name>\n" +
                "    <age>32</age>\n" +
                "</user>\n", xml);
    }

}
