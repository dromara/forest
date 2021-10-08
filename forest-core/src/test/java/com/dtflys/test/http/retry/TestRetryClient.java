package com.dtflys.test.http.retry;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.test.http.BaseClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestRetryClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"error\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private final RetryClient retryClient;

    private final RetryClient2 retryClient2;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }


    public TestRetryClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        retryClient = configuration.createInstance(RetryClient.class);
        retryClient2 = configuration.createInstance(RetryClient2.class);
    }

    @Test
    public void testRetryRequest() {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        ForestRequest<String> request = retryClient.testRetryRequest(3, 10);
        assertThat(request).isNotNull();
        assertThat(request.getRetryCount()).isEqualTo(3);
        assertThat(request.getMaxRetryInterval()).isEqualTo(10);
        String ret = request.execute(String.class);
        assertThat(ret).isNotNull().isEqualTo(EXPECTED);
        assertThat(request.getCurrentRetryCount()).isEqualTo(3);
        assertThat(request.getAttachment("retry-interceptor")).isNotNull().isEqualTo(3);
        mockRequest(server).assertQueryEquals(null);
        mockRequest(server).assertQueryEquals("a=1");
        mockRequest(server).assertQueryEquals("a=1&a=2");
        mockRequest(server).assertQueryEquals("a=1&a=2&a=3");
    }

    @Test
    public void testRetry() {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        AtomicInteger count = new AtomicInteger(0);
        String ret = retryClient.testRetry(3, 10, ((data, req, res) -> {
            count.incrementAndGet();
            assertThat(req.getCurrentRetryCount()).isEqualTo(3);
            assertThat(req.getAttachment("retry-interceptor")).isNotNull().isEqualTo(3);
        }));
        assertThat(ret).isNotNull().isEqualTo(EXPECTED);
        assertThat(count.get()).isEqualTo(1);
    }

    @Test
    public void testRetry_base() {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        AtomicInteger count = new AtomicInteger(0);
        String ret = retryClient2.testRetry(3, 10, ((data, req, res) -> {
            count.incrementAndGet();
            assertThat(req.getCurrentRetryCount()).isEqualTo(3);
            assertThat(req.getRetryWhen()).isInstanceOf(TestRetryWhen.class);
        }));
        assertThat(ret).isNotNull().isEqualTo(EXPECTED);
        assertThat(count.get()).isEqualTo(1);
    }

    @Test
    public void testRetry_base_not_retry() {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        AtomicInteger count = new AtomicInteger(0);
        String ret = retryClient2.testRetry_not_retry(3, 10, ((data, req, res) -> {
            count.incrementAndGet();
            assertThat(req.getCurrentRetryCount()).isEqualTo(0);
            assertThat(req.getRetryWhen()).isInstanceOf(TestRetryWhen2.class);
        }));
        assertThat(ret).isNotNull().isEqualTo(EXPECTED);
        assertThat(count.get()).isEqualTo(1);
    }



}
