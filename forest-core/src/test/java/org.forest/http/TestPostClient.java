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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 16:55
 */
public class TestPostClient {

    private final static Logger log = LoggerFactory.getLogger(TestPostClient.class);

    @Rule
    public MockServerRule server = new MockServerRule(this, 5000);

    private static ForestConfiguration configuration;

    private static PostClient postClient;

    private final static String expected = "{\"status\": \"ok\"}";

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        postClient = configuration.createInstance(PostClient.class);
    }


    @Before
    public void prepareMockServer() {
        MockServerClient mockClient = new MockServerClient("localhost", 5000);
        mockClient.when(
                request()
                        .withPath("/hello")
                        .withMethod("POST")
                        .withHeader(new Header(HttpHeaders.ACCEPT, "text/plan"))
                        .withBody("username=foo&password=123456")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(expected)
        );
    }

    @Test
    public void testSimplePost() {
        String result = postClient.simplePost();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    public void testTextParamPost() {
        String result = postClient.textParamPost("foo", "123456");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    public void testAnnParamPost() {
        String result = postClient.textParamPost("foo", "123456");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(expected, result);
    }


}
