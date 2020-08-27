package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.client.BaseReqClient;
import com.dtflys.test.http.client.BaseURLClient;
import com.dtflys.test.http.client.BaseURLVarClient;
import com.dtflys.test.mock.BaseUrlMockServer;
import com.dtflys.test.mock.GetMockServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.$colon$plus;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 16:12
 */
public class TestBaseURL2Client extends BaseClientTest {


    private final static Logger log = LoggerFactory.getLogger(TestGetClient.class);

    @Rule
    public BaseUrlMockServer server = new BaseUrlMockServer(this);

    private static ForestConfiguration configuration;

    private static BaseReqClient baseReqClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("baseURL", "http://localhost:" + BaseUrlMockServer.port);
        configuration.setVariableValue("userAgent", BaseUrlMockServer.USER_AGENT);
        configuration.setVariableValue("port", BaseUrlMockServer.port);
    }

    public TestBaseURL2Client(HttpBackend backend) {
        super(backend, configuration);
        baseReqClient = configuration.createInstance(BaseReqClient.class);
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    @Test
    public void testBaseURL() {
        ForestResponse response = baseReqClient.simpleBaseUrl("UTF-8");
        assertNotNull(response);
        assertEquals("http://localhost:" + BaseUrlMockServer.port, response.getRequest().getUrl());
        String userAgent = response.getRequest().getHeaderValue("User-Agent");
        assertNotNull(userAgent);
        assertEquals(BaseUrlMockServer.USER_AGENT, userAgent);
    }


}
