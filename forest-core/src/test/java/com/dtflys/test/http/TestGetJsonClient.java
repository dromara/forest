package com.dtflys.test.http;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.json.ForestFastjsonConverter;
import com.dtflys.test.http.client.PostClient;
import com.dtflys.test.mock.PostJsonMockServer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 16:55
 */
public class TestGetJsonClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestGetJsonClient.class);

    @Rule
    public PostJsonMockServer server = new PostJsonMockServer(this);

    private static ForestConfiguration configuration;

    private static PostClient postClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", PostJsonMockServer.port);
        ForestFastjsonConverter fastjsonConverter = new ForestFastjsonConverter();
        fastjsonConverter.setSerializerFeature(SerializerFeature.SortField);
        configuration.setJsonConverter(fastjsonConverter);
    }

    public TestGetJsonClient(HttpBackend backend) {
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

    @Test
    public void testJsonPost3() {
        String result = postClient.postJson3("foo", "123456");
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostJsonMockServer.EXPECTED, result);
    }

    @Test
    public void testJsonPost4() {
        String result = postClient.postJson4("foo", "123456");
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostJsonMockServer.EXPECTED, result);
    }

/*
    @Test
    public void testJsonPost5() {
        SimpleUser user = new SimpleUser();
        user.setUsername("foo");
        user.setPassword("123456");
        String result = postClient.postJson5(user);
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostJsonMockServer.EXPECTED, result);
    }

    @Test
    public void testJsonPost6() {
        SimpleUser user = new SimpleUser();
        user.setUsername("foo");
        user.setPassword("123456");
        String result = postClient.postJson6(user);
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostJsonMockServer.EXPECTED, result);
    }
*/



}
