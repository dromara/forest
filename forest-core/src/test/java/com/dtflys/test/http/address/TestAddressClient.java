package com.dtflys.test.http.address;

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
        ForestRequest<String> request = addressClient.sendHostPort("localhost", server.getPort());
        assertThat(request.getHost()).isEqualTo("localhost");
        assertThat(request.getPort()).isEqualTo(server.getPort());
        String result = request.execute(String.class);
        assertThat(result).isEqualTo(EXPECTED);
    }

    @Test
    public void testAddress_source() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = addressClient.sendAddressSource(server.getPort());
        assertThat(request.getHost()).isEqualTo("127.0.0.1");
        assertThat(request.getPort()).isEqualTo(server.getPort());
        String result = request.execute(String.class);
        assertThat(result).isEqualTo(EXPECTED);
    }

    @Test
    public void testAddress_base() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = addressClient2.sendAddressSource(server.getPort());
        assertThat(request.getHost()).isEqualTo("127.0.0.1");
        assertThat(request.getPort()).isEqualTo(server.getPort());
        String result = request.execute(String.class);
        assertThat(result).isEqualTo(EXPECTED);
    }

    @Test
    public void testAddress_base2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = addressClient2.sendAddressSource2(server.getPort());
        assertThat(request.getHost()).isEqualTo("localhost");
        assertThat(request.getPort()).isEqualTo(server.getPort());
        String result = request.execute(String.class);
        assertThat(result).isEqualTo(EXPECTED);
    }


}
