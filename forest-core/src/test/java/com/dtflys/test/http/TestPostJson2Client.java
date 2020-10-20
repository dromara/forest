package com.dtflys.test.http;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.json.ForestFastjsonConverter;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.logging.ForestLogger;
import com.dtflys.forest.logging.RequestLogMessage;
import com.dtflys.test.http.client.PostClient;
import com.dtflys.test.http.model.JsonTestUser;
import com.dtflys.test.mock.PostJson2MockServer;
import com.dtflys.test.mock.PostJsonMockServer;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 16:55
 */
public class TestPostJson2Client extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestPostJson2Client.class);

    @Rule
    public PostJson2MockServer server = new PostJson2MockServer(this);

    private static ForestConfiguration configuration;

    private static PostClient postClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", PostJson2MockServer.port);
        ForestFastjsonConverter fastjsonConverter = new ForestFastjsonConverter();
        fastjsonConverter.setSerializerFeature(SerializerFeature.SortField);
        configuration.setJsonConverter(fastjsonConverter);
    }

    public TestPostJson2Client(HttpBackend backend) {
        super(backend, configuration);
        postClient = configuration.createInstance(PostClient.class);
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }


    @Test
    public void testJsonPost5() {
        ForestLogger logger = Mockito.mock(ForestLogger.class);
        configuration.getLogHandler().setLogger(logger);
        JsonTestUser user = new JsonTestUser();
        user.setUsername("foo");
        ForestResponse<String> response = postClient.postJson5("foo", "1111111111111");
        String result = response.getResult();
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostJson2MockServer.EXPECTED, result);
        ForestRequest request = response.getRequest();
        assertNotNull(result);
        RequestLogMessage logMessage = request.getRequestLogMessage();
        assertNotNull(logMessage);
        assertNotNull(logMessage.getRequest());
        Mockito.verify(logger).info("[Forest] Request: \n" +
                "\tPOST http://localhost:5015/json HTTP\n" +
                "\tHeaders: \n" +
                "\t\tAccept: application/json\n" +
                "\t\tAuthorization: 1111111111111\n" +
                "\t\tContent-Type: application/json\n" +
                "\tBody: {\"username\":\"foo\"}");
    }

    @Test
    public void testJsonPost5Map() {
        ForestLogger logger = Mockito.mock(ForestLogger.class);
        configuration.getLogHandler().setLogger(logger);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", "foo");
        String result = postClient.postJson5Map(userMap);
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostJson2MockServer.EXPECTED, result);
        Mockito.verify(logger).info("[Forest] Request: \n" +
                "\tPOST http://localhost:5015/json HTTP\n" +
                "\tHeaders: \n" +
                "\t\tContent-Type: application/json\n" +
                "\tBody: {\"username\":\"foo\"}");

    }

    @Test
    public void testJsonPost5Map2() {
        ForestLogger logger = Mockito.mock(ForestLogger.class);
        configuration.getLogHandler().setLogger(logger);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", "foo");
        String result = postClient.postJson5Map2(userMap);
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostJson2MockServer.EXPECTED, result);
        Mockito.verify(logger, Mockito.never()).info("[Forest] Request: \n" +
                "\tPOST http://localhost:5015/json HTTP\n" +
                "\tHeaders: \n" +
                "\t\tContent-Type: application/json\n" +
                "\tBody: {\"username\":\"foo\"}");
    }



    @Test
    public void testJsonPost6() {
        ForestLogger logger = Mockito.mock(ForestLogger.class);
        configuration.getLogHandler().setLogger(logger);
        JsonTestUser user = new JsonTestUser();
        user.setUsername("foo");
        String result = postClient.postJson6(user);
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostJson2MockServer.EXPECTED, result);
        Mockito.verify(logger, Mockito.never()).info("[Forest] Request: \n" +
                "\tPOST http://localhost:5015/json HTTP\n" +
                "\tHeaders: \n" +
                "\t\tContent-Type: application/json\n" +
                "\tBody: {\"username\":\"foo\"}");
    }

    @Test
    public void testJsonPost11() {
        ForestLogger logger = Mockito.mock(ForestLogger.class);
        configuration.getLogHandler().setLogger(logger);
        JsonTestUser user = new JsonTestUser();
        user.setUsername("foo");
        String result = postClient.postJson11(user);
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostJson2MockServer.EXPECTED, result);
        Mockito.verify(logger).info("[Forest] Request: \n" +
                "\tPOST http://localhost:5015/json HTTP\n" +
                "\tHeaders: \n" +
                "\t\tAccept-Encoding: UTF-8\n" +
                "\t\tContent-Type: application/json\n" +
                "\tBody: {\"username\":\"foo\"}");
        Mockito.verify(logger).info("[Forest] Response: Content=成功访问");
    }


}
