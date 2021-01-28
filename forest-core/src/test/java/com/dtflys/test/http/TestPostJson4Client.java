package com.dtflys.test.http;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.json.ForestFastjsonConverter;
import com.dtflys.test.http.client.PostClient;
import com.dtflys.test.http.model.JsonTestList;
import com.dtflys.test.http.model.JsonTestUser;
import com.dtflys.test.mock.PostJson4MockServer;
import com.dtflys.test.mock.PostJsonMockServer;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 16:55
 */
public class TestPostJson4Client extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestPostJson4Client.class);

    @Rule
    public PostJson4MockServer server = new PostJson4MockServer(this);

    private static ForestConfiguration configuration;

    private static PostClient postClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", PostJson4MockServer.port);
        ForestFastjsonConverter fastjsonConverter = new ForestFastjsonConverter();
        fastjsonConverter.setSerializerFeature(SerializerFeature.SortField);
        configuration.setJsonConverter(fastjsonConverter);
    }

    public TestPostJson4Client(HttpBackend backend) {
        super(backend, configuration);
        postClient = configuration.createInstance(PostClient.class);
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }


    @Test
    public void testJsonPost7() {
        JsonTestUser user = new JsonTestUser();
        user.setUsername("foo");
        JsonTestList testList = new JsonTestList();
        testList.setUserList(Arrays.asList(user));
        String result = postClient.postJson9(testList);
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostJson4MockServer.EXPECTED, result);
    }


    @Test
    public void testJsonPost8() {
        JsonTestUser user = new JsonTestUser();
        user.setUsername("foo");
        JsonTestList testList = new JsonTestList();
        testList.setUserList(Arrays.asList(user));
        String result = postClient.postJson10(testList);
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostJson4MockServer.EXPECTED, result);
    }

    @Test
    public void testJsonPost8WithDefaultBody() {
        String result = postClient.postJson10WithDefaultBody(null);
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostJson4MockServer.EXPECTED, result);
    }


}
