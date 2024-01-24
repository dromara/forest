package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.test.http.client.BaseURLPortClient;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

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
        configuration.variable("port", server.getPort());
        configuration.variable("baseURL", "http://localhost:" + server.getPort() +"/user");
        baseURLPortClient = configuration.createInstance(BaseURLPortClient.class);
    }

    /**
     * 修复声明式接口，@BaseRequest 或 @BaseURL，在有baseURL属性下，如果方法的完整URL不写端口，就会被baseURL属性的端口覆盖，并不是默认的80端口的bug。
     */
    @Test
    public void testSimpleGet() {
        ForestRequest baidu = baseURLPortClient.baidu();
        Assertions.assertThat(baidu).isNotNull();
        Assertions.assertThat(baidu.getUrl()).isEqualTo("http://www.baidu.com");
        baidu.execute();
    }
}