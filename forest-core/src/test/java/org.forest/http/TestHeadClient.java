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

import static junit.framework.Assert.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 17:50
 */
public class TestHeadClient {

    private final static Logger log = LoggerFactory.getLogger(TestHeadClient.class);

    @Rule
    public MockServerRule server = new MockServerRule(this, 5000);

    private static ForestConfiguration configuration;

    private static HeadClient headClient;

    private final static String expected = "{\"status\": \"ok\"}";

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        headClient = configuration.createInstance(HeadClient.class);
    }


    @Before
    public void prepareMockServer() {
        MockServerClient mockClient = new MockServerClient("localhost", 5000);
        mockClient.when(
                request()
                        .withPath("/hello/user")
                        .withMethod("HEAD")
                        .withHeader(new Header(HttpHeaders.ACCEPT, "text/plan"))
                        .withQueryStringParameter("username", "foo")
        ).respond(
                response()
                        .withStatusCode(200)
        );
    }


    @Test
    public void testSimpleHead() {
        headClient.simpleHead();
    }


    @Test
    public void testResponseHead() {
        ForestResponse response = headClient.responseHead();
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertTrue(response.isSuccess());
        assertFalse(response.isError());
    }

}
