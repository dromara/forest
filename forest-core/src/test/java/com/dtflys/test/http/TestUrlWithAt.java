package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.client.GetClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 15:02
 */
public class TestUrlWithAt extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    private final static Logger log = LoggerFactory.getLogger(TestUrlWithAt.class);

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private GetClient getClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
    }


    public TestUrlWithAt(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVar("port", server.getPort());
        getClient = configuration.createInstance(GetClient.class);
    }



    @Override
    public void afterRequests() {
    }

    @Test
    public void testGetUrlWithUserInfo() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestResponse<String> response = getClient.getUrlWithUserInfo();
        assertNotNull(response);
        log.info("response: " + response);
        ForestRequest request = response.getRequest();
        String userInfo = request.getUserInfo();
        assertEquals("xxxxxx:yyyy", userInfo);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/hello/user");
    }

    @Test
    public void testGetUrlWithUserInfo2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestResponse<String> response = getClient.getUrlWithUserInfo2();
        assertNotNull(response);
        log.info("response: " + response);
        ForestRequest request = response.getRequest();
        String userInfo = request.getUserInfo();
        assertEquals("xxxxxx:1234", userInfo);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/hello/user");
    }

    @Test
    public void testGetUrlWithUserInfo3() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestResponse<String> response = getClient.getUrlWithUserInfo3("xxx/yyy/foo");
        assertNotNull(response);
        log.info("response: " + response);
        ForestRequest request = response.getRequest();
        String userInfo = request.getUserInfo();
        assertEquals("xxx%2Fyyy%2Ffoo", userInfo);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/hello/user");
    }

    @Test
    public void testGetUrlWithUserInfo4() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestResponse<String> response = getClient.getUrlWithUserInfo3("foo");
        assertNotNull(response);
        log.info("response: " + response);
        ForestRequest request = response.getRequest();
        String userInfo = request.getUserInfo();
        assertEquals("foo", userInfo);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/hello/user");
    }


}
