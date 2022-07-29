package com.dtflys.test.misc;

import com.dtflys.forest.http.ForestCookie;
import com.dtflys.forest.http.ForestHeaderMap;
import com.dtflys.forest.http.ForestURL;
import com.dtflys.forest.http.HasURL;
import junit.framework.TestCase;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;

public class HeaderTest extends TestCase {

    public void testHeaders() {
        ForestHeaderMap headers = new ForestHeaderMap(null);
        headers.setHeader("Content-Type", "application/json");
        assertEquals("application/json", headers.getValue("content-type"));
        headers.setHeader("content-type", "application/xml");
        assertEquals("application/xml", headers.getValue("content-type"));
        headers.addHeader("Content-Length", "0");
        assertEquals(2, headers.size());
        headers.addHeader("Content-Length", "3");
        List<String> values = headers.getValues("content-length");
        assertEquals(2, values.size());
        assertEquals("0", values.get(0));
        assertEquals("3", values.get(1));
    }

    /**
     * 测试添加cookie
     *
     * @author yangle94
     */
    @Test
    public void testAddCookie() {
        HasURL hasURL = () -> {
            try {
                return new ForestURL(new URL("https://baidu.com"));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        };
        ForestHeaderMap forestHeaderMap = new ForestHeaderMap(hasURL);

        Duration maxAge = Duration.ofSeconds(10L);
        String url = "http://forest.dtflyx.com/docs";
        String setCookie = "foo=bar; max-age=" + maxAge.getSeconds();
        ForestCookie cookie = ForestCookie.parse(url, setCookie);

        forestHeaderMap.addCookie(cookie);

        assertFalse(forestHeaderMap.containsKey("Cookie"));


        String url1 = "https://baidu.com";
        String setCookie1 = "foo=bar; max-age=" + maxAge.getSeconds();
        ForestCookie cookie1 = ForestCookie.parse(url1, setCookie1);
        forestHeaderMap.addCookie(cookie1);

        assertTrue(forestHeaderMap.containsKey("Cookie"));
    }
}
