package org.dromara.forest.core.test.http.backend;

import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.http.ForestRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestBackendClient {

    public final static String EXPECTED = "{\"status\": \"ok\"}";
    @Rule
    public MockWebServer server = new MockWebServer();

    private BackendClient backendClient;

    private BackendClient2 backendClient2;

    private static ForestConfiguration configuration = ForestConfiguration.createConfiguration();


    public TestBackendClient() {
        backendClient = configuration.client(BackendClient.class);
        backendClient2 = configuration. client(BackendClient2.class);
    }

    @Test
    public void testHttpclient() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = backendClient.testHttpclient(server.getPort());
        assertThat(request.getBackend().getName()).isEqualTo("httpclient");
        String result = request.executeAsString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testHttpclient_2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = backendClient.testHttpclient_2(server.getPort());
        assertThat(request.getBackend().getName()).isEqualTo("httpclient");
        String result = request.executeAsString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }


    @Test
    public void testOkHttp3() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = backendClient.testOkHttp3(server.getPort());
        assertThat(request.getBackend().getName()).isEqualTo("okhttp3");
        String result = request.executeAsString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testOkHttp3_2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = backendClient.testOkHttp3_2(server.getPort());
        assertThat(request.getBackend().getName()).isEqualTo("okhttp3");
        String result = request.executeAsString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }


    @Test
    public void testVariableBackend() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = backendClient.testVariableBackend(server.getPort(), "okhttp3");
        assertThat(request.getBackend().getName()).isEqualTo("okhttp3");
        String result = request.executeAsString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }


    @Test
    public void testBaseBackend() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = backendClient2.testBaseBackend(server.getPort());
        assertThat(request.getBackend().getName()).isEqualTo("httpclient");
        String result = request.executeAsString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testMethodBackend() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = backendClient2.testMethodBackend(server.getPort());
        assertThat(request.getBackend().getName()).isEqualTo("okhttp3");
        String result = request.executeAsString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }


}
