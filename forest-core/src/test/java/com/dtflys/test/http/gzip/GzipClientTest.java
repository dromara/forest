package com.dtflys.test.http.gzip;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
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

    private GzipClient2 gzipClient2;

    private static ForestConfiguration configuration = ForestConfiguration.configuration();

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setLogResponseContent(true);
        configuration.getVariables().put("baseUrl", "http://localhost");
        configuration.setVariableValue("port", Get2MockServer.port);
    }


    public GzipClientTest(HttpBackend backend) {
        super(backend, configuration);
        gzipClient = configuration.createInstance(GzipClient.class);
        gzipClient2 = configuration.createInstance(GzipClient2.class);
    }

    @Before
    public void before() {
        // configuration.setBackend(new HttpclientBackend());
        server.initServer();
    }

    @Test
    public void testTransaction() {
        ForestResponse<String> response = gzipClient.transaction("gzip");
        assertEquals("测试gzip数据", response.getResult());
    }

    @Test
    public void testTransaction2() {
        ForestResponse<String> response = gzipClient2.transaction("gzip");
        assertEquals("测试gzip数据", response.getResult());
    }


    @Test
    public void testNoneGzip() {
        ForestResponse<String> response = gzipClient2.noneGzip();
        assertEquals("测试gzip数据", response.getResult());
    }


}
