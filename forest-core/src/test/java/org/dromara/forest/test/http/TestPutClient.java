package org.dromara.forest.test.http;

import org.dromara.forest.backend.HttpBackend;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.test.http.client.PutClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.*;

import static org.dromara.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 17:13
 */
public class TestPutClient extends BaseClientTest {

    @Rule
    public MockWebServer server = new MockWebServer();

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    private static ForestConfiguration configuration;

    private static PutClient putClient;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    @Override
    public void afterRequests() {
    }

    public TestPutClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        putClient = configuration.createInstance(PutClient.class);
    }


    @Test
    public void testPutHello() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(putClient.putHello())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("PUT")
                .assertPathEquals("/hello")
                .assertHeaderEquals("Accept", "text/plain")
                .assertBodyEquals("username=foo&password=123456");
    }

    @Test
    public void testSimplePut() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(putClient.simplePut())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("PUT")
                .assertPathEquals("/hello")
                .assertHeaderEquals("Accept", "text/plain")
                .assertBodyEquals("username=foo&password=123456");
    }

    @Test
    public void testSimplePut2() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(putClient.simplePut2())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("PUT")
                .assertPathEquals("/hello")
                .assertHeaderEquals("Accept", "text/plain")
                .assertBodyEquals("username=foo&password=123456");
    }

    @Test
    public void testSimplePut3() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(putClient.simplePut3())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("PUT")
                .assertPathEquals("/hello")
                .assertHeaderEquals("Accept", "text/plain")
                .assertBodyEquals("username=foo&password=123456");
    }


    @Test
    public void testTextParamPut() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(putClient.textParamPut("foo", "123456"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("PUT")
                .assertPathEquals("/hello")
                .assertHeaderEquals("Accept", "text/plain")
                .assertBodyEquals("username=foo&password=123456");
    }

    @Test
    public void testAnnParamPut() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(putClient.annParamPut("foo", "123456"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("PUT")
                .assertPathEquals("/hello")
                .assertHeaderEquals("Accept", "text/plain")
                .assertBodyEquals("username=foo&password=123456");

    }


}
