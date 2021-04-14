package com.dtflys.test.encode;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.backend.okhttp3.OkHttp3Backend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestHeader;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.BaseClientTest;
import com.dtflys.test.mock.Get2MockServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-04-13
 **/
public class GzipClientTest extends BaseClientTest {

    @Rule
    public Get2MockServer server = new Get2MockServer(this);

    private GzipClient gzipClient;

    private static ForestConfiguration configuration = ForestConfiguration.configuration();

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setLogResponseContent(true);
        configuration.getVariables().put("baseUrl", "https://localhost");
        configuration.setVariableValue("port", Get2MockServer.port);
    }


    public GzipClientTest(HttpBackend backend) {
        super(backend, configuration);
        gzipClient = configuration.createInstance(GzipClient.class);
    }

    @Before
    public void before() {
        // configuration.setBackend(new HttpclientBackend());
        server.initServer();
    }

    @Test
    public void testTransaction() {
        ForestResponse<String> response = gzipClient.transaction("gzip");
//        ForestHeader contentEncoding = response.getHeader("Content-Encoding");
//        assertNotNull(contentEncoding);
//        assertEquals("gzip, deflate", contentEncoding.getValue());
        assertEquals("测试gzip数据", response.getResult());
    }
}
