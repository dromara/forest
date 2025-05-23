package com.dtflys.forest.test.http.gzip;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.reflection.BasicVariable;
import com.dtflys.forest.test.mock.Get2MockServer;
import com.dtflys.forest.test.http.BaseClientTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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

    private static ForestConfiguration configuration;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
        configuration.setLogResponseContent(true);
        configuration.getVariables().put("baseUrl", new BasicVariable("http://localhost"));
    }

    @Override
    public void afterRequests() {
    }

    public GzipClientTest(String backend, String jsonConverter) {
        super(backend, jsonConverter, configuration);
        configuration.setVariableValue("port", method -> server.port());
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
    public void testTransaction_without_annotation() {
        ForestResponse<String> response = gzipClient.transaction_without_annotation("gzip");
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
        assertThat(response).isNotNull();
        assertThat(response.getCharset()).isEqualToIgnoringCase("UTF-8");
        assertThat(response.getContentEncoding()).isNull();
        assertEquals("测试gzip数据", response.getResult());
    }


}
