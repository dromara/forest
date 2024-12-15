package com.dtflys.forest.test.misc;

import com.dtflys.forest.utils.NameUtils;
import junit.framework.TestCase;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-10 22:59
 */
public class NameTest extends TestCase {

    public void testSplitCamelName() {
        String name = "getUserName";
        String[] result = NameUtils.splitCamelName(name);
        assertEquals(3, result.length);
        assertEquals("get", result[0]);
        assertEquals("user", result[1]);
        assertEquals("name", result[2]);

        name = "postUser";
        result = NameUtils.splitCamelName(name);
        assertEquals(2, result.length);
        assertEquals("post", result[0]);
        assertEquals("user", result[1]);

    }
}
