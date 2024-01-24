package com.dtflys.test.http.address;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.BaseClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestAddressClient4 extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;


    private AddressClient4 addressClient4;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    public TestAddressClient4(HttpBackend backend) {
        super(backend, configuration);
        configuration.variable("port", server.getPort());
        addressClient4 = configuration.createInstance(AddressClient4.class);
    }

    @Test
    public void test() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = addressClient4.test();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

}
