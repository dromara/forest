package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.PostClient;
import com.dtflys.test.http.model.UserParam;
import com.dtflys.test.mock.PostEmptyBodyMockServer;
import com.dtflys.test.mock.PostMockServer;
import com.dtflys.test.mock.PutMockServer;
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
public class TestPostEmptyBodyClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestPostEmptyBodyClient.class);

    @Rule
    public PostEmptyBodyMockServer server = new PostEmptyBodyMockServer(this);

    private static ForestConfiguration configuration;

    private static PostClient postClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", PostEmptyBodyMockServer.port);
    }

    public TestPostEmptyBodyClient(HttpBackend backend) {
        super(backend, configuration);
        postClient = configuration.createInstance(PostClient.class);
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    @Test
    public void testPostEmptyBody() {
        String result = postClient.emptyBody();
        assertEquals(PostEmptyBodyMockServer.EXPECTED, result);
    }

}
