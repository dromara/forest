package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.logging.ForestLogHandler;
import com.dtflys.forest.logging.ForestLogger;
import com.dtflys.forest.logging.RequestLogMessage;
import com.dtflys.forest.utils.Base64Utils;
import com.dtflys.test.http.client.EmptyJsonClient;
import com.dtflys.test.http.client.PostClient;
import com.dtflys.test.http.model.Cause;
import com.dtflys.test.http.model.FormArrayParam;
import com.dtflys.test.http.model.FormListParam;
import com.dtflys.test.http.model.JsonTestList;
import com.dtflys.test.http.model.JsonTestUser;
import com.dtflys.test.http.model.JsonTestUser2;
import com.dtflys.test.http.model.JsonTestUser3;
import com.dtflys.test.http.model.UserParam;
import com.dtflys.test.http.model.XmlTestParam;
import com.google.common.collect.Lists;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.Assertions.*;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 16:55
 */
public class TestPostClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\":\"ok\"}";

    public final static String CN_EXPECTED = "{\"status\": \"ok\", \"msg\": \"中文\"}";

    @Rule
    public final MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private final PostClient postClient;

    private final EmptyJsonClient emptyJsonClient;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    @Override
    public void afterRequests() {
    }

    public TestPostClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        postClient = configuration.createInstance(PostClient.class);
        emptyJsonClient = configuration.createInstance(EmptyJsonClient.class);
    }


    @Test
    public void testPostHello() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(postClient.postHello())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/hello")
                .assertHeaderEquals("Accept", "text/plain")
                .assertHeaderEquals("Content-Type", "application/x-www-form-urlencoded")
                .assertBodyEquals("username=foo&password=123456");
    }

    @Test
    public void testSimplePost() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(postClient.simplePost("text/plain"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/hello")
                .assertHeaderEquals("Accept", "text/plain")
                .assertHeaderEquals("Content-Type", "application/x-www-form-urlencoded")
                .assertBodyEquals("username=foo&password=123456");
    }

    @Test
    public void testSimplePostWithProxy() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(postClient.simplePostWithProxy(server.getPort(), "text/plain", "xxxyyy"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertRequestLineEquals("POST http://localhost:" + server.getPort() + "/hello HTTP/1.1")
                .assertPathEquals("/")
                .assertHeaderEquals("Accept", "text/plain")
                .assertBodyEquals("username=foo&password=123456");
    }


    @Test
    public void testSimplePost2() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(postClient.simplePost2())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/hello")
                .assertHeaderEquals("Accept", "text/plain")
                .assertBodyEquals("username=foo&password=123456");
    }

    @Test
    public void testSimplePost3() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(postClient.simplePost3())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/hello")
                .assertHeaderEquals("Accept", "text/plain")
                .assertBodyEquals("username=foo&password=123456");
    }


    @Test
    public void testTextParamPost() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(postClient.textParamPost("foo", "123456"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/hello")
                .assertHeaderEquals("Accept", "text/plain")
                .assertBodyEquals("username=foo&password=123456");
    }

    @Test
    public void testVarParamPost() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(postClient.varParamPost("foo", "123456"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/hello")
                .assertHeaderEquals("Accept", "text/plain")
                .assertBodyEquals("username=foo&password=123456");
    }

    @Test
    public void testModelParamPost() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        UserParam userParam = new UserParam();
        userParam.setUsername("foo");
        userParam.setPassword("123456");
        assertThat(postClient.modelParamPost(userParam))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/hello")
                .assertHeaderEquals("Accept", "text/plain")
                .assertBodyEquals("username=foo&password=123456");
    }

    @Test
    public void testAnnParamPost() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(postClient.annParamPost("foo", "123456"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/hello")
                .assertHeaderEquals("Accept", "text/plain")
                .assertBodyEquals("username=foo&password=123456");
    }

    @Test
    public void testListBodyPost() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        List<String> list = Lists.newArrayList("xx", "yy", "zz");
        assertThat(postClient.listBodyPost(list))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/hello-list")
                .assertHeaderEquals("Accept", "text/plain")
                .assertHeaderEquals("Content-Type", "application/x-www-form-urlencoded")
                .assertBodyEquals("item_0=xx&item_1=yy&item_2=zz");
    }

    @Test
    public void testEmptyJsonMap() throws InterruptedException {
        Map<String, Object> map = new HashMap<>();
        server.enqueue(new MockResponse().setBody("success"));
        assertThat(emptyJsonClient.postEmptyJsonMap(map))
                .isNotNull()
                .isEqualTo("success");
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/empty/map")
                .assertHeaderEquals("Content-Type", "application/json")
                .assertBodyEquals("{}");
    }

    @Test
    public void testEmptyJson2Map() throws InterruptedException {
        Map<String, Object> map = new HashMap<>();
        server.enqueue(new MockResponse().setBody("success"));
        assertThat(emptyJsonClient.postEmptyJson2Map(map, map))
                .isNotNull()
                .isEqualTo("success");
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/empty/map")
                .assertHeaderEquals("Content-Type", "application/json")
                .assertBodyEquals("{}");
    }

    @Test
    public void testEmptyJsonString() throws InterruptedException {
        Map<String, Object> map = new HashMap<>();
        server.enqueue(new MockResponse().setBody("success"));
        assertThat(emptyJsonClient.postEmptyJsonString(map))
                .isNotNull()
                .isEqualTo("success");
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/empty/map")
                .assertHeaderEquals("Content-Type", "application/json")
                .assertBodyEquals("{}");
    }

    @Test
    public void testEmptyJsonStringWithParams() throws InterruptedException {
        Map<String, Object> map = new HashMap<>();
        server.enqueue(new MockResponse().setBody("success"));
        assertThat(emptyJsonClient.postEmptyJsonStringWithParams("ok", map))
                .isNotNull()
                .isEqualTo("success");
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/empty/map/ok")
                .assertHeaderEquals("Content-Type", "application/json")
                .assertBodyEquals("{}");
    }

    @Test
    public void testEmptyBody() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(postClient.emptyBody())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/hello")
                .assertHeaderEquals("Accept", "text/plain")
                .assertBodyEmpty();
    }

    @Test
    public void testComplexPost() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(postClient.complexPost("1", "username=foo&password=123456"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/complex")
                .assertHeaderEquals("Accept", "text/plain")
                .assertQueryEquals("param", "1")
                .assertBodyEquals("username=foo&password=123456");
    }

    @Test
    public void testComplexPost2() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(postClient.complexPost2("1", "foo", "123456"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/complex")
                .assertHeaderEquals("Accept", "text/plain")
                .assertQueryEquals("param", "1")
                .assertBodyEquals("username=foo&password=123456");
    }

    @Test
    public void testComplexPost3() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(postClient.complexPost3("1", "foo", "123456"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/complex")
                .assertHeaderEquals("Accept", "text/plain")
                .assertQueryEquals("param", "1")
                .assertBodyEquals("username=foo&password=123456");
    }

    @Test
    public void testComplexPost3Map() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("username", "foo");
        map.put("password", "123456");
        assertThat(postClient.complexPost3Map("1", map))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/complex")
                .assertHeaderEquals("Accept", "text/plain")
                .assertQueryEquals("param", "1")
                .assertBodyEquals("username=foo&password=123456");
    }

    @Test
    public void testComplexPost4() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(postClient.complexPost4("1", "foo", "123456"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/complex")
                .assertHeaderEquals("Accept", "text/plain")
                .assertQueryEquals("param", "1")
                .assertBodyEquals("username=foo&password=123456");
    }

    @Test
    public void testPostHead() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(postClient.postHead("username=foo&password=123456"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Accept", "text/plain")
                .assertBodyEquals("username=foo&password=123456");
    }

    @Test
    public void testPostHead2() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(postClient.postHead2("username=foo&password=123456"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Accept", "text/plain")
                .assertBodyEquals("username=foo&password=123456");
    }

    @Test
    public void testPostJsonByteArray() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String json = "{\"a\": \"1\", \"b\": \"2\"}";
        byte[] data = Base64Utils.encodeToByteArray(json);
        assertThat(postClient.postJsonByteArray(data))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertBodyEquals(data);
    }

    @Test
    public void testPostJsonWithCnCharacters() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(CN_EXPECTED));
        assertThat(postClient.postJsonWithCnCharacters("foo", "123456&&++===", "中文名"))
                .isNotNull()
                .isEqualTo(CN_EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json; charset=utf-8")
                .assertBodyEquals("{\"username\":\"foo\",\"password\":\"123456&&++===\",\"cn_name\":\"中文名\"}");
    }


    @Test
    public void testPostJsonWithCnCharacters2() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(CN_EXPECTED));
        assertThat(postClient.postJsonWithCnCharacters2("foo", "123456&&++===", "中文名"))
                .isNotNull()
                .isEqualTo(CN_EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json; charset=utf-8")
                .assertBodyEquals("{\"username\":\"foo\",\"password\":\"123456&&++===\",\"cn_name\":\"中文名\"}");
    }

    @Test
    public void testPostJsonWithCnCharacters3() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(CN_EXPECTED));
        assertThat(postClient.postJsonWithCnCharacters3("foo", "123456&&++===", "中文名"))
                .isNotNull()
                .isEqualTo(CN_EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json; charset=utf-8")
                .assertBodyEquals("{\"username\":\"foo\",\"password\":\"123456&&++===\",\"cn_name\":\"中文名\"}");
    }

    @Test
    public void testPostJsonWithCnCharacters4() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(CN_EXPECTED));
        assertThat(postClient.postJsonWithCnCharacters4("foo", "123456&&++===", "中文名"))
                .isNotNull()
                .isEqualTo(CN_EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json; charset=utf-8")
                .assertBodyEquals("{\"username\":\"foo\",\"password\":\"123456&&++===\",\"cn_name\":\"中文名\"}");
    }

    @Test
    public void testPostJsonWithCnCharacters5() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(CN_EXPECTED));
        JsonTestUser3 user = new JsonTestUser3();
        user.setUsername("foo");
        user.setPassword("123456&&++===");
        user.setCnName("中文名");
        assertThat(postClient.postJsonWithCnCharacters5(user))
                .isNotNull()
                .isEqualTo(CN_EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json; charset=utf-8")
                .assertBodyEquals("{\"username\":\"foo\",\"password\":\"123456&&++===\",\"cn_name\":\"中文名\"}");
    }

    @Test
    public void testPostJsonWithCnCharacters6() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(CN_EXPECTED));
        JsonTestUser3 user = new JsonTestUser3();
        user.setUsername("foo");
        user.setPassword("123456&&++===");
        user.setCnName("中文名");
        assertThat(postClient.postJsonWithCnCharacters6(user))
                .isNotNull()
                .isEqualTo(CN_EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json; charset=utf-8")
                .assertBodyEquals("{\"username\":\"foo\",\"password\":\"123456&&++===\",\"cn_name\":\"中文名\"}");
    }

    @Test
    public void testPostJsonWithCnCharacters7() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(CN_EXPECTED));
        JsonTestUser3 user = new JsonTestUser3();
        user.setUsername("foo");
        user.setPassword("123456&&++===");
        user.setCnName("中文名");
        assertThat(postClient.postJsonWithCnCharacters7(user))
                .isNotNull()
                .isEqualTo(CN_EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json; charset=utf-8")
                .assertBodyEquals("{\"username\":\"foo\",\"password\":\"123456&&++===\",\"cn_name\":\"中文名\"}");
    }

    @Test
    public void testPostBodyCnStringWithBodyAnn() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(CN_EXPECTED));
        assertThat(postClient.postBodyCnStringWithBodyAnn(
                "{\"username\":\"foo\",\"password\":\"123456&&++===\",\"cn_name\":\"中文名\"}"))
                .isNotNull()
                .isEqualTo(CN_EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json; charset=utf-8")
                .assertBodyEquals("{\"username\":\"foo\",\"password\":\"123456&&++===\",\"cn_name\":\"中文名\"}");
    }

    @Test
    public void testPostBodyCnStringWithBodyAnnAndEmptyName() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(CN_EXPECTED));
        assertThat(postClient.postBodyCnStringWithBodyAnnAndEmptyName(
                "{\"username\":\"foo\",\"password\":\"123456&&++===\",\"cn_name\":\"中文名\"}"))
                .isNotNull()
                .isEqualTo(CN_EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json; charset=utf-8")
                .assertBodyEquals("{\"username\":\"foo\",\"password\":\"123456&&++===\",\"cn_name\":\"中文名\"}");
    }


    @Test
    public void testPostBodyCnStringWithDefaultBody() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(CN_EXPECTED));
        assertThat(postClient.postBodyCnStringWithDefaultBody(null))
                .isNotNull()
                .isEqualTo(CN_EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json; charset=utf-8")
                .assertBodyEquals("{\"username\":\"foo\",\"password\":\"123456&&++===\",\"cn_name\":\"中文名\"}");
    }

    @Test
    public void testPostJsonWithLog() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestLogger logger = Mockito.mock(ForestLogger.class);
        configuration.getLogHandler().setLogger(logger);
        JsonTestUser user = new JsonTestUser();
        user.setUsername("foo");
        ForestResponse<String> response = postClient.postJsonWithLog("foo", "1111111111111");
        assertThat(response).isNotNull();
        assertThat(response.getResult())
                .isNotNull()
                .isEqualTo(EXPECTED);
        ForestRequest request = response.getRequest();
        assertThat(request.getRequestLogMessage())
                .isNotNull()
                .extracting(RequestLogMessage::getRequest)
                .isEqualTo(request);
        Mockito.verify(logger).info("[Forest] Request (" + configuration.getBackend().getName() + "): \n" +
                "\tPOST http://localhost:" + server.getPort() + "/json HTTP\n" +
                "\tHeaders: \n" +
                "\t\tAccept: application/json\n" +
                "\t\tAuthorization: 1111111111111\n" +
                "\t\tContent-Type: application/json\n" +
                "\tBody: {\"username\":\"foo\"}");
    }

    @Test
    public void testPostJsonMapWithLog() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestLogger logger = Mockito.mock(ForestLogger.class);
        configuration.getLogHandler().setLogger(logger);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", "foo");
        assertThat(postClient.postJsonMapWithLog(userMap))
                .isNotNull()
                .isEqualTo(EXPECTED);
        Mockito.verify(logger).info("[Forest] Request (" + configuration.getBackend().getName() + "): \n" +
                "\tPOST http://localhost:" + server.getPort() + "/json HTTP\n" +
                "\tHeaders: \n" +
                "\t\tContent-Type: application/json\n" +
                "\tBody: {\"username\":\"foo\"}");
    }

    @Test
    public void testPostJsonMapWithoutLog() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestLogger logger = Mockito.mock(ForestLogger.class);
        configuration.getLogHandler().setLogger(logger);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", "foo");
        assertThat(postClient.postJsonMapWithoutLog(userMap))
                .isNotNull()
                .isEqualTo(EXPECTED);
        Mockito.verify(logger, Mockito.never()).info("[Forest] Request (" + configuration.getBackend().getName() + "): \n" +
                "\tPOST http://localhost:" + server.getPort() + "/json HTTP\n" +
                "\tHeaders: \n" +
                "\t\tContent-Type: application/json\n" +
                "\tBody: {\"username\":\"foo\"}");
    }

    @Test
    public void testPostJsonObjectWithoutLog() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestLogger logger = Mockito.mock(ForestLogger.class);
        configuration.getLogHandler().setLogger(logger);
        JsonTestUser user = new JsonTestUser();
        user.setUsername("foo");
        assertThat(postClient.postJsonObjectWithoutLog(user))
                .isNotNull()
                .isEqualTo(EXPECTED);
        Mockito.verify(logger, Mockito.never()).info("[Forest] Request (" + configuration.getBackend().getName() + "): \n" +
                "\tPOST http://localhost:" + server.getPort() + "/json HTTP\n" +
                "\tHeaders: \n" +
                "\t\tContent-Type: application/json\n" +
                "\tBody: {\"username\":\"foo\"}");
    }

    @Test
    public void testPostJsonObjectWithLog_content_noStatus() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestLogger logger = Mockito.mock(ForestLogger.class);
        configuration.getLogHandler().setLogger(logger);
        JsonTestUser user = new JsonTestUser();
        user.setUsername("foo");
        assertThat(postClient.postJsonObjectWithLog_content_noStatus(user))
                .isNotNull()
                .isEqualTo(EXPECTED);
        Mockito.verify(logger).info("[Forest] Request (" + configuration.getBackend().getName() + "): \n" +
                "\tPOST http://localhost:" + server.getPort() + "/json HTTP\n" +
                "\tHeaders: \n" +
                "\t\tAccept-Encoding: UTF-8\n" +
                "\t\tContent-Type: application/json\n" +
                "\tBody: {\"username\":\"foo\"}");
        Mockito.verify(logger).info("[Forest] Response Content:\n\t" + EXPECTED);
    }

    @Test
    public void testPostJsonObjectWithLog_content_noStatus2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestLogHandler logHandler = configuration.getLogHandler();
        ForestLogger logger = Mockito.mock(ForestLogger.class);
        logHandler.setLogger(logger);
        JsonTestUser2 user = new JsonTestUser2();
        user.setUsername("foo");
        assertThat(postClient.postJsonObjectWithLog_content_noStatus(user))
                .isNotNull()
                .isEqualTo(EXPECTED);
        Mockito.verify(logger).info("[Forest] Response Content:\n\t" + EXPECTED);
    }

    @Test
    public void testPostJsonObjListWithLog_content_noRequest_noStatus() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestLogger logger = Mockito.mock(ForestLogger.class);
        configuration.getLogHandler().setLogger(logger);

        JsonTestUser user = new JsonTestUser();
        user.setUsername("foo");
        JsonTestList testList = new JsonTestList();
        testList.setUserList(Collections.singletonList(user));
        List<JsonTestList> list = new ArrayList<>();
        list.add(testList);
        assertThat(postClient.postJsonObjListWithLog_content_noRequest_noStatus(list))
                .isNotNull()
                .isEqualTo(EXPECTED);
        Mockito.verify(logger, Mockito.never()).info("[Forest] Request (" + configuration.getBackend().getName() + "): \n" +
                "\tPOST http://localhost:" + server.getPort() + "/json HTTP\n" +
                "\tHeaders: \n" +
                "\t\tContent-Type: application/json; charset=utf-8\n" +
                "\tBody: [{\"userList\":[{\"username\":\"foo\"}]}]");
        Mockito.verify(logger).info("[Forest] Response Content:\n\t" + EXPECTED);
    }


    @Test
    public void testJsonPostBodyMap() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", "foo");
        assertThat(postClient.postJsonBodyMap(userMap))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json")
                .assertBodyEquals("{\"username\":\"foo\"}");
    }

    @Test
    public void testJsonPostBodyMapWithDefaultBody() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(postClient.postJsonBodyMapWithDefaultBody(null))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json")
                .assertBodyEquals("{\"username\":\"foo\"}");
    }

    @Test
    public void testJsonPostBodyMap2() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", "foo");
        assertThat(postClient.postJsonBodyMap2(userMap))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json")
                .assertBodyEquals("{\"username\":\"foo\"}");
    }

    @Test
    public void testJsonPostBodyObj() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        JsonTestUser user = new JsonTestUser();
        user.setUsername("foo");
        assertThat(postClient.postJsonBodyObj(user))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json")
                .assertBodyEquals("{\"username\":\"foo\"}");
    }

    @Test
    public void testJsonPostBodyField() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(postClient.postJsonBodyField("foo"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json")
                .assertBodyEquals("{\"username\":\"foo\"}");
    }


    @Test
    public void testJsonPostBodyString() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(postClient.postJsonBodyString("{\"username\":\"foo\"}"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json")
                .assertBodyEquals("{\"username\":\"foo\"}");
    }

    @Test
    public void testJsonPostBodyMapErrorCheck() {
        server.enqueue(new MockResponse().setResponseCode(404));
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", "foo");
        String result = null;
        boolean error = false;
        try {
            result = postClient.postJsonBodyMapError(userMap, "application/xml");
        } catch (Throwable th) {
            error = true;
        }
        assertThat(result).isNull();
        assertThat(error).isTrue();
    }

    @Test
    public void testJsonPostBodyMapErrorCheck_500() {
        server.enqueue(new MockResponse().setResponseCode(500).setBody(EXPECTED));
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", "foo");
        ForestResponse<String> response = null;
        response = postClient.postJsonBodyMapError2(userMap, "application/xml");
        assertThat(response.getContent()).isNotNull().isEqualTo(EXPECTED);
        assertThat(response.getStatusCode()).isEqualTo(500);
    }


    @Test
    public void testJsonPostBodyMapErrorCheck2() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(EXPECTED));
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", "foo");
        String result = null;
        boolean error;
        try {
            result = postClient.postJsonBodyMapError(userMap, "application/json");
            error = false;
        } catch (Throwable th) {
            error = true;
        }
        assertThat(result).isNotNull();
        assertThat(error).isFalse();

    }

    @Test
    public void testPostJsonObjList() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        JsonTestUser user = new JsonTestUser();
        user.setUsername("foo");
        JsonTestList testList = new JsonTestList();
        testList.setUserList(Collections.singletonList(user));
        List<JsonTestList> list = new ArrayList<>();
        list.add(testList);
        assertThat(postClient.postJsonObjListWithDataObjectAnn(list))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json; charset=utf-8")
                .assertBodyEquals("[{\"userList\":[{\"username\":\"foo\"}]}]");
    }

    @Test
    public void testPostJsonObjListInData() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        JsonTestUser user = new JsonTestUser();
        user.setUsername("foo");
        JsonTestList testList = new JsonTestList();
        testList.setUserList(Collections.singletonList(user));
        List<JsonTestList> list = new ArrayList<>();
        list.add(testList);
        assertThat(postClient.postJsonObjListInDataProperty(list))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json; charset=utf-8")
                .assertBodyEquals("[{\"userList\":[{\"username\":\"foo\"}]}]");
    }

    @Test
    public void testPostJsonListInObjWithDataObjectAnn() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        JsonTestUser user = new JsonTestUser();
        user.setUsername("foo");
        JsonTestList testList = new JsonTestList();
        testList.setUserList(Collections.singletonList(user));
        assertThat(postClient.postJsonListInObjWithDataObjectAnn(testList))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json; charset=utf-8")
                .assertBodyEquals("{\"userList\":[{\"username\":\"foo\"}]}");
    }

    @Test
    public void testPostJsonListInObjInDataProperty() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        JsonTestUser user = new JsonTestUser();
        user.setUsername("foo");
        JsonTestList testList = new JsonTestList();
        testList.setUserList(Collections.singletonList(user));
        assertThat(postClient.postJsonListInObjInDataProperty(testList))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json; charset=utf-8")
                .assertBodyEquals("{\"userList\":[{\"username\":\"foo\"}]}");
    }

    @Test
    public void testPostJsonListObjWithDefaultBody() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(postClient.postJsonListObjWithDefaultBody(null))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json; charset=utf-8")
                .assertBodyEquals("{\"userList\":[{\"username\":\"foo\"}]}");
    }

    @Test
    public void testPostJsonMapWithBodyAnn() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Map<String, Object> obj = new LinkedHashMap<>();
        List<String> data = Lists.newArrayList("A", "B", "C");
        obj.put("name", "test");
        obj.put("data", data);
        assertThat(postClient.postJsonMapWithBodyAnn(obj))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json; charset=utf-8")
                .assertBodyEquals("{\"name\":\"test\",\"data\":[\"A\",\"B\",\"C\"]}");
    }

    @Test
    public void testPostJsonObjFromMultipleBodyAnnParams() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        List<String> data = Lists.newArrayList("A", "B", "C");
        assertThat(postClient.postJsonObjFromMultipleBodyAnnParams("test", data))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json; charset=utf-8")
                .assertBodyEquals("{\"name\":\"test\",\"data\":[\"A\",\"B\",\"C\"]}");
    }

    @Test
    public void testPostJsonObjFromMultipleBodyAnnParams2() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(postClient.postJsonObjFromMultipleBodyAnnParams2(null, null))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/json")
                .assertHeaderEquals("Content-Type", "application/json; charset=utf-8")
                .assertBodyEquals("{\"name\":\"test\",\"data\":[\"A\",\"B\",\"C\"]}");
    }


    @Test
    public void postFormListWithBodyAnn() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        List<Integer> idList = Lists.newArrayList(1, 2, 3);
        Cause cause1 = new Cause();
        cause1.setId(1);
        cause1.setScore(87);
        Cause cause2 = new Cause();
        cause2.setId(2);
        cause2.setScore(73);
        List<Cause> causes = Lists.newArrayList(cause1, cause2);
        assertThat(postClient.postFormListWithBodyAnn("foo", "123456", idList, causes))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/form-array")
                .assertHeaderEquals("Content-Type", "application/x-www-form-urlencoded")
                .assertBodyEquals("username=foo&password=123456&idList=1%2C2%2C3&cause%5B0%5D.id=1&cause%5B0%5D.score=87&cause%5B1%5D.id=2&cause%5B1%5D.score=73");
    }

    @Test
    public void postFormListWithBodyAnn2() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        FormListParam param = new FormListParam();
        List<Integer> idList = Lists.newArrayList(1, 2, 3);
        param.setUsername("foo");
        param.setPassword("123456");
        param.setIdList(idList);
        Cause cause1 = new Cause();
        cause1.setId(1);
        cause1.setScore(87);
        Cause cause2 = new Cause();
        cause2.setId(2);
        cause2.setScore(73);
        List<Cause> causes = Lists.newArrayList(cause1, cause2);
        param.setCause(causes);
        assertThat(postClient.postFormListWithBodyAnn2(param))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/form-array")
                .assertHeaderEquals("Content-Type", "application/x-www-form-urlencoded")
                .assertBodyEquals("username=foo&password=123456&idList=1%2C2%2C3&cause%5B0%5D.id=1&cause%5B0%5D.score=87&cause%5B1%5D.id=2&cause%5B1%5D.score=73");
    }

    @Test
    public void postFormListWithBodyAnn3() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Integer[] idList = new Integer[] {1, 2, 3};
        Cause cause1 = new Cause();
        cause1.setId(1);
        cause1.setScore(87);
        Cause cause2 = new Cause();
        cause2.setId(2);
        cause2.setScore(73);
        Cause[] causes = new Cause[] {cause1, cause2};
        assertThat(postClient.postFormListWithBodyAnn3("foo", "123456", idList, causes))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/form-array")
                .assertHeaderEquals("Content-Type", "application/x-www-form-urlencoded")
                .assertBodyEquals("username=foo&password=123456&idList=1%2C2%2C3&cause%5B0%5D.id=1&cause%5B0%5D.score=87&cause%5B1%5D.id=2&cause%5B1%5D.score=73");
    }

    @Test
    public void postFormListWithBodyAnn4() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        FormArrayParam param = new FormArrayParam();
        param.setUsername("foo");
        param.setPassword("123456");
        param.setIdList(new Integer[] {1, 2, 3});
        Cause cause1 = new Cause();
        cause1.setId(1);
        cause1.setScore(87);
        Cause cause2 = new Cause();
        cause2.setId(2);
        cause2.setScore(73);
        Cause[] causes = new Cause[] {cause1, cause2};
        param.setCause(causes);
        assertThat(postClient.postFormListWithBodyAnn4(param))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/form-array")
                .assertHeaderEquals("Content-Type", "application/x-www-form-urlencoded")
                .assertBodyEquals("username=foo&password=123456&idList=1%2C2%2C3&cause%5B0%5D.id=1&cause%5B0%5D.score=87&cause%5B1%5D.id=2&cause%5B1%5D.score=73");
    }

    @Test
    public void testPostXml() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        XmlTestParam testParam = new XmlTestParam();
        testParam.setA(1);
        testParam.setB(2);
        assertThat(postClient.postXml(testParam))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/xml")
                .assertHeaderEquals("Content-Type", "application/xml")
                .assertBodyEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<misc>\n" +
                        "    <a>1</a>\n" +
                        "    <b>2</b>\n" +
                        "</misc>\n");
    }

    @Test
    public void testPostXmlInDataProperty() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        XmlTestParam testParam = new XmlTestParam();
        testParam.setA(1);
        testParam.setB(2);
        assertThat(postClient.postXmlInDataProperty(testParam))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/xml")
                .assertHeaderEquals("Content-Type", "application/xml")
                .assertBodyEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<misc>\n" +
                        "    <a>1</a>\n" +
                        "    <b>2</b>\n" +
                        "</misc>\n");
    }

    @Test
    public void testPostXmlInDataProperty2() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        XmlTestParam testParam = new XmlTestParam();
        testParam.setA(1);
        testParam.setB(2);
        assertThat(postClient.postXmlInDataProperty2(testParam))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/xml")
                .assertHeaderEquals("Content-Type", "application/xml")
                .assertBodyEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<misc>\n" +
                        "    <a>1</a>\n" +
                        "    <b>2</b>\n" +
                        "</misc>\n");
    }

    @Test
    public void testPostXmlWithXMLBodyAnn() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        XmlTestParam testParam = new XmlTestParam();
        testParam.setA(1);
        testParam.setB(2);
        assertThat(postClient.postXmlWithXMLBodyAnn(testParam))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/xml")
                .assertHeaderEquals("Content-Type", "application/xml")
                .assertBodyEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<misc>\n" +
                        "    <a>1</a>\n" +
                        "    <b>2</b>\n" +
                        "</misc>\n");
    }

    @Test
    public void testPostXmlBodyString() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(postClient.postXmlBodyString(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<misc>\n" +
                "    <a>1</a>\n" +
                "    <b>2</b>\n" +
                "</misc>\n"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/xml")
                .assertHeaderEquals("Content-Type", "application/xml")
                .assertBodyEquals(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<misc>\n" +
                        "    <a>1</a>\n" +
                        "    <b>2</b>\n" +
                        "</misc>\n");
    }


    @Test
    public void testPostXmlWithXMLBodyAnnAndReturnObj() throws InterruptedException {
        server.enqueue(new MockResponse()
                .setBody("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<misc>\n" +
                "    <a>3</a>\n" +
                "    <b>4</b>\n" +
                "</misc>\n"));
        XmlTestParam testParam = new XmlTestParam();
        testParam.setA(1);
        testParam.setB(2);
        assertThat(postClient.postXmlWithXMLBodyAnnAndReturnObj(testParam))
                .isNotNull()
                .extracting(XmlTestParam::getA, XmlTestParam::getB)
                .contains(10, 20);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/xml-response")
                .assertHeaderEquals("Content-Type", "application/xml")
                .assertBodyEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<misc>\n" +
                        "    <a>1</a>\n" +
                        "    <b>2</b>\n" +
                        "</misc>\n");
    }

}
