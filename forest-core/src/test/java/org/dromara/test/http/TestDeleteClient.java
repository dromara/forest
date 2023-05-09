package org.dromara.test.http;

import org.dromara.forest.backend.HttpBackend;
import org.dromara.forest.config.ForestConfiguration;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.http.HttpHeaders;
import org.dromara.test.http.client.DeleteClient;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static org.dromara.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 17:24
 */
public class TestDeleteClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\":\"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private final DeleteClient deleteClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    public TestDeleteClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        deleteClient = configuration.createInstance(DeleteClient.class);
    }

    @Override
    public void afterRequests() {
    }

    @Test
    public void testDeleteUser() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(deleteClient.deleteUser())
            .isNotNull()
            .isEqualTo(EXPECTED);
        mockRequest(server)
            .assertMethodEquals("DELETE")
            .assertPathEquals("/xx/user")
            .assertHeaderEquals(HttpHeaders.ACCEPT, "text/plain")
            .assertQueryEquals("username=foo");
    }


    @Test
    public void testSimpleDelete() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(deleteClient.simpleDelete())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("DELETE")
                .assertPathEquals("/xx/user")
                .assertHeaderEquals(HttpHeaders.ACCEPT, "text/plain")
                .assertQueryEquals("username=foo");
    }

    @Test
    public void testSimpleDelete2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(deleteClient.simpleDelete2())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("DELETE")
                .assertPathEquals("/xx/user")
                .assertHeaderEquals(HttpHeaders.ACCEPT, "text/plain")
                .assertQueryEquals("username=foo");
    }

    @Test
    public void testSimpleDelete3() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(deleteClient.simpleDelete3())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("DELETE")
                .assertPathEquals("/xx/user")
                .assertHeaderEquals(HttpHeaders.ACCEPT, "text/plain")
                .assertQueryEquals("username=foo");
    }



    @Test
    public void testTextParamDelete() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(deleteClient.textParamDelete("foo"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("DELETE")
                .assertPathEquals("/xx/user")
                .assertHeaderEquals(HttpHeaders.ACCEPT, "text/plain")
                .assertQueryEquals("username=foo");
    }

    @Test
    public void testAnnParamDelete() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(deleteClient.annParamDelete("foo"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("DELETE")
                .assertPathEquals("/xx/user")
                .assertHeaderEquals(HttpHeaders.ACCEPT, "text/plain")
                .assertQueryEquals("username=foo");
    }

}
