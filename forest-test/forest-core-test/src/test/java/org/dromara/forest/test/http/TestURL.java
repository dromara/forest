package org.dromara.forest.test.http;

import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.utils.URLUtil;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 16:58
 */
public class TestURL {

    @Test
    public void testNormalURL() {
        String url = "http://www.xxx.com/cc/yy";
        String newURL = URLUtil.getValidURL(null, url);
        assertEquals(url, newURL);
        newURL = URLUtil.getValidURL("", url);
        assertEquals(url, newURL);
        newURL = URLUtil.getValidURL("  ", url);
        assertEquals(url, newURL);
    }


    @Test
    public void checkBaseURL() {
        boolean exception = false;
        try {
            URLUtil.checkBaseURL("http://www.xxx.com");
        } catch (ForestRuntimeException e) {
            exception = true;
        }
        assertFalse(exception);

        try {
            URLUtil.checkBaseURL("www.xxx.com");
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

        String newURL = URLUtil.getValidURL(baseUrl, uri);
        assertEquals(expected, newURL);

        baseUrl = "http://www.xxx.com";
        uri = "/cc/yy";

        newURL = URLUtil.getValidURL(baseUrl, uri);
        assertEquals(expected, newURL);

        baseUrl = "http://www.xxx.com/";
        uri = "/cc/yy";

        newURL = URLUtil.getValidURL(baseUrl, uri);
        assertEquals(expected, newURL);

        baseUrl = "http://www.xxx.com";
        uri = "cc/yy";

        newURL = URLUtil.getValidURL(baseUrl, uri);
        assertEquals(expected, newURL);

        newURL = URLUtil.getValidURL("www.xxx.com", uri);
        assertEquals(expected, newURL);

    }

}
