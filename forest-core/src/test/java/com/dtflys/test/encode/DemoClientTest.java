package com.dtflys.test.encode;

import com.dtflys.forest.backend.HttpBackendSelector;
import com.dtflys.forest.backend.httpclient.HttpclientBackend;
import com.dtflys.forest.backend.okhttp3.OkHttp3Backend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-04-13
 **/
public class DemoClientTest {

    private DemoClient demoClient;

    private ForestConfiguration configuration = ForestConfiguration.configuration();

    @Before
    public void before() {
        configuration = ForestConfiguration.configuration();
        configuration.setLogResponseContent(true);
        configuration.getVariables().put("baseUrl", "https://api.vvhan.com/api/");
        configuration.setBackend(new OkHttp3Backend());
        // configuration.setBackend(new HttpclientBackend());
        demoClient = configuration.createInstance(DemoClient.class);
    }

    @Test
    public void testTransaction() {
        ForestResponse<String> transaction = demoClient.transaction("xh", "json");
        assertNotNull(transaction.getResult());
    }
}
