package org.dromara.forest.test.http;

import org.dromara.forest.utils.StringUtil;
import org.junit.Test;

import java.lang.reflect.Method;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-18 15:48
 */
public class TestStringUtil {

    @Test
    public void testIsEmpty() throws InterruptedException {
        String s1 = null;
        String s2 = "";
        String s3 = "a";
        String s4 = " ";

        assertTrue(StringUtil.isEmpty(s1));
        assertTrue(StringUtil.isEmpty(s2));
        assertFalse(StringUtil.isEmpty(s3));
        assertFalse(StringUtil.isEmpty(s4));

        assertFalse(StringUtil.isNotEmpty(s1));
        assertFalse(StringUtil.isNotEmpty(s2));
        assertTrue(StringUtil.isNotEmpty(s3));
        assertTrue(StringUtil.isNotEmpty(s4));
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
        assertEquals("value", StringUtil.getGetterName(method1));
        assertEquals("exist", StringUtil.getGetterName(method2));
        assertEquals("getValue", StringUtil.toGetterName("value"));
    }


}
