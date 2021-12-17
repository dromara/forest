package com.dtflys.test.http.proxy;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.test.http.BaseClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-12-17
 */
public class TestHTTPProxyClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private HTTPProxyClient addressClient;

    private HTTPProxyClient2 addressClient2;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }


    public TestHTTPProxyClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setLogRequest(true);
        addressClient = configuration.createInstance(HTTPProxyClient.class);
        addressClient2 = configuration.createInstance(HTTPProxyClient2.class);
    }

    @Test
    public void testHTTPProxy_host_port() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = addressClient.sendHostPort("localhost", server.getPort());
        String result = request.execute(String.class);
        assertThat(request.getProxy().getHost()).isEqualTo("localhost");
        assertThat(request.getProxy().getPort()).isEqualTo(server.getPort());
        assertThat(result).isEqualTo(EXPECTED);
    }

    @Test
    public void testHTTPProxy_source() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = addressClient.sendHTTPProxySource(server.getPort());
        String result = request.execute(String.class);
        assertThat(request.getProxy().getHost()).isEqualTo("127.0.0.1");
        assertThat(request.getProxy().getPort()).isEqualTo(server.getPort());
        assertThat(result).isEqualTo(EXPECTED);
    }

    @Test
    public void testHTTPProxy_base() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = addressClient2.sendHTTPProxySource(server.getPort());
        String result = request.execute(String.class);
        assertThat(request.getProxy().getHost()).isEqualTo("127.0.0.1");
        assertThat(request.getProxy().getPort()).isEqualTo(server.getPort());
        assertThat(result).isEqualTo(EXPECTED);
    }

    @Test
    public void testHTTPProxy_base2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = addressClient2.sendHTTPProxySource2(server.getPort());
        String result = request.execute(String.class);
        assertThat(request.getProxy().getHost()).isEqualTo("localhost");
        assertThat(request.getProxy().getPort()).isEqualTo(server.getPort());
        assertThat(result).isEqualTo(EXPECTED);
    }


}
