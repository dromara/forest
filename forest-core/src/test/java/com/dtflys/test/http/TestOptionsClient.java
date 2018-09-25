package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import org.apache.http.HttpHeaders;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.client.OptionsClient;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 18:26
 */
public class TestOptionsClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestOptionsClient.class);

    @Rule
    public MockServerRule server = new MockServerRule(this, 5000);

    private static ForestConfiguration configuration;

    private static OptionsClient optionsClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
    }

    public TestOptionsClient(HttpBackend backend) {
        super(backend, configuration);
        optionsClient = configuration.createInstance(OptionsClient.class);
    }


    @Before
    public void prepareMockServer() {
        MockServerClient mockClient = new MockServerClient("localhost", 5000);
        mockClient.when(
                request()
                        .withPath("/hello/user")
                        .withMethod("OPTIONS")
                        .withHeader(new Header(HttpHeaders.ACCEPT, "text/plan"))
                        .withQueryStringParameter("username",  "foo")
        ).respond(
                response()
                        .withStatusCode(200)
        );
    }


    @Test
    public void testSimpleOptions() {
        ForestResponse response = optionsClient.simpleOptions();
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
    }


}
