package com.dtflys.test.misc;

import com.dtflys.forest.http.ForestHeaderMap;
import junit.framework.TestCase;

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

}
