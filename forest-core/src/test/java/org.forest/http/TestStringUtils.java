package org.forest.http;

import org.forest.utils.StringUtils;
import org.junit.Test;

import java.lang.reflect.Method;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-18 15:48
 */
public class TestStringUtils {

    @Test
    public void testIsEmpty() {
        String s1 = null;
        String s2 = "";
        String s3 = "a";
        String s4 = " ";

        assertTrue(StringUtils.isEmpty(s1));
        assertTrue(StringUtils.isEmpty(s2));
        assertFalse(StringUtils.isEmpty(s3));
        assertFalse(StringUtils.isEmpty(s4));

        assertFalse(StringUtils.isNotEmpty(s1));
        assertFalse(StringUtils.isNotEmpty(s2));
        assertTrue(StringUtils.isNotEmpty(s3));
        assertTrue(StringUtils.isNotEmpty(s4));
    }

    public static class Getter {
        public String getValue() {
            return "";
        }
        public boolean isExist() {
            return true;
        }
    }

    @Test
    public void testGetGetterName() throws NoSuchMethodException {
        Method method1 = Getter.class.getMethod("getValue");
        Method method2 = Getter.class.getMethod("isExist");
        assertEquals("value", StringUtils.getGetterName(method1));
        assertEquals("exist", StringUtils.getGetterName(method2));
        assertEquals("getValue", StringUtils.toGetterName("value"));
    }


}
