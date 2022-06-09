package com.dtflys.test.http;

import com.dtflys.forest.annotation.Backend;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.HTTPProxy;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.mock.MockServerRequest;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author tanglingyan[xiao4852@qq.com]
 * @since 2022-06-04 10:57
 */
public class TestHttpClientHttpPorxy extends BaseClientTest {

    private static ForestConfiguration configuration;
    @Rule
    public final MockWebServer server = new MockWebServer();
    private final TestHttpClientHttpPorxyClient testHttpClientHttpPorxyClient;

    public TestHttpClientHttpPorxy(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        testHttpClientHttpPorxyClient = configuration.createInstance(TestHttpClientHttpPorxyClient.class);

    }

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    public static MockServerRequest mockRequest(MockWebServer server) {
        try {
            RecordedRequest request = server.takeRequest();
            return new MockServerRequest(request);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterRequests() {
    }

    /**
     *
     */
    @Test
    public void testHttpClientHttpPorxy() {
//        server.enqueue(new MockResponse().setBody("{'flag':'ok'}"));
//        //发送http请求
//        testHttpClientHttpPorxyClient.testHttpPorxy();
//        try {
//            server.takeRequest();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        String text = testHttpClientHttpPorxyClient.testHttpPorxy();
        //System.out.println(text);
    }


    /**
     * http接口
     *
     * @author tanglingyan[xiao4852@qq.com]
     * @since 2022-06-04 10:57
     */
    @HTTPProxy(host = "127.0.0.1", port = "8888")
    public interface TestHttpClientHttpPorxyClient {
        @Backend("httpclient")
//        @Get("https://localhost:{port}")
//        @Get("https:www.baidu.com")
        @Get(url="https://10.34.1.170/",sslProtocol = "TLS")
        String testHttpPorxy();
    }

}
