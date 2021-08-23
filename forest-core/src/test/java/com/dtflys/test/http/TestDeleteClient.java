package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.http.HttpHeaders;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.backend.okhttp3.OkHttp3Backend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.DeleteClient;
import com.dtflys.test.http.client.GetClient;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 17:24
 */
public class TestDeleteClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestDeleteClient.class);

    public final static String EXPECTED = "{\"status\":\"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private final DeleteClient deleteClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
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
