package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.mock.PostComplexMockServer;
import com.dtflys.test.mock.PostMockServer;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.backend.okhttp3.OkHttp3Backend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.PostClient;
import com.dtflys.test.mock.PostComplexMockServer;
import com.dtflys.test.mock.PostMockServer;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 16:55
 */
public class TestPostComplexClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestPostComplexClient.class);

    @Rule
    public PostComplexMockServer server = new PostComplexMockServer(this);

    private static ForestConfiguration configuration;

    private static PostClient postClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
    }

    public TestPostComplexClient(HttpBackend backend) {
        super(backend, configuration);
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
        Assert.assertEquals(PostMockServer.EXPECTED, result);
    }



}
