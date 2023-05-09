package org.dromara.forest.core.test.http;

import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.utils.URLUtils;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 16:58
 */
public class TestURL {

    @Test
    public void testNormalURL() {
        String url = "http://www.xxx.com/cc/yy";
        String newURL = URLUtils.getValidURL(null, url);
        assertEquals(url, newURL);
        newURL = URLUtils.getValidURL("", url);
        assertEquals(url, newURL);
        newURL = URLUtils.getValidURL("  ", url);
        assertEquals(url, newURL);
    }


    @Test
    public void checkBaseURL() {
        boolean exception = false;
        try {
            URLUtils.checkBaseURL("http://www.xxx.com");
        } catch (ForestRuntimeException e) {
            exception = true;
        }
        assertFalse(exception);

        try {
            URLUtils.checkBaseURL("www.xxx.com");
        } catch (ForestRuntimeException e) {
            exception = true;
        }
        assertTrue(exception);

    }

    @Test
    public void testBaseURL() {
        String expected = "http://www.xxx.com/cc/yy";
        String baseUrl = "http://www.xxx.com";
        String uri = "/cc/yy";

        String newURL = URLUtils.getValidURL(baseUrl, uri);
        assertEquals(expected, newURL);

        baseUrl = "http://www.xxx.com";
        uri = "/cc/yy";

        newURL = URLUtils.getValidURL(baseUrl, uri);
        assertEquals(expected, newURL);

        baseUrl = "http://www.xxx.com/";
        uri = "/cc/yy";

        newURL = URLUtils.getValidURL(baseUrl, uri);
        assertEquals(expected, newURL);

        baseUrl = "http://www.xxx.com";
        uri = "cc/yy";

        newURL = URLUtils.getValidURL(baseUrl, uri);
        assertEquals(expected, newURL);

        newURL = URLUtils.getValidURL("www.xxx.com", uri);
        assertEquals(expected, newURL);

    }

}
