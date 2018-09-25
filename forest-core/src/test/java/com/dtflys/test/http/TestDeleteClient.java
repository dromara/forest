package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import org.apache.http.HttpHeaders;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.backend.okhttp3.OkHttp3Backend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.DeleteClient;
import com.dtflys.test.http.client.GetClient;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
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
 * @since 2017-05-11 17:24
 */
public class TestDeleteClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestDeleteClient.class);

    @Rule
    public MockServerRule server = new MockServerRule(this, 5000);

    private static ForestConfiguration configuration;

    private static DeleteClient deleteClient;

    private final static String expected = "{\"status\": \"ok\"}";


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
    }

    public TestDeleteClient(HttpBackend backend) {
        super(backend, configuration);
        deleteClient = configuration.createInstance(DeleteClient.class);
    }

    @Before
    public void prepareMockServer() {
        MockServerClient mockClient = new MockServerClient("localhost", 5000);
        mockClient.when(
                request()
                        .withPath("/xx/user")
                        .withMethod("DELETE")
                        .withHeader(new Header(HttpHeaders.ACCEPT, "text/plan"))
                        .withQueryStringParameter("username", "foo")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(expected)
        );

        mockClient.when(
                request()
                        .withPath("/xx/user/data")
                        .withMethod("DELETE")
                        .withHeader(new Header(HttpHeaders.ACCEPT, "text/plan"))
                        .withBody("username=foo")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(expected)
        );

    }

    @Test
    public void testSimpleDelete() {
        String result = deleteClient.simpleDelete();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    public void testTextParamDelete() {
        String result = deleteClient.textParamDelete("foo");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    public void testAnnParamDelete() {
        String result = deleteClient.annParamDelete("foo");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(expected, result);
    }

}
