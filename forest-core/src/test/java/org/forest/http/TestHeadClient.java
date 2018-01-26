package org.forest.http;

import org.forest.config.ForestConfiguration;
import org.forest.http.client.HeadClient;
import org.forest.mock.HeadMockServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 17:50
 */
public class TestHeadClient {

    private final static Logger log = LoggerFactory.getLogger(TestHeadClient.class);

    @Rule
    public HeadMockServer server = new HeadMockServer(this);

    private static ForestConfiguration configuration;

    private static HeadClient headClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
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
