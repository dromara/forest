package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.PostClient;
import com.dtflys.test.mock.PostJsonMockServer;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 16:55
 */
public class TestPostJsonClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestPostJsonClient.class);

    @Rule
    public PostJsonMockServer server = new PostJsonMockServer(this);

    private static ForestConfiguration configuration;

    private static PostClient postClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
    }

    public TestPostJsonClient(HttpBackend backend) {
        super(backend, configuration);
        postClient = configuration.createInstance(PostClient.class);
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    @Test
    public void testJsonPost() {
        String result = postClient.postJson("foo", "123456");
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostJsonMockServer.EXPECTED, result);
    }

    @Test
    public void testJsonPost2() {
        String result = postClient.postJson2("foo", "123456");
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostJsonMockServer.EXPECTED, result);
    }



}
