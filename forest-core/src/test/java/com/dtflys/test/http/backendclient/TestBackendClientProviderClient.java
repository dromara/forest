package com.dtflys.test.http.backendclient;

import com.dtflys.forest.Forest;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.retry.TestRetryInterceptor;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestBackendClientProviderClient {
    public final static String EXPECTED = "{\"status\": \"ok\"}";
    @Rule
    public MockWebServer server = new MockWebServer();

    private BackendClientProviderClient backendClientProviderClient;

    private static ForestConfiguration configuration = ForestConfiguration.createConfiguration();

    public TestBackendClientProviderClient() {
        backendClientProviderClient = configuration.client(BackendClientProviderClient.class);
        configuration.setVariableValue("port", server.getPort());
    }

    @Test
    public void testOkHttpClientProvider() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = backendClientProviderClient.getOkHttpClientProvider();
        assertThat(request).isNotNull();
        assertThat(request.getBackendClient())
                .isNotNull()
                .isInstanceOf(MyOkHttpClientProvider.class);
        AtomicBoolean executed = new AtomicBoolean(false);
        request.addInterceptor(new TestRetryInterceptor() {
            @Override
            public boolean beforeExecute(ForestRequest request) {
                assertThat(request.getBackendClient())
                        .isNotNull()
                        .isInstanceOf(MyOkHttpClientProvider.class);
                executed.set(true);
                return true;
            }
        });
        String result = request.execute(String.class);
        assertThat(executed.get()).isTrue();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testHttpClientProvider() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = backendClientProviderClient.getHttpClientProvider();
        assertThat(request).isNotNull();
        assertThat(request.getBackendClient())
                .isNotNull()
                .isInstanceOf(MyHttpClientProvider.class);
        AtomicBoolean executed = new AtomicBoolean(false);
        request.addInterceptor(new TestRetryInterceptor() {
            @Override
            public boolean beforeExecute(ForestRequest request) {
                assertThat(request.getBackendClient())
                        .isNotNull()
                        .isInstanceOf(MyHttpClientProvider.class);
                executed.set(true);
                return true;
            }
        });
        String result = request.execute(String.class);
        assertThat(executed.get()).isTrue();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }


}
