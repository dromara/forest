package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.mock.PostMockServer;
import com.dtflys.test.http.client.PostClient;
import com.dtflys.test.http.model.UserParam;
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
public class TestPostClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestPostClient.class);

    @Rule
    public PostMockServer server = new PostMockServer(this);

    private static ForestConfiguration configuration;

    private static PostClient postClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", PostMockServer.port);
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
    public void testPostHello() {
        String result = postClient.postHello();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(PostMockServer.EXPECTED, result);
    }

    @Test
    public void testSimplePost() {
        String result = postClient.simplePost("text/plain");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(PostMockServer.EXPECTED, result);
    }

    @Test
    public void testSimplePostWithProxy() {
        try {
            String result = postClient.simplePostWithProxy("text/plain");
            log.info("response: " + result);
            assertNotNull(result);
            assertEquals(PostMockServer.EXPECTED, result);
        } catch (Throwable th) {}
    }


    @Test
    public void testSimplePost2() {
        String result = postClient.simplePost2();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(PostMockServer.EXPECTED, result);
    }

    @Test
    public void testSimplePost3() {
        String result = postClient.simplePost3();
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

    @Test
    public void testAnnParamPost() {
        String result = postClient.annParamPost("foo", "123456");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(PutMockServer.EXPECTED, result);
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
