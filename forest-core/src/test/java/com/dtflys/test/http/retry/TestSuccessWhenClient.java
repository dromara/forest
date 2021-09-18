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

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-16 22:25
 */
public class TestSuccessWhenClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"error\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private final SuccessWhenClient successWhenClient;

    private final SuccessWhenClient2 successWhenClient2;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }


    public TestSuccessWhenClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        successWhenClient = configuration.createInstance(SuccessWhenClient.class);
        successWhenClient2 = configuration.createInstance(SuccessWhenClient2.class);
    }


    @Test
    public void testRetry() {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        AtomicReference<Boolean> isError = new AtomicReference<>(false);
        ForestRequest<String> request = successWhenClient.testRetryRequest(3, (ex, req, res) -> {
            isError.set(true);
        });
        assertThat(request).isNotNull();
        assertThat(request.getSuccessWhen()).isNotNull().isInstanceOf(TestSuccessWhen.class);
        try {
            request.execute();
        } catch (Throwable ignored) {}
        assertThat(request.getRetryCount()).isEqualTo(3);
        assertThat(isError.get()).isTrue();
    }

    @Test
    public void testRetry_base() {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        AtomicReference<Boolean> isError = new AtomicReference<>(false);
        ForestRequest<String> request = successWhenClient2.testRetryRequest(3, (ex, req, res) -> {
            isError.set(true);
        });
        assertThat(request).isNotNull();
        assertThat(request.getSuccessWhen()).isNotNull().isInstanceOf(TestSuccessWhen.class);
        try {
            request.execute();
        } catch (Throwable ignored) {}
        assertThat(request.getCurrentRetryCount()).isEqualTo(3);
        assertThat(isError.get()).isTrue();
    }


    @Test
    public void testRetry_base_success() {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        AtomicReference<Boolean> isError = new AtomicReference<>(false);
        ForestRequest<String> request = successWhenClient2.testRetryRequest_success(3, (ex, req, res) -> {
            isError.set(true);
        });
        assertThat(request).isNotNull();
        assertThat(request.getSuccessWhen()).isNotNull().isInstanceOf(TestSuccessWhen2.class);
        try {
            request.execute();
        } catch (Throwable ignored) {}
        assertThat(request.getCurrentRetryCount()).isEqualTo(0);
        assertThat(isError.get()).isFalse();
    }

}
