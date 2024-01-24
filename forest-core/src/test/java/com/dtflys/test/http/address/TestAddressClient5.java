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

public class TestAddressClient5 extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;


    private AddressClient5 addressClient5;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    public TestAddressClient5(HttpBackend backend) {
        super(backend, configuration);
        configuration.variable("port", server.getPort());
        addressClient5 = configuration.createInstance(AddressClient5.class);
    }

    @Test
    public void test() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest request = addressClient5.test();
        assertThat(request.basePath()).isEqualTo("/aaa");
        String result = request.stringResult();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

}
