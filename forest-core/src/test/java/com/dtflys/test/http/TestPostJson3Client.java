package com.dtflys.test.http;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.json.ForestFastjsonConverter;
import com.dtflys.forest.logging.ForestLogger;
import com.dtflys.test.http.client.PostClient;
import com.dtflys.test.http.model.JsonTestList;
import com.dtflys.test.http.model.JsonTestUser;
import com.dtflys.test.mock.PostJson3MockServer;
import com.dtflys.test.mock.PostJsonMockServer;
import org.junit.*;
import org.mockito.Mockito;
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
public class TestPostJson3Client extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestPostJson3Client.class);

    @Rule
    public PostJson3MockServer server = new PostJson3MockServer(this);

    private static ForestConfiguration configuration;

    private static PostClient postClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", PostJson3MockServer.port);
        ForestFastjsonConverter fastjsonConverter = new ForestFastjsonConverter();
        fastjsonConverter.setSerializerFeature(SerializerFeature.SortField);
        configuration.setJsonConverter(fastjsonConverter);
    }

    public TestPostJson3Client(HttpBackend backend) {
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
        List<JsonTestList> list = new ArrayList<>();
        list.add(testList);
        String result = postClient.postJson7(list);
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostJson3MockServer.EXPECTED, result);
    }


    @Test
    public void testJsonPost8() {
        JsonTestUser user = new JsonTestUser();
        user.setUsername("foo");
        JsonTestList testList = new JsonTestList();
        testList.setUserList(Arrays.asList(user));
        List<JsonTestList> list = new ArrayList<>();
        list.add(testList);
        String result = postClient.postJson8(list);
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostJson3MockServer.EXPECTED, result);
    }

    @Test
    public void testJsonPost12() {
        ForestLogger logger = Mockito.mock(ForestLogger.class);
        configuration.getLogHandler().setLogger(logger);

        JsonTestUser user = new JsonTestUser();
        user.setUsername("foo");
        JsonTestList testList = new JsonTestList();
        testList.setUserList(Arrays.asList(user));
        List<JsonTestList> list = new ArrayList<>();
        list.add(testList);
        String result = postClient.postJson12(list);
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostJson3MockServer.EXPECTED, result);

        Mockito.verify(logger, Mockito.never()).info("[Forest] Request: \n" +
                "\tPOST http://localhost:5016/json HTTP\n" +
                "\tHeaders: \n" +
                "\t\tContent-Type: application/json; charset=utf-8\n" +
                "\tBody: [{\"userList\":[{\"username\":\"foo\"}]}]");
        Mockito.verify(logger).info("[Forest] Response Content:\n\t{\"status\": \"ok\"}");

    }


}
