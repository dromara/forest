package com.dtflys.test.misc;

import com.dtflys.forest.utils.URLUtils;
import junit.framework.TestCase;

public class URLTest extends TestCase {

    public void testUrl() {
        assertEquals("http://www.baidu.com", URLUtils.getValidURL("http://www.baidu.com", ""));
        assertEquals("http://www.baidu.com/xxx", URLUtils.getValidURL("http://www.baidu.com", "xxx"));
        assertEquals("http://www.baidu.com/xxx", URLUtils.getValidURL("http://www.baidu.com/", "xxx"));
        assertEquals("http://www.baidu.com/xxx", URLUtils.getValidURL("http://www.baidu.com/", "/xxx"));
        assertEquals("http://www.baidu.com/xxx", URLUtils.getValidURL("http://www.baidu.com", "/xxx"));

        assertEquals("http://www.baidu.com", URLUtils.getValidURL("www.baidu.com", ""));
        assertEquals("http://www.baidu.com/xxx", URLUtils.getValidURL("www.baidu.com", "xxx"));
        assertEquals("http://www.baidu.com/xxx", URLUtils.getValidURL("www.baidu.com/", "xxx"));
        assertEquals("http://www.baidu.com/xxx", URLUtils.getValidURL("www.baidu.com/", "/xxx"));
        assertEquals("http://www.baidu.com/xxx", URLUtils.getValidURL("www.baidu.com", "/xxx"));


        assertEquals("http://www.baidu.com", URLUtils.getValidURL("http://www.baidu.com", "http://www.baidu.com"));
        assertEquals("http://www.baidu.com", URLUtils.getValidURL("www.baidu.com", "http://www.baidu.com"));
        assertEquals("http://www.baidu.com", URLUtils.getValidURL("google.com", "http://www.baidu.com"));
        assertEquals("http://www.baidu.com", URLUtils.getValidURL("xxx", "http://www.baidu.com"));

    }
}
