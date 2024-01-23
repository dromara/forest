package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.client.TraceClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 18:26
 */
public class TestTraceClient extends BaseClientTest {

    @Rule
    public MockWebServer server = new MockWebServer();

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    private static ForestConfiguration configuration;

    private static TraceClient traceClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    @Override
    public void afterRequests() {
    }

    public TestTraceClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVar("port", server.getPort());
        traceClient = configuration.createInstance(TraceClient.class);
    }

    @Test
    public void testSimpleOptions() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(traceClient.simpleTrace())
                .isNotNull()
                .extracting(ForestResponse::getStatusCode, ForestResponse::getContent)
                .contains(200, EXPECTED);
        mockRequest(server)
                .assertMethodEquals("TRACE")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Accept", "text/plain")
                .assertQueryEquals("username", "foo");
    }

    @Test
    public void testSimpleOptions2() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(traceClient.simpleTrace2())
                .isNotNull()
                .extracting(ForestResponse::getStatusCode, ForestResponse::getContent)
                .contains(200, EXPECTED);
        mockRequest(server)
                .assertMethodEquals("TRACE")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Accept", "text/plain")
                .assertQueryEquals("username", "foo");
    }

    @Test
    public void testSimpleOptions3() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(traceClient.simpleTrace3())
                .isNotNull()
                .extracting(ForestResponse::getStatusCode, ForestResponse::getContent)
                .contains(200, EXPECTED);
        mockRequest(server)
                .assertMethodEquals("TRACE")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Accept", "text/plain")
                .assertQueryEquals("username", "foo");
    }

    @Test
    public void testTextParamOptions() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(traceClient.textParamTrace("foo"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("TRACE")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Accept", "text/plain")
                .assertQueryEquals("username", "foo");
    }

    @Test
    public void testAnnParamOptions() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(traceClient.annParamTrace("foo"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("TRACE")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Accept", "text/plain")
                .assertQueryEquals("username", "foo");

    }

}
