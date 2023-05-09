package org.dromara.forest.core.test.interceptor;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.http.HttpHeaders;
import org.dromara.forest.backend.HttpBackend;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.core.test.http.BaseClientTest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.dromara.forest.mock.MockServerRequest.mockRequest;

public class TestBaseAuthClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    public final static String AUTHORIZATION = "Basic Zm9vOmJhcg==";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private static BaseAuthClient baseAuthClient;

    public TestBaseAuthClient(HttpBackend backend) {
        super(backend, configuration);
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
