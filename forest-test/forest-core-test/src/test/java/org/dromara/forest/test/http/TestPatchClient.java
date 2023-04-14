package org.dromara.forest.test.http;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.http.HttpHeaders;
import org.dromara.forest.backend.HttpBackend;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.test.http.client.PatchClient;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static org.dromara.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 17:13
 */
public class TestPatchClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private static PatchClient patchClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    @Override
    public void afterRequests() {
    }

    public TestPatchClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        patchClient = configuration.createInstance(PatchClient.class);
    }

    @Test
    public void testPatchHello() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(patchClient.patchHello())
            .isNotNull()
            .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("PATCH")
                .assertPathEquals("/hello")
                .assertHeaderEquals(HttpHeaders.ACCEPT, "text/plain")
                .assertBodyEquals("username=foo&password=123456");
    }


    @Test
    public void testSimplePatch() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(patchClient.simplePatch())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("PATCH")
                .assertPathEquals("/hello")
                .assertHeaderEquals(HttpHeaders.ACCEPT, "text/plain")
                .assertBodyEquals("username=foo&password=123456");
    }

    @Test
    public void testSimplePatch2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(patchClient.simplePatch2())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("PATCH")
                .assertPathEquals("/hello")
                .assertHeaderEquals(HttpHeaders.ACCEPT, "text/plain")
                .assertBodyEquals("username=foo&password=123456");
    }

    @Test
    public void testSimplePatch3() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(patchClient.simplePatch3())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("PATCH")
                .assertPathEquals("/hello")
                .assertHeaderEquals(HttpHeaders.ACCEPT, "text/plain")
                .assertBodyEquals("username=foo&password=123456");
    }



    @Test
    public void testTextParamPatch() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(patchClient.textParamPatch("foo", "123456"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("PATCH")
                .assertPathEquals("/hello")
                .assertHeaderEquals(HttpHeaders.ACCEPT, "text/plain")
                .assertBodyEquals("username=foo&password=123456");
    }

    @Test
    public void testAnnParamPatch() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(patchClient.annParamPatch("foo", "123456"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("PATCH")
                .assertPathEquals("/hello")
                .assertHeaderEquals(HttpHeaders.ACCEPT, "text/plain")
                .assertBodyEquals("username=foo&password=123456");
    }


}
