package com.dtflys.test.interceptor;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.BaseClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.http.HttpHeaders;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestBasicAuthClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    public final static String AUTHORIZATION = "Basic Zm9vOmJhcg==";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private static BasicAuthClient basicAuthClient;

    public TestBasicAuthClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setCacheEnabled(false);
        configuration.setVariableValue("port", server.getPort());
        basicAuthClient = configuration.createInstance(BasicAuthClient.class);
    }


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
    }

    @Test
    public void testBasicAuth() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(basicAuthClient.send("foo"))
            .isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Authorization", AUTHORIZATION)
                .assertHeaderEquals(HttpHeaders.ACCEPT, "text/plain")
                .assertQueryEquals("username",  "foo");
    }

}
