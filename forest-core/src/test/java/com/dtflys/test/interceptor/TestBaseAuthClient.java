package com.dtflys.test.interceptor;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.test.http.BaseClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.http.HttpHeaders;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestBaseAuthClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    public final static String AUTHORIZATION = "Basic Zm9vOmJhcg==";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private static BaseAuthClient baseAuthClient;

    public TestBaseAuthClient(String backend, String jsonConverter) {
        super(backend, jsonConverter, configuration);
        configuration.setCacheEnabled(false);
        configuration.setVariableValue("port", server.getPort());
        baseAuthClient = configuration.createInstance(BaseAuthClient.class);
    }

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    @Test
    public void testBaseAuth() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(baseAuthClient.send("foo"))
            .isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Authorization", AUTHORIZATION)
                .assertHeaderEquals(HttpHeaders.ACCEPT, "text/plain")
                .assertQueryEquals("username",  "foo");

        assertThat(baseAuthClient.send2("foo"))
                .isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Authorization", AUTHORIZATION)
                .assertHeaderEquals(HttpHeaders.ACCEPT, "text/plain")
                .assertQueryEquals("username",  "foo");
    }

}
