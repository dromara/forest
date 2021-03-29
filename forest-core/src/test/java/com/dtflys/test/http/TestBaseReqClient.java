package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.BaseReqClient;
import com.dtflys.test.mock.BaseUrlMockServer;
import com.dtflys.test.mock.GetMockServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 16:12
 */
public class TestBaseReqClient extends BaseClientTest {


    private final static Logger log = LoggerFactory.getLogger(TestGetClient.class);

    @Rule
    public GetMockServer server = new GetMockServer(this);

    private static ForestConfiguration configuration;

    private static BaseReqClient baseReqClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("baseURL", "http://localhost:5000/");
        configuration.setVariableValue("userAgent", BaseUrlMockServer.USER_AGENT);
        configuration.setVariableValue("port", GetMockServer.port);
    }

    public TestBaseReqClient(HttpBackend backend) {
        super(backend, configuration);
        baseReqClient = configuration.createInstance(BaseReqClient.class);
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    @Test
    public void testBaseURL() {
        String result = baseReqClient.simpleGet((data, request, response) -> {
            String userAgent = response.getRequest().getHeaderValue("User-Agent");
            assertNotNull(userAgent);
            assertEquals(BaseUrlMockServer.USER_AGENT, userAgent);
        }, "UTF-8");
        assertEquals(GetMockServer.EXPECTED, result);
    }

    @Test
    public void testBaseURL2() {
        String result = baseReqClient.simpleGet2((data, request, response) -> {
            String userAgent = response.getRequest().getHeaderValue("User-Agent");
            assertNotNull(userAgent);
            assertEquals(BaseUrlMockServer.USER_AGENT, userAgent);
        }, "UTF-8");
        assertEquals(GetMockServer.EXPECTED, result);
    }


}
