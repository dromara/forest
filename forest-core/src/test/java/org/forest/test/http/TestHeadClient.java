package org.forest.test.http;

import org.forest.backend.HttpBackend;
import org.forest.backend.okhttp3.OkHttp3Backend;
import org.forest.config.ForestConfiguration;
import org.forest.http.ForestResponse;
import org.forest.test.http.client.GetClient;
import org.forest.test.http.client.HeadClient;
import org.forest.test.mock.HeadMockServer;
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
    }

}
