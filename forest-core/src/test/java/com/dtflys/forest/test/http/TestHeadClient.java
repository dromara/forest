package com.dtflys.forest.test.http;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.test.http.client.HeadClient;
import com.dtflys.forest.test.model.TestHeaders;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 17:50
 */
public class TestHeadClient extends BaseClientTest {

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private static HeadClient headClient;

    private ThreadLocal<String> accessTokenLocal = new ThreadLocal<>();


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    @Override
    public void afterRequests() {
    }

    public TestHeadClient(String backend, String jsonConverter) {
        super(backend, jsonConverter, configuration);
        configuration.setVariableValue("port", server.getPort());
        headClient = configuration.createInstance(HeadClient.class);
    }


    @Test
    public void testHeadHelloUser() throws InterruptedException {
        server.enqueue(new MockResponse().setBody("ok"));
        headClient.headHelloUser();
        mockRequest(server)
                .assertMethodEquals("HEAD")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Accept", "text/plain")
                .assertHeaderEquals("accessToken", "11111111")
                .assertHeaderEquals("test", "testquery:dsds")
                .assertHeaderEquals("test2", "testquery2: dsds")
                .assertQueryEquals("username", "foo");
    }

    @Test
    public void testHeadHelloUser2() throws InterruptedException {
        server.enqueue(new MockResponse().setBody("ok"));
        headClient.headHelloUser("text/plain", "11111111");
        mockRequest(server)
                .assertMethodEquals("HEAD")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Accept", "text/plain")
                .assertHeaderEquals("accessToken", "11111111")
                .assertHeaderEquals("test", "testquery:dsds")
                .assertHeaderEquals("test2", "testquery2: dsds")
                .assertQueryEquals("username", "foo");
    }

    @Test
    public void testHeadHelloUser_lazy() throws InterruptedException {
        server.enqueue(new MockResponse().setBody("ok"));
        headClient.headHelloUser_Lazy("text/plain", req -> "11111111");
        mockRequest(server)
                .assertMethodEquals("HEAD")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Accept", "text/plain")
                .assertHeaderEquals("accessToken", "11111111")
                .assertHeaderEquals("test", "testquery:dsds")
                .assertHeaderEquals("test2", "testquery2: dsds")
                .assertQueryEquals("username", "foo");
    }


    @Test
    public void testHeadHelloUser2WithDefaultHeaders() throws InterruptedException {
        server.enqueue(new MockResponse().setBody("ok"));
        headClient.headHelloUserWithDefaultHeaders(null, null);
        mockRequest(server)
                .assertMethodEquals("HEAD")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Accept", "text/plain")
                .assertHeaderEquals("accessToken", "11111111")
                .assertHeaderEquals("test", "testquery:dsds")
                .assertHeaderEquals("test2", "testquery2: dsds")
                .assertQueryEquals("username", "foo");
    }

    @Test
    public void testSimpleHead() throws InterruptedException {
        server.enqueue(new MockResponse().setBody("ok"));
        accessTokenLocal.set("11111111");
        assertThat(headClient.simpleHead(accessTokenLocal.get(), "testquery:dsds", "testquery2: dsds"))
            .isNotNull()
            .isEqualTo("");
        mockRequest(server)
                .assertMethodEquals("HEAD")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Accept", "text/plain")
                .assertHeaderEquals("accessToken", "11111111")
                .assertHeaderEquals("test", "testquery:dsds")
                .assertHeaderEquals("test2", "testquery2: dsds")
                .assertQueryEquals("username", "foo");
    }

    @Test
    public void testSimpleHead2() throws InterruptedException {
        server.enqueue(new MockResponse().setBody("ok"));
        headClient.simpleHead2();
        mockRequest(server)
                .assertMethodEquals("HEAD")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Accept", "text/plain")
                .assertHeaderEquals("accessToken", "11111111")
                .assertHeaderEquals("test", "testquery:dsds")
                .assertHeaderEquals("test2", "testquery2: dsds")
                .assertQueryEquals("username", "foo");
    }

    @Test
    public void testSimpleHead3() throws InterruptedException {
        server.enqueue(new MockResponse().setBody("ok"));
        headClient.simpleHead3();
        mockRequest(server)
                .assertMethodEquals("HEAD")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Accept", "text/plain")
                .assertHeaderEquals("accessToken", "11111111")
                .assertHeaderEquals("test", "testquery:dsds")
                .assertHeaderEquals("test2", "testquery2: dsds")
                .assertQueryEquals("username", "foo");
    }


    @Test
    public void testHeadHelloUser3() throws InterruptedException {
        server.enqueue(new MockResponse().setBody("ok"));
        accessTokenLocal.set("11111111");
        Map<String, Object> headers = new HashMap<>();
        headers.put("Accept", "text/plain");
        headers.put("accessToken", accessTokenLocal.get());
        headers.put("test", "testquery:dsds");
        headers.put("test2", "testquery2: dsds");
        headClient.headHelloUser(headers, "foo");
        mockRequest(server)
                .assertMethodEquals("HEAD")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Accept", "text/plain")
                .assertHeaderEquals("accessToken", "11111111")
                .assertHeaderEquals("test", "testquery:dsds")
                .assertHeaderEquals("test2", "testquery2: dsds")
                .assertQueryEquals("username", "foo");
    }

    @Test
    public void testHeadHelloUser4() throws InterruptedException {
        server.enqueue(new MockResponse().setBody("ok"));
        TestHeaders headers = new TestHeaders();
        headers.setAccept("text/plain");
        headers.setAccessToken("11111111");
        headers.setTest("testquery:dsds");
        headers.setTest2("testquery2: dsds");
        headClient.headHelloUser(headers);
        mockRequest(server)
                .assertMethodEquals("HEAD")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Accept", "text/plain")
                .assertHeaderEquals("accessToken", "11111111")
                .assertHeaderEquals("test", "testquery:dsds")
                .assertHeaderEquals("test2", "testquery2: dsds")
                .assertQueryEquals("username", "foo");
    }


   @Test
    public void testResponseHead() throws InterruptedException {
        server.enqueue(new MockResponse()
                .setHeader("server", "mock server")
                .setHeader("Content-Length", "0"));
        ForestResponse response = headClient.responseHead();
        assertThat(response)
                .isNotNull()
                .extracting(
                        ForestResponse::getStatusCode,
                        ForestResponse::isSuccess,
                        ForestResponse::isError)
                .contains(200, true, false);
        assertThat(response.getHeaderValue("server")).isEqualTo("mock server");
        assertThat(response.getHeaderValue("content-length")).isEqualTo("0");
        mockRequest(server)
                .assertMethodEquals("HEAD")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Accept", "text/plain")
                .assertHeaderEquals("accessToken", "11111111")
                .assertHeaderEquals("test", "testquery:dsds")
                .assertHeaderEquals("test2", "testquery2: dsds")
                .assertQueryEquals("username", "foo");
    }

}
