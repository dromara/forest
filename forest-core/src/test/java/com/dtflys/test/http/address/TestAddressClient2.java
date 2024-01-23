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

public class TestAddressClient2 extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;


    private AddressClient3 addressClient3;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    public TestAddressClient2(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVar("port", server.getPort());
        addressClient3 = configuration.createInstance(AddressClient3.class);
    }

    @Test
    public void testCustomAnnotation() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = addressClient3.testCustomAnnotation();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

}
