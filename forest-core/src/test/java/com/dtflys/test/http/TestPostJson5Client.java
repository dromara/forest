package com.dtflys.test.http;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.json.ForestFastjsonConverter;
import com.dtflys.forest.logging.DefaultLogHandler;
import com.dtflys.forest.logging.ForestLogger;
import com.dtflys.forest.logging.LogHandler;
import com.dtflys.test.http.client.PostClient;
import com.dtflys.test.http.model.JsonTestList;
import com.dtflys.test.http.model.JsonTestUser;
import com.dtflys.test.http.model.JsonTestUser2;
import com.dtflys.test.mock.PostJson5MockServer;
import com.dtflys.test.mock.PostJsonMockServer;
import org.junit.*;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 16:55
 */
public class TestPostJson5Client extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestPostJson5Client.class);

    @Rule
    public PostJson5MockServer server = new PostJson5MockServer(this);

    private static ForestConfiguration configuration;

    private static PostClient postClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", PostJson5MockServer.port);
        ForestFastjsonConverter fastjsonConverter = new ForestFastjsonConverter();
        fastjsonConverter.setSerializerFeature(SerializerFeature.SortField);
        configuration.setJsonConverter(fastjsonConverter);
    }

    public TestPostJson5Client(HttpBackend backend) {
        super(backend, configuration);
        postClient = configuration.createInstance(PostClient.class);
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }




    @Test
    public void testJsonPost8() {
        LogHandler logHandler = configuration.getLogHandler();
        ForestLogger logger = Mockito.mock(ForestLogger.class);
        logHandler.setLogger(logger);
        JsonTestUser2 user = new JsonTestUser2();
        user.setUsername("foo");
        String result = postClient.postJson11(user);
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostJsonMockServer.EXPECTED, result);
        Mockito.verify(logger).info("[Forest] Response: Content={\"status\": \"ok\"}");
    }

/*
    @Test
    public void testJsonPost9() {
        JsonTestUser2 user = new JsonTestUser2();
        user.setUsername("foo");
        String result = postClient.postJson12(user);
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostJsonMockServer.EXPECTED, result);
    }
*/


}
