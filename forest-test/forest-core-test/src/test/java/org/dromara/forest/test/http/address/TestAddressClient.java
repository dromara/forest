package org.dromara.forest.test.http.address;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.dromara.forest.backend.HttpBackend;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.test.http.BaseClientTest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.dromara.forest.mock.MockServerRequest.mockRequest;

public class TestAddressClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private AddressClient addressClient;

    private AddressClient2 addressClient2;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }


    public TestAddressClient(HttpBackend backend) {
        super(backend, configuration);
        addressClient = configuration.createInstance(AddressClient.class);
        addressClient2 = configuration.createInstance(AddressClient2.class);
    }

    @Test
    public void testAddress_host_port() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = addressClient.testHostPort("localhost", server.getPort());
        assertThat(request.getHost()).isEqualTo("localhost");
        assertThat(request.getPort()).isEqualTo(server.getPort());
        String result = request.execute(String.class);
        assertThat(result).isEqualTo(EXPECTED);
    }

    @Test
    public void testAddress_source() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = addressClient.testAddressSource(server.getPort());
        assertThat(request.getHost()).isEqualTo("127.0.0.1");
        assertThat(request.getPort()).isEqualTo(server.getPort());
        String result = request.execute(String.class);
        assertThat(result).isEqualTo(EXPECTED);
    }

    @Test
    public void testAddress_basePath() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = addressClient.testBasePath("localhost", server.getPort(), "BASE/1/2");
        String result = request.execute(String.class);
        assertThat(request.host()).isEqualTo("localhost");
        assertThat(request.port()).isEqualTo(server.getPort());
        assertThat(request.basePath()).isEqualTo("/BASE/1/2");
        assertThat(result).isEqualTo(EXPECTED);

        mockRequest(server)
                .assertPathEquals("/BASE/1/2/xxx");
    }

    @Test
    public void testAddress_basePath2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = addressClient.testBasePath(
                "127.0.0.1", server.getPort(), "http://localhost:" + server.getPort() + "/BASE/1/2");
        String result = request.execute(String.class);
        assertThat(request.host()).isEqualTo("127.0.0.1");
        assertThat(request.port()).isEqualTo(server.getPort());
        assertThat(request.basePath()).isEqualTo("/BASE/1/2");
        assertThat(result).isEqualTo(EXPECTED);

        mockRequest(server)
                .assertPathEquals("/BASE/1/2/xxx");
    }

    @Test
    public void testAddress_basePathOnly() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = addressClient.testBasePathOnly(server.getPort());
        String result = request.execute(String.class);
        assertThat(request.host()).isEqualTo("localhost");
        assertThat(request.port()).isEqualTo(server.getPort());
        assertThat(request.basePath()).isEqualTo("/aaa");
        assertThat(result).isEqualTo(EXPECTED);

        mockRequest(server)
                .assertPathEquals("/aaa/xxx");
    }


    @Test
    public void testAddress_base() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = addressClient2.sendAddressSource(server.getPort());
        assertThat(request.getHost()).isEqualTo("127.0.0.1");
        assertThat(request.getPort()).isEqualTo(server.getPort());
        String result = request.execute(String.class);
        assertThat(result).isEqualTo(EXPECTED);
        mockRequest(server).assertPathEquals("/");
    }

    @Test
    public void testAddress_base2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = addressClient2.sendAddressSource2(server.getPort());
        request.addQuery("a", 123);
        assertThat(request.getQueryString()).isNotBlank();
        assertThat(request.getBasePath()).isEqualTo("/base/path");
        assertThat(request.getHost()).isEqualTo("localhost");
        assertThat(request.getPort()).isEqualTo(server.getPort());
        String result = request.execute(String.class);
        assertThat(result).isEqualTo(EXPECTED);
        mockRequest(server).assertPathEquals("/base/path/");
    }


}
