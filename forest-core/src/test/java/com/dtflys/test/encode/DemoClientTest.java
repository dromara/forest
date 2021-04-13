package com.dtflys.test.encode;

import com.dtflys.forest.backend.HttpBackendSelector;
import com.dtflys.forest.backend.httpclient.HttpclientBackend;
import com.dtflys.forest.backend.okhttp3.OkHttp3Backend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.mock.Get2MockServer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-04-13
 **/
public class DemoClientTest {

    @Rule
    public Get2MockServer server = new Get2MockServer(this);

    private DemoClient demoClient;

    private ForestConfiguration configuration = ForestConfiguration.configuration();

    @Before
    public void before() {
        configuration = ForestConfiguration.configuration();
        configuration.setLogResponseContent(true);
        configuration.getVariables().put("baseUrl", "https://localhost");
        configuration.setVariableValue("port", Get2MockServer.port);
        configuration.setBackend(new OkHttp3Backend());
        // configuration.setBackend(new HttpclientBackend());
        demoClient = configuration.createInstance(DemoClient.class);
        server.initServer();
    }

    @Test
    public void testTransaction() {
        ForestResponse<String> transaction = demoClient.transaction("gzip");
        assertEquals("测试gzip数据", transaction.getResult());
    }
}
