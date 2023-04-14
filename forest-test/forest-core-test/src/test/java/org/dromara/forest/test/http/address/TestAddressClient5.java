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
        configuration.setVariableValue("port", server.getPort());
        addressClient5 = configuration.createInstance(AddressClient5.class);
    }

    @Test
    public void test() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest request = addressClient5.test();
        assertThat(request.basePath()).isEqualTo("/aaa");
        String result = request.executeAsString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

}
