package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.client.GetClient;
import com.dtflys.test.mock.GetMockServer;
import com.dtflys.test.mock.QueryStringMockServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 15:02
 */
public class TestUrlWithAt extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestUrlWithAt.class);


    private static ForestConfiguration configuration;

    private GetClient getClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
    }


    public TestUrlWithAt(HttpBackend backend) {
        super(backend, configuration);
        getClient = configuration.createInstance(GetClient.class);
    }


    @Override
    public void afterRequests() {
    }

    @Test
    public void testGetUrlWithAt() {
        ForestResponse<String> response = getClient.getUrlWithAt();
        assertNotNull(response);
        log.info("response: " + response);
        ForestRequest request = response.getRequest();
//        String url = request.getUrl();
        String userInfo = request.getUserInfo();
        assertEquals("xxxxxx:yyyy", userInfo);
    }


}
