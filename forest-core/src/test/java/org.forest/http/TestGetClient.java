package org.forest.http;

import org.apache.http.HttpHeaders;
import org.forest.config.ForestConfiguration;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 15:02
 */
public class TestGetClient {

    private final static Logger log = LoggerFactory.getLogger(TestGetClient.class);

    @Rule
    public MockServerRule server = new MockServerRule(this, 5000);

    private static ForestConfiguration configuration;

    private static GetClient getClient;

    private final static String expected = "{\"status\": \"ok\"}";

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        getClient = configuration.createInstance(GetClient.class);
    }


    @Before
    public void prepareMockServer() {

        MockServerClient mockClient = new MockServerClient("localhost", 5000);
        mockClient.when(
                request()
                        .withPath("/hello/foo")
                        .withMethod("GET")
                        .withHeader(new Header(HttpHeaders.ACCEPT, "text/plan"))
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(expected)
        );
    }

    @Test
    public void testGet() {
        String result = getClient.simpleGet();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(expected, result);
    }


    @Test
    public void testJsonMapGet() {
        Map map = getClient.jsonMapGet();
        assertNotNull(map);
        assertEquals("ok", map.get("status"));
    }

}
