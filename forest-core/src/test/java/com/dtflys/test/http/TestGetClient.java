package com.dtflys.test.http;

import com.alibaba.fastjson.JSON;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.client.EmptyJsonClient;
import com.dtflys.test.http.client.GetWithBodyClient;
import com.dtflys.test.http.model.JsonTestUser;
import com.dtflys.test.mock.GetMockServer;
import com.dtflys.test.http.client.GetClient;
import com.dtflys.test.mock.GetWithBodyMockServer;
import com.dtflys.test.model.TokenResult;
import com.google.common.collect.Lists;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 15:02
 */
public class TestGetClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestGetClient.class);

    public final static String EXPECTED = "{\"status\":\"ok\"}";

    @Rule
    public final MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private final GetClient getClient;

    private final EmptyJsonClient emptyJsonClient;

    private final GetWithBodyClient getWithBodyClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
    }


    public TestGetClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        getClient = configuration.createInstance(GetClient.class);
        emptyJsonClient = configuration.createInstance(EmptyJsonClient.class);
        getWithBodyClient = configuration.createInstance(GetWithBodyClient.class);
    }


    @Test
    public void testGet() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = getClient.simpleGet();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(EXPECTED, result);
        RecordedRequest request = server.takeRequest();
        assertEquals("/hello/user?username=foo", request.getPath());
    }

    @Test
    public void testGet2() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = getClient.simpleGet2();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(EXPECTED, result);
        RecordedRequest request = server.takeRequest();
        assertEquals("/hello/user?username=foo", request.getPath());

    }

    @Test
    public void testGet3() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = getClient.simpleGet3();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(GetMockServer.EXPECTED, result);
        RecordedRequest request = server.takeRequest();
        assertEquals("/hello/user?username=foo", request.getPath());
    }


    @Test
    public void testJsonMapGet() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestResponse<Map> response = getClient.jsonMapGet();
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNull(response.getContentType());
        assertNull(response.getContentEncoding());
        Map map  = response.getResult();
        assertNotNull(map);
        assertEquals("ok", map.get("status"));
        RecordedRequest request = server.takeRequest();
        assertEquals("/hello/user?username=foo", request.getPath());
    }

    @Test
    public void testJsonMapGetWithUTF8Response() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                .setHeader("Content-Encoding", "GBK")
                .setBody(EXPECTED));
        ForestResponse<Map> response = getClient.jsonMapGet();
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNull(response.getContentType());
        assertEquals("GBK", response.getContentEncoding());
        Map map  = response.getResult();
        assertNotNull(map);
        assertEquals("ok", map.get("status"));
        RecordedRequest request = server.takeRequest();
        assertEquals("/hello/user?username=foo", request.getPath());
    }

    @Test
    public void testJsonMapGetWithJsonResponse() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        ForestResponse<Map> response = getClient.jsonMapGet();
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getContentType());
        assertEquals("application/json", response.getContentType().toString());
        assertEquals("UTF-8", response.getContentEncoding());
        Map map  = response.getResult();
        assertNotNull(map);
        assertEquals("ok", map.get("status"));
        RecordedRequest request = server.takeRequest();
        assertEquals("/hello/user?username=foo", request.getPath());
    }



    @Test
    public void testTextParamGet() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String result = getClient.textParamGet("foo");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(GetMockServer.EXPECTED, result);
        RecordedRequest request = server.takeRequest();
        assertEquals("/hello/user?username=foo", request.getPath());
    }

    @Test
    public void testTextParamGetWithDefaultValue() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String result = getClient.textParamGetWithDefaultUsername(null);
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(GetMockServer.EXPECTED, result);
        RecordedRequest request = server.takeRequest();
        assertEquals("/hello/user?username=foo", request.getPath());
    }



    @Test
    public void testTextParamInPathGet() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String result = getClient.textParamInPathGet("foo", (data, request, response) -> {
            response.setResult("onSuccess is ok");
        });
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals("onSuccess is ok", result);
        RecordedRequest request = server.takeRequest();
        assertEquals("/hello/user?username=foo", request.getPath());
    }


    @Test
    public void testAnnParamGet() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String result = getClient.annParamGet("foo");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(GetMockServer.EXPECTED, result);
        RecordedRequest request = server.takeRequest();
        assertEquals("/hello/user?username=foo", request.getPath());
    }

    @Test
    public void testAnnQueryGet() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String result = getClient.annQueryGet("foo");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(GetMockServer.EXPECTED, result);
        RecordedRequest request = server.takeRequest();
        assertEquals("/hello/user?username=foo", request.getPath());
    }


    @Test
    public void testAnnObjectGet() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        JsonTestUser user = new JsonTestUser();
        user.setUsername("foo");
        String result = getClient.annObjectGet(user);
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(GetMockServer.EXPECTED, result);
        RecordedRequest request = server.takeRequest();
        assertEquals("/hello/user?username=foo", request.getPath());
    }

    @Test
    public void testQueryAnnWithMap() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        Map<String, Object> map = new HashMap<>();
        map.put("username", "foo");
        String result = getClient.annObjectGet(map);
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(GetMockServer.EXPECTED, result);
        RecordedRequest request = server.takeRequest();
        assertEquals("/hello/user?username=foo", request.getPath());
    }

    @Test
    public void testQueryAnnWithMap2() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("username", "foo");
        map.put("password", "bar");
        String result = getClient.annObjectGet(map);
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(GetMockServer.EXPECTED, result);
        RecordedRequest request = server.takeRequest();
        assertEquals("/hello/user?username=foo&password=bar", request.getPath());
    }


    @Test
    public void testQueryObjectGet() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        JsonTestUser user = new JsonTestUser();
        user.setUsername("foo");
        String result = getClient.queryObjectGet(user);
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(GetMockServer.EXPECTED, result);
        RecordedRequest request = server.takeRequest();
        assertEquals("/hello/user?username=foo", request.getPath());
    }

    @Test
    public void testQueryObjectGet2() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        Map<String, Object> map = new HashMap<>();
        map.put("username", "foo");
        String result = getClient.queryObjectGet(map);
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(GetMockServer.EXPECTED, result);
        RecordedRequest request = server.takeRequest();
        assertEquals("/hello/user?username=foo", request.getPath());
    }

    @Override
    public void afterRequests() {
    }

    @Test
    public void testVarParamGet() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String result = getClient.varParamGet("foo");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(GetMockServer.EXPECTED, result);
        RecordedRequest request = server.takeRequest();
        assertEquals("/hello/user?username=foo", request.getPath());
    }

    @Test
    public void testUrl() throws UnsupportedEncodingException, InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String token = "YmZlNDYzYmVkMWZjYzgwNjExZDVhMWM1ODZmMWRhYzg0NTcyMGEwMg==";
        ForestResponse<String> response = getClient.testUrl();
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNull(response.getContentType());
        assertEquals("UTF-8", response.getContentEncoding());
        assertEquals(EXPECTED, response.getResult());
        RecordedRequest request = server.takeRequest();
        assertEquals("/?token=" + URLEncoder.encode(token, "UTF-8"), request.getPath());
    }


    @Test
    public void testSimpleGetMultiQuery() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String result = getClient.simpleGetMultiQuery("bar");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(EXPECTED, result);
        RecordedRequest request = server.takeRequest();
        assertEquals("/hello/user?username=foo&password=bar", request.getPath());
    }

    @Test
    public void testSimpleGetMultiQuery2() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String result = getClient.simpleGetMultiQuery2("foo", "bar");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(EXPECTED, result);
        RecordedRequest request = server.takeRequest();
        assertEquals("/hello/user?username=foo&password=bar", request.getPath());
    }


    @Test
    public void testSimpleGetMultiQuery3() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String result = getClient.simpleGetMultiQuery3();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(EXPECTED, result);
        RecordedRequest request = server.takeRequest();
        assertEquals("/hello/user?username=foo&password=bar", request.getPath());
    }

    @Test
    public void testSimpleGetMultiQuery4() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        ForestResponse<String> response = getClient.simpleGetMultiQuery4();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getContentType());
        assertEquals("text/plain", response.getContentType().toString());
        assertEquals("UTF-8", response.getContentEncoding());
        String result = response.getResult();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(EXPECTED, result);
        RecordedRequest request = server.takeRequest();
        assertEquals("/hello/user?username=foo&password=bar", request.getPath());
    }

    @Test
    public void testSimpleGetMultiQuery5() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        ForestResponse<String> response = getClient.simpleGetMultiQuery5("foo");
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getContentType());
        assertEquals("text/plain", response.getContentType().toString());
        assertEquals("UTF-8", response.getContentEncoding());
        String result = response.getResult();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(EXPECTED, result);
        RecordedRequest request = server.takeRequest();
        assertEquals("/hello/user?username=foo&username=foo&password=bar", request.getPath());
    }

    @Test
    public void testGetBooleanResultTrue() throws InterruptedException {
        server.enqueue(new MockResponse().setBody("true"));
        Boolean ret = getClient.getBooleanResultTrue();
        assertTrue(ret);
        RecordedRequest request = server.takeRequest();
        assertEquals("/boolean/true", request.getPath());
    }

    @Test
    public void testGetBooleanResultTrueWithContentType() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody("true"));
        Boolean ret = getClient.getBooleanResultTrue();
        assertTrue(ret);
        RecordedRequest request = server.takeRequest();
        assertEquals("/boolean/true", request.getPath());
    }

    @Test
    public void testGetToken() {
        String token = "eyJjfeljlOfjelajflaFJLjlaefjl";
        String str = "{\"TokenTimeout\": 604800, \"URLToken\": \"" + token + "\"}";
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(str));
        TokenResult result = getClient.getToken();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(604800L, result.getTokenTimeout());
        assertEquals(token, result.getURLToken());
    }

    @Test
    public void testRepeatableQuery1() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String result = getClient.repeatableQuery();
        assertNotNull(result);
        assertEquals(EXPECTED, result);
        RecordedRequest request = server.takeRequest();
        assertEquals(
                "/hello/user?username=foo&username=bar&username=user1&username=user2&password=123456",
                request.getPath());
    }

    @Test
    public void testRepeatableQuery2() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String result = getClient.repeatableQuery("user1", "user2", "123456");
        assertNotNull(result);
        assertEquals(EXPECTED, result);
        RecordedRequest request = server.takeRequest();
        assertEquals(
                "/hello/user?username=foo&username=bar&username=user1&username=user2&password=123456",
                request.getPath());
    }

    @Test
    public void testRepeatableQuery3() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        List<String> usernames = Lists.newArrayList("foo", "bar", "user1", "user2");
        String result = getClient.repeatableQuery(usernames, "123456");
        assertNotNull(result);
        assertEquals(EXPECTED, result);
        RecordedRequest request = server.takeRequest();
        assertEquals(
                "/hello/user?username=foo&username=bar&username=user1&username=user2&password=123456",
                request.getPath());
    }

    @Test
    public void testRepeatableQuery4() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String[] usernames = new String[] {"foo", "bar", "user1", "user2"};
        String result = getClient.repeatableQuery(usernames, "123456");
        assertNotNull(result);
        assertEquals(EXPECTED, result);
        RecordedRequest request = server.takeRequest();
        assertEquals(
                "/hello/user?username=foo&username=bar&username=user1&username=user2&password=123456",
                request.getPath());
    }


    @Test
    public void testArrayQuery() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        List<String> usernames = Lists.newArrayList("foo", "bar", "user1", "user2");
        String result = getClient.arrayQuery(usernames, "123456");
        assertNotNull(result);
        assertEquals(EXPECTED, result);
        RecordedRequest request = server.takeRequest();
        assertEquals(
                "/hello/user/array?username_0=foo&username_1=bar&username_2=user1&username_3=user2&password=123456",
                request.getPath());
    }

    @Test
    public void testJSONQuery() throws InterruptedException, UnsupportedEncodingException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        List<Integer> idList = Lists.newArrayList(1, 2, 3, 4, 5, 6);
        Map<String, String> userInfo = new LinkedHashMap<>();
        userInfo.put("username", "foo");
        userInfo.put("password", "bar");
        String result = getClient.jsonQuery(idList, userInfo);
        assertNotNull(result);
        assertEquals(EXPECTED, result);
        String idsJson = URLEncoder.encode(JSON.toJSONString(idList), "UTF-8");
        String userInfoJson = URLEncoder.encode(JSON.toJSONString(userInfo), "UTF-8");
        RecordedRequest request = server.takeRequest();
        assertEquals(
                "/hello/user?ids=" + idsJson + "&user=" + userInfoJson,
                request.getPath());
    }



    @Test
    public void emptyJsonMap() throws InterruptedException {
        Map<String, Object> map = new HashMap<>();
        server.enqueue(new MockResponse().setBody("success"));
        String result = emptyJsonClient.postEmptyJsonMap(map);
        Assert.assertEquals("success", result);
        RecordedRequest request = server.takeRequest();
        String body = request.getBody().readString(StandardCharsets.UTF_8);
        Assert.assertEquals("{}", body);
    }

    @Test
    public void emptyJson2Map() throws InterruptedException {
        Map<String, Object> map = new HashMap<>();
        server.enqueue(new MockResponse().setBody("success"));
        String result = emptyJsonClient.postEmptyJson2Map(map, map);
        Assert.assertEquals("success", result);
        RecordedRequest request = server.takeRequest();
        String body = request.getBody().readString(StandardCharsets.UTF_8);
        Assert.assertEquals("{}", body);
    }


    @Test
    public void emptyJsonString() throws InterruptedException {
        Map<String, Object> map = new HashMap<>();
        server.enqueue(new MockResponse().setBody("success"));
        String result = emptyJsonClient.postEmptyJsonString(map);
        Assert.assertEquals("success", result);
        RecordedRequest request = server.takeRequest();
        String body = request.getBody().readString(StandardCharsets.UTF_8);
        Assert.assertEquals("{}", body);
    }

    @Test
    public void emptyJsonStringWithParams() throws InterruptedException {
        Map<String, Object> map = new HashMap<>();
        server.enqueue(new MockResponse().setBody("success"));
        String result = emptyJsonClient.postEmptyJsonStringWithParams("ok", map);
        Assert.assertEquals("success", result);
        RecordedRequest request = server.takeRequest();
        String body = request.getBody().readString(StandardCharsets.UTF_8);
        Assert.assertEquals("{}", body);
        Assert.assertEquals("/empty/map/ok", request.getPath());
    }




}
