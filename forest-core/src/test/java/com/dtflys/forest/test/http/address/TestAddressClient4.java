package com.dtflys.forest.test.http.address;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.test.http.BaseClientTest;
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

    public TestAddressClient4(String backend, String jsonConverter) {
        super(backend, jsonConverter, configuration);
        configuration.setVariableValue("port", server.getPort());
        addressClient4 = configuration.createInstance(AddressClient4.class);
    }

    @Test
    public void test() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = addressClient4.test();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

}
