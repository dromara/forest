package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.PostClient;
import com.dtflys.test.mock.PostComplexMockServer;
import com.dtflys.test.mock.PostHeadMockServer;
import com.dtflys.test.mock.PostMockServer;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 16:55
 */
public class TestPostHeadClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestPostHeadClient.class);

    @Rule
    public PostHeadMockServer server = new PostHeadMockServer(this);

    private static ForestConfiguration configuration;

    private static PostClient postClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setCacheEnabled(false);
        configuration.setVariableValue("port", PostHeadMockServer.port);
    }

    public TestPostHeadClient(HttpBackend backend) {
        super(backend, configuration);
        postClient = configuration.createInstance(PostClient.class);
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    @Test
    public void testPostHead() {
        String result = postClient.postHead("username=foo&password=123456");
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostMockServer.EXPECTED, result);
    }

    @Test
    public void testPostHead2() {
        String result = postClient.postHead2("username=foo&password=123456");
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostMockServer.EXPECTED, result);
    }


}
