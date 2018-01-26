package org.forest.http;

import org.forest.config.ForestConfiguration;
import org.forest.http.client.PostClient;
import org.forest.http.model.UserParam;
import org.forest.mock.PostComplexMockServer;
import org.forest.mock.PostMockServer;
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
 * @since 2017-05-11 16:55
 */
public class TestPostComplexClient {

    private final static Logger log = LoggerFactory.getLogger(TestPostComplexClient.class);

    @Rule
    public PostComplexMockServer server = new PostComplexMockServer(this);

    private static ForestConfiguration configuration;

    private static PostClient postClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        postClient = configuration.createInstance(PostClient.class);
    }


    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    @Test
    public void testComplexPost() {
        String result = postClient.complexPost("1", "username=foo&password=123456");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(PostMockServer.EXPECTED, result);
    }



}
