package org.forest.http;

import org.apache.http.HttpHeaders;
import org.forest.config.ForestConfiguration;
import org.junit.*;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 17:13
 */
public class TestPutClient {

    private final static Logger log = LoggerFactory.getLogger(TestPutClient.class);

    @Rule
    public MockServerRule server = new MockServerRule(this, 5000);

    private static ForestConfiguration configuration;

    private static PutClient putClient;

    private final static String expected = "{\"status\": \"ok\"}";

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        putClient = configuration.createInstance(PutClient.class);
    }

    @Before
    public void prepareMockServer() {
        MockServerClient mockClient = new MockServerClient("localhost", 5000);
        mockClient.when(
                request()
                        .withPath("/hello")
                        .withMethod("PUT")
                        .withHeader(new Header(HttpHeaders.ACCEPT, "text/plan"))
                        .withBody("username=foo&password=123456")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(expected)
        );
    }

    @Test
    public void testSimplePut() {
        String result = putClient.simplePut();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    public void testTextParamPut() {
        String result = putClient.textParamPut("foo", "123456");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    public void testAnnParamPut() {
        String result = putClient.annParamPut("foo", "123456");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(expected, result);
    }


}
