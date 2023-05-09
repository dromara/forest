package org.dromara.forest.core.test.http.backendclient;

import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.core.test.http.retry.TestRetryInterceptor;
import org.dromara.forest.http.ForestRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

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
        int count = 3;
        for (int i = 0; i < count; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED));
        }
        MyOkHttpClientProvider myOkHttpClientProvider = configuration.getForestObject(MyOkHttpClientProvider.class);
        myOkHttpClientProvider.setCount(0);
        for (int i = 0; i < count; i++) {
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
        assertThat(myOkHttpClientProvider.getCount()).isEqualTo(1);
    }

    @Test
    public void testHttpClientProvider() {
        int count = 3;
        for (int i = 0; i < count; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED));
        }
        MyHttpClientProvider httpClientProvider = configuration.getForestObject(MyHttpClientProvider.class);
        httpClientProvider.setCount(0);
        for (int i = 0; i < count; i++) {
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
        assertThat(httpClientProvider.getCount()).isEqualTo(1);
    }


    @Test
    public void testOkHttpClientProvider_without_cache() {
        int count = 3;
        for (int i = 0; i < count; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED));
        }
        MyOkHttpClientProvider myOkHttpClientProvider = configuration.getForestObject(MyOkHttpClientProvider.class);
        myOkHttpClientProvider.setCount(0);
        for (int i = 0; i < count; i++) {
            ForestRequest<String> request = backendClientProviderClient.getOkHttpClientProvider_without_cache();
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
        assertThat(myOkHttpClientProvider.getCount()).isEqualTo(count);
    }


    @Test
    public void testHttpClientProvider_without_cache() {
        int count = 3;
        for (int i = 0; i < count; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED));
        }
        MyHttpClientProvider httpClientProvider = configuration.getForestObject(MyHttpClientProvider.class);
        httpClientProvider.setCount(0);
        for (int i = 0; i < count; i++) {
            ForestRequest<String> request = backendClientProviderClient.getHttpClientProvider_without_cache();
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
        assertThat(httpClientProvider.getCount()).isEqualTo(count);
    }


}
