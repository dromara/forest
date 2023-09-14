package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.BaseURLPortClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;

/**
 * @Author Microsiland
 * @Date: 2023/9/14 10:03
 * @Version 1.0
 */
public class TestBaseURLPortClient  extends BaseClientTest{

    public final static String EXPECTED = "{\"status\":\"ok\"}";

    public final static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.75 Safari/537.36";

    @Rule
    public MockWebServer server = new MockWebServer();

    private final BaseURLPortClient baseURLPortClient;

    private static ForestConfiguration configuration;
    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }
    public TestBaseURLPortClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        configuration.setVariableValue("baseURL", "http://localhost:" + server.getPort());
        baseURLPortClient = configuration.createInstance(BaseURLPortClient.class);
    }

    /**
     * 修复声明式接口，@BaseRequest 或 @BaseURL，在有baseURL属性下，如果方法的完整URL不写端口，就会被baseURL属性的端口覆盖，并不是默认的80端口的bug。
     */
    @Test
    public void testSimpleGet() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(baseURLPortClient.getBaidu())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/getBaidu");
    }

    @Test
    public void testSimpleGet2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(baseURLPortClient.testPort("123"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/testPort");
    }
}