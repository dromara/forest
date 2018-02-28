package org.forest.test.http;

import org.forest.backend.HttpBackend;
import org.forest.backend.okhttp3.OkHttp3Backend;
import org.forest.config.ForestConfiguration;
import org.forest.test.http.client.PostClient;
import org.forest.test.http.model.UserParam;
import org.forest.test.mock.PostMockServer;
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
public class TestPostClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestPostClient.class);

    @Rule
    public PostMockServer server = new PostMockServer(this);

    private static ForestConfiguration configuration;

    private static PostClient postClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
    }

    public TestPostClient(HttpBackend backend) {
        super(backend, configuration);
        postClient = configuration.createInstance(PostClient.class);
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    @Test
    public void testSimplePost() {
        String result = postClient.simplePost();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(PostMockServer.EXPECTED, result);
    }

    @Test
    public void testTextParamPost() {
        String result = postClient.textParamPost("foo", "123456");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(PostMockServer.EXPECTED, result);
    }

    @Test
    public void testAnnParamPost() {
        String result = postClient.textParamPost("foo", "123456");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(PostMockServer.EXPECTED, result);
    }

    @Test
    public void testVarParamPost() {
        String result = postClient.varParamPost("foo", "123456");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(PostMockServer.EXPECTED, result);
    }

    @Test
    public void testModelParamPost() {
        UserParam userParam = new UserParam();
        userParam.setUsername("foo");
        userParam.setPassword("123456");
        String result = postClient.modelParamPost(userParam);
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(PostMockServer.EXPECTED, result);
    }


/*
    @Test
    public void testModelParamNumPost() {
        UserParam userParam = new UserParam();
        userParam.setUsername("foo");
        userParam.setPassword("123456");
        String result = postClient.modelParamNumPost(userParam);
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(PostMockServer.EXPECTED, result);
    }
*/


}
