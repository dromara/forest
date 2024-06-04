package com.dtflys.test.http.address;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.test.http.BaseClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestAddressClient6 extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;


    private AddressClient6 addressClient6;

    public TestAddressClient6(String backendName, String jsonConverterName) {
        super(backendName, jsonConverterName, configuration);
        configuration.setVariableValue("port", server.getPort());
        addressClient6 = configuration.createInstance(AddressClient6.class);
    }

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }


    @Test
    public void testLocalHost() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest request = addressClient6.testLocalHost();
        assertThat(request.getPath()).isEqualTo("/aaa");
        assertThat(request.getHost()).isEqualTo("127.0.0.1");
        String result = request.executeAsString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

}
