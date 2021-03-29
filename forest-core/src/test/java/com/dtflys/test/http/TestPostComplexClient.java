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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
        configuration.setCacheEnabled(false);
        configuration.setVariableValue("port", PostComplexMockServer.port);
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

    @Test
    public void testComplexPost2() {
        String result = postClient.complexPost2("1", "foo", "123456");
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostMockServer.EXPECTED, result);
    }

    @Test
    public void testComplexPost3() {
        String result = postClient.complexPost3("1", "foo", "123456");
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostMockServer.EXPECTED, result);
    }

    @Test
    public void testComplexPost3Map() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("username", "foo");
        map.put("password", "123456");
        String result = postClient.complexPost3Map("1", map);
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostMockServer.EXPECTED, result);
    }


    @Test
    public void testComplexPost4() {
        String result = postClient.complexPost4("1", "foo", "123456");
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostMockServer.EXPECTED, result);
    }

}
