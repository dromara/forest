package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.BaseReqAddressClient;
import com.dtflys.test.http.client.BaseReqVarClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @title: TestBaseReqVarClient
 * @Author Zengjie
 * @Date: 2023/9/14 9:42
 * @Version 1.0
 */
public class TestBaseReqVarClient extends BaseClientTest{

    public final static String EXPECTED = "{\"status\":\"ok\"}";

    public final static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.75 Safari/537.36";

    @Rule
    public MockWebServer server = new MockWebServer();

    private final BaseReqVarClient baseReqVarClient;

    private final BaseReqAddressClient baseReqAddressClient;

    private static ForestConfiguration configuration;
    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }
    public TestBaseReqVarClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.variable("port", server.getPort());
        configuration.variable("baseURL", "http://localhost:" + server.getPort() + "/a/");
        baseReqVarClient = configuration.createInstance(BaseReqVarClient.class);
        baseReqAddressClient = configuration.createInstance(BaseReqAddressClient.class);
    }

    /**
     * @BaseRequest注解下 方法URL地址没有一个/的情况下导致和baseUrl的拼接错误
     */
    @Test
    public void testSimpleGetWithoutSlash() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(baseReqVarClient.simpleGetWithoutSlash())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/a/hello");
    }

    /**
     * @BaseRequest注解下 方法URL地址没有一个/的情况下导致和baseUrl的拼接错误
     */
    @Test
    public void testSimpleGetWithoutSlash2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(baseReqVarClient.simpleGetWithoutSlash2())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/a/c/hello");
    }


    /**
     * @Address 注解下 方法URL地址没有一个/的情况下导致和baseUrl的拼接错误
     */
    @Test
    public void testSimpleGetWithoutSlash4Address() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(baseReqAddressClient.simpleGetWithoutSlash())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/a/hello");
    }
}