package com.dtflys.forest.test.http.retry;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.retryer.BackOffRetryer;
import com.dtflys.forest.test.http.BaseClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestRetryerClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"error\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private final RetryerClient retryerClient;

    private final RetryerClient2 retryerClient2;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }


    public TestRetryerClient(String backend, String jsonConverter) {
        super(backend, jsonConverter, configuration);
        configuration.setVariableValue("port", server.getPort());
        retryerClient = configuration.createInstance(RetryerClient.class);
        retryerClient2 = configuration.createInstance(RetryerClient2.class);
    }

    @Test
    public void testRetryerRequest() {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        ForestRequest<String> request = retryerClient.testRetryRequest(3);
        assertThat(request).isNotNull();
        assertThat(request.getRetryer()).isInstanceOf(TestRetryer.class);
        try {
            request.execute(String.class);
        } catch (Throwable ignored) {}
        assertThat(request.getRetryCount()).isEqualTo(3);
    }

    @Test
    public void testRetryerRequest_base() {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        ForestRequest<String> request = retryerClient2.testRetryRequest(3);
        assertThat(request).isNotNull();
        assertThat(request.getRetryer().getClass()).isEqualTo(TestRetryer.class);
        try {
            request.execute(String.class);
        } catch (Throwable ignored) {}
        assertThat(request.getRetryCount()).isEqualTo(3);
    }

    @Test
    public void testRetryerRequest_base_backoff() {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        ForestRequest<String> request = retryerClient2.testRetryRequest_backoff(3);
        assertThat(request).isNotNull();
        assertThat(request.getRetryer().getClass()).isEqualTo(BackOffRetryer.class);
        try {
            request.execute(String.class);
        } catch (Throwable ignored) {}
        assertThat(request.getRetryCount()).isEqualTo(3);
    }


}
