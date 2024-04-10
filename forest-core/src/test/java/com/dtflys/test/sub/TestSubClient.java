package com.dtflys.test.sub;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.test.http.BaseClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-19 2:55
 */
public class TestSubClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private SubClient subClient;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }


    public TestSubClient(String backend, String jsonConverter) {
        super(backend, jsonConverter, configuration);
        configuration.setVariableValue("port", server.getPort());
        subClient = configuration.createInstance(SubClient.class);
    }

    @Test
    public void testSub() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        server.enqueue(new MockResponse().setBody(EXPECTED));

        String result = subClient.testA();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/A");

        result = subClient.testB();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/B");

        result = subClient.testC();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/C");

        result = subClient.testD();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/D");

        result = subClient.testE();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/E");

    }

}
