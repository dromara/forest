package org.dromara.forest.test.http;

import org.dromara.forest.backend.HttpBackend;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.http.ForestResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.http.HttpHeaders;
import org.dromara.forest.test.http.client.OptionsClient;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static org.dromara.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 18:26
 */
public class TestOptionsClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private static OptionsClient optionsClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    @Override
    public void afterRequests() {
    }

    public TestOptionsClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        optionsClient = configuration.createInstance(OptionsClient.class);
    }


    @Test
    public void testSimpleOptions() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(optionsClient.simpleOptions())
            .isNotNull()
            .extracting(ForestResponse::getStatusCode, ForestResponse::getContent)
            .contains(200, EXPECTED);
        mockRequest(server)
                .assertMethodEquals("OPTIONS")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals(HttpHeaders.ACCEPT, "text/plain")
                .assertQueryEquals("username", "foo");
    }

    @Test
    public void testSimpleOptions2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(optionsClient.simpleOptions2())
                .isNotNull()
                .extracting(ForestResponse::getStatusCode, ForestResponse::getContent)
                .contains(200, EXPECTED);
        mockRequest(server)
                .assertMethodEquals("OPTIONS")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals(HttpHeaders.ACCEPT, "text/plain")
                .assertQueryEquals("username", "foo");
    }

    @Test
    public void testSimpleOptions3() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(optionsClient.simpleOptions3())
                .isNotNull()
                .extracting(ForestResponse::getStatusCode, ForestResponse::getContent)
                .contains(200, EXPECTED);
        mockRequest(server)
                .assertMethodEquals("OPTIONS")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals(HttpHeaders.ACCEPT, "text/plain")
                .assertQueryEquals("username", "foo");
    }



    @Test
    public void testTextParamOptions() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(optionsClient.textParamOptions("foo"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("OPTIONS")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals(HttpHeaders.ACCEPT, "text/plain")
                .assertQueryEquals("username", "foo");

    }

    @Test
    public void testAnnParamOptions() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(optionsClient.annParamOptions("foo"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("OPTIONS")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals(HttpHeaders.ACCEPT, "text/plain")
                .assertQueryEquals("username", "foo");

    }

}
