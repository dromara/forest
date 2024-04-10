package com.dtflys.test.http;

import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.HTTPProxy;
import com.dtflys.forest.config.ForestConfiguration;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author tanglingyan[xiao4852@qq.com]
 * @since 2022-06-09 14:29
 */
public class TestHttpClientHttpProxy extends BaseClientTest {

    public final static String EXPECTED = "{\"status\":\"ok\"}";


    private static ForestConfiguration configuration;
    @Rule
    public final MockWebServer server = new MockWebServer();

    private TestHttpClientHttpProxyClient testHttpClientHttpProxyClient;

    public TestHttpClientHttpProxy(String backend, String jsonConverter) {
        super(backend, jsonConverter, configuration);
        configuration.setVariableValue("port", server.getPort());
        testHttpClientHttpProxyClient = configuration.createInstance(TestHttpClientHttpProxyClient.class);

    }

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }


    @Override
    public void afterRequests() {
    }

    /**
     * 测试http代理
     */
    //由于需要代理依赖本地环境这里进行注释
    @Test
    public void testHttpClientHttpProxy() {
        //模拟https失败
//        server.enqueue(new MockResponse().setBody(EXPECTED));
//        String text = testHttpClientHttpProxyClient.testHttpProxy();
//        System.out.println(text);
//        Assert.assertTrue(text != null && text.indexOf("百度一下，你就知道") != -1);
    }


    /**
     * http接口
     *
     * @author tanglingyan[xiao4852@qq.com]
     * @since 2022-06-09 14:29
     */
    @HTTPProxy(host = "127.0.0.1", port = "10809")
    public interface TestHttpClientHttpProxyClient {
        //@Backend("httpclient")
        //@Get(url="https://127.0.0.1:{port}")
        @Get(value = "https://www.baidu.com", sslProtocol = "TLS")
        String testHttpProxy();
    }

}
