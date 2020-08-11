package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.mock.HeadMockServer;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.client.HeadClient;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.*;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 17:50
 */
public class TestHeadClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestHeadClient.class);

    @Rule
    public HeadMockServer server = new HeadMockServer(this);

    private static ForestConfiguration configuration;

    private static HeadClient headClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", HeadMockServer.port);
    }

    public TestHeadClient(HttpBackend backend) {
        super(backend, configuration);
        headClient = configuration.createInstance(HeadClient.class);

    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }


    @Test
    public void testHeadHelloUser() {
        headClient.headHelloUser();
    }

    @Test
    public void testSimpleHead() {
        headClient.simpleHead();
    }


    @Test
    public void testResponseHead() {
        ForestResponse response = headClient.responseHead();
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertTrue(response.isSuccess());
        assertFalse(response.isError());
        assertEquals("mock server", response.getHeaderValue("server"));
        assertEquals("0", response.getHeaderValue("content-length"));
    }

}
