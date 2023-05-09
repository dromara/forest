package org.dromara.forest.test.convert.xml.jakartaxml;


import org.dromara.forest.converter.xml.jakartaxml.ForestJakartaXmlConverter;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.utils.TypeReference;
import org.junit.Test;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class TestJakartaXml {

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
        ForestJakartaXmlConverter forestJaxbConverter = new ForestJakartaXmlConverter();
        String xmlText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<user>\n" +
                "<name>Peter</name>\n" +
                "<age>32</age>\n" +
                "</user>";
        User user = forestJaxbConverter.convertToJavaObject(xmlText, User.class);
        assertNotNull(user);
        assertEquals("Peter", user.getName());
        assertEquals(Integer.valueOf(32), user.getAge());

        user = forestJaxbConverter.convertToJavaObject(xmlText, new TypeReference<User>() {}.getType());
        assertNotNull(user);
        assertEquals("Peter", user.getName());
        assertEquals(Integer.valueOf(32), user.getAge());
    }

    @Test
    public void convertToJavaObjectError() {
        ForestJakartaXmlConverter forestJaxbConverter = new ForestJakartaXmlConverter();
        String xmlText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<user>\n" +
                "<name>Peter</name>\n" +
                "<age>32</age>\n" +
                "</user";
        boolean error = false;
        try {
            forestJaxbConverter.convertToJavaObject(xmlText, User.class);
        } catch (ForestRuntimeException e) {
            error = true;
            assertNotNull(e.getCause());
        }
        assertTrue(error);
    }


    @Test
    public void testConvertToXml() {
        User user = new User();
        user.setName("Peter");
        user.setAge(32);

        ForestJakartaXmlConverter forestJaxbConverter = new ForestJakartaXmlConverter();
        String xml = forestJaxbConverter.encodeToString(user);
        assertNotNull(xml);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<user>\n" +
                "    <name>Peter</name>\n" +
                "    <age>32</age>\n" +
                "</user>\n", xml);
    }


    public static class BadUser {
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
    public void testConvertToXmlError() {
        BadUser user = new BadUser();
        user.setName("Peter");
        user.setAge(32);

        ForestJakartaXmlConverter forestJaxbConverter = new ForestJakartaXmlConverter();
        boolean error = false;
        try {
            forestJaxbConverter.encodeToString(user);
        } catch (ForestRuntimeException e) {
            error = true;
        }
        assertTrue(error);
    }


}
