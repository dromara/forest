package com.dtflys.test.http;

import com.alibaba.fastjson.JSON;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.client.UrlEncodedClient;
import com.dtflys.test.http.model.JsonTestUser;
import com.dtflys.test.http.client.GetClient;
import com.dtflys.test.model.TokenResult;
import com.google.common.collect.Lists;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 15:02
 */
public class TestGetClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\":\"ok\"}";

    @Rule
    public final MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private final GetClient getClient;

    private final UrlEncodedClient urlEncodedClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    @Override
    public void afterRequests() {
    }


    public TestGetClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        getClient = configuration.createInstance(GetClient.class);
        urlEncodedClient = configuration.createInstance(UrlEncodedClient.class);
    }


    @Test
    public void testGet() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(getClient.simpleGet())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo")
                .assertHeaderEquals("content-type", "text/plain");

    }

    @Test
    public void testGet2() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(getClient.simpleGet2())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo");
    }

    @Test
    public void testGet3() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(getClient.simpleGet3())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo");
    }

    @Test
    public void testPath() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(getClient.testPath("hello &(user)a:a?b=1/2&c=http://localhost:8080/?x=0&d=1"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/hello%20&(user)a:a")
                .assertQueryEquals("b", "1/2")
                .assertQueryEquals("c", "http://localhost:8080/?x=0")
                .assertQueryEquals("d", "1");
    }

    @Test
    public void testPath2() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(getClient.testPath2("hello &(user)a:a?b=1/2&c=http://localhost:8080/?x=0&d=1"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/hello%20&(user)a:a")
                .assertQueryEquals("b", "1/2")
                .assertQueryEquals("c", "http://localhost:8080/?x=0")
                .assertQueryEquals("d", "1");
    }


    @Test
    public void testJsonMapGet() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestResponse<Map> response = getClient.jsonMapGet();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getContentType()).isNull();
        assertThat(response.getContentEncoding()).isNull();
        assertThat(response.getResult())
                .isNotNull()
                .containsOnly(entry("status", "ok"));
        mockRequest(server)
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo");
    }

    @Test
    public void testJsonMapGetWithUTF8Response() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                .setHeader("Content-Encoding", "GBK")
                .setBody(EXPECTED));
        ForestResponse<Map> response = getClient.jsonMapGet();
        assertThat(response).isNotNull();
        assertThat(response.getContentType()).isNull();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getContentEncoding()).isEqualTo("GBK");
        assertThat(response.getResult())
                .isNotNull()
                .containsOnly(entry("status", "ok"));
        mockRequest(server)
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo");
    }

    @Test
    public void testJsonMapGetWithGBKResponse() throws IOException {
        String body = "{\"status\": \"正常\"}";
        byte[] byteArray = body.getBytes("GBK");
        Buffer buffer = new Buffer();
        try {
            buffer.readFrom(new ByteArrayInputStream(byteArray));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        server.enqueue(
                new MockResponse()
                        .setBody(buffer));
        ForestResponse<Map> response = getClient.jsonMapGetWithResponseEncoding();
        assertThat(response).isNotNull();
        assertThat(response.getContentType()).isNull();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getContentEncoding()).isEqualTo("GBK");
        assertThat(response.getResult())
                .isNotNull()
                .containsOnly(entry("status", "正常"));
        mockRequest(server)
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo");
    }


    @Test
    public void testJsonMapGetWithJsonResponse() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        ForestResponse<Map> response = getClient.jsonMapGet();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getContentType())
                .isNotNull()
                .extracting(ContentType::toStringWithoutParameters)
                .isEqualTo(ContentType.APPLICATION_JSON);
        assertThat(response.getContentEncoding()).isEqualTo("UTF-8");
        assertThat(response.getResult())
                .isNotNull()
                .containsOnly(entry("status", "ok"));
        mockRequest(server)
                .assertHeaderEquals("Accept", "text/plain")
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo");
    }



    @Test
    public void testTextParamGet() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        assertThat(getClient.textParamGet("foo"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertHeaderEquals("Accept", "text/plain")
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo");
    }

    @Test
    public void testTextParamGetWithDefaultValue() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        assertThat(getClient.textParamGetWithDefaultUsername(null))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertHeaderEquals("Accept", "text/plain")
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo");
    }



    @Test
    public void testTextParamInPathGet() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        assertThat(
                getClient.textParamInPathGet("foo", (data, request, response) -> {
                    response.setResult("onSuccess is ok");
                }))
                .isNotNull()
                .isEqualTo("onSuccess is ok");
        mockRequest(server)
                .assertHeaderEquals("Accept", "text/plain")
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo");
    }


    @Test
    public void testAnnParamGet() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        assertThat(getClient.annParamGet("foo"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertHeaderEquals("Accept", "text/plain")
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo");
    }

    @Test
    public void testAnnQueryGet() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        assertThat(getClient.annQueryGet("foo"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertHeaderEquals("Accept", "text/plain")
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo");
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
        assertThat(getClient.annObjectGet(user))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertHeaderEquals("Accept", "text/plain")
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo");
    }

    @Test
    public void testQueryAnnWithMap() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("username", "foo");
        map.put("password", "bar");
        map.put("list", list);
        assertThat(getClient.annObjectGet(map))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertHeaderEquals("Accept", "text/plain")
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username=foo&password=bar&list=a&list=b&list=c")
                .assertQueryEquals("username", "foo")
                .assertQueryEquals("password", "bar");
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
        assertThat(getClient.annObjectGet(map))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertHeaderEquals("Accept", "text/plain")
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo")
                .assertQueryEquals("password", "bar");
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
        assertThat(getClient.queryObjectGet(user))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertHeaderEquals("Accept", "text/plain")
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo");
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
        assertThat(getClient.queryObjectGet(map))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertHeaderEquals("Accept", "text/plain")
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo");
    }


    @Test
    public void testVarParamGet() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        assertThat(getClient.varParamGet("foo"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertHeaderEquals("Accept", "text/plain")
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo");
    }

    @Test
    public void testUrl() throws UnsupportedEncodingException, InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String token = "YmZlNDYzYmVkMWZjYzgwNjExZDVhMWM1ODZmMWRhYzg0NTcyMGEwMg==";
        ForestResponse<String> response = getClient.testUrl();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(200);
        assertThat(response.getContentEncoding()).isNotNull().isEqualTo("UTF-8");
        assertThat(response.getContentType()).isNull();
        assertThat(response.getResult()).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/")
                .assertEncodedQueryEquals("token=" + token)
                .assertQueryEquals("token", token);
    }


    @Test
    public void testSimpleGetMultiQuery() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        assertThat(getClient.simpleGetMultiQuery("bar"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertHeaderEquals("Accept", "text/plain")
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo")
                .assertQueryEquals("password", "bar");
    }

    @Test
    public void testSimpleGetMultiQuery2() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        assertThat(getClient.simpleGetMultiQuery2("foo", "bar"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertHeaderEquals("Accept", "text/plain")
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo")
                .assertQueryEquals("password", "bar");
    }


    @Test
    public void testSimpleGetMultiQuery3() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        assertThat(getClient.simpleGetMultiQuery3())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertHeaderEquals("Accept", "text/plain")
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo")
                .assertQueryEquals("password", "bar");
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
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.isError()).isFalse();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(200);
        assertThat(response.getContentType()).isNotNull()
                .extracting(ContentType::toString).isEqualTo("text/plain");
        assertThat(response.getContentEncoding()).isNotNull().isEqualTo("UTF-8");
        assertThat(response.getResult()).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertHeaderEquals("Accept", "text/plain")
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo")
                .assertQueryEquals("password", "bar");
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
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.isError()).isFalse();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(200);
        assertThat(response.getContentType()).isNotNull()
                .extracting(ContentType::toString).isEqualTo("text/plain");
        assertThat(response.getContentEncoding()).isNotNull().isEqualTo("UTF-8");
        assertThat(response.getResult()).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertHeaderEquals("Accept", "text/plain")
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo")
                .assertQueryEquals("password", "bar");
    }

    @Test
    public void testGetBooleanResultTrue() throws InterruptedException {
        server.enqueue(new MockResponse().setBody("true"));
        assertThat(getClient.getBooleanResultTrue()).isTrue();
        mockRequest(server).assertPathEquals("/boolean/true");
    }

    @Test
    public void testGetBooleanResultTrueWithContentType() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody("true"));
        assertThat(getClient.getBooleanResultTrue()).isTrue();
        mockRequest(server).assertPathEquals("/boolean/true");
    }

    @Test
    public void testGetToken() throws InterruptedException {
        String token = "eyJjfeljlOfjelajflaFJLjlaefjl";
        String str = "{\"TokenTimeout\": 604800, \"URLToken\": \"" + token + "\"}";
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(str));
        assertThat(getClient.getToken())
                .isNotNull()
                .extracting(TokenResult::getTokenTimeout, TokenResult::getURLToken)
                .contains(604800L, token);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/token");
    }

    @Test
    public void testRepeatableQuery1() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        assertThat(getClient.repeatableQuery())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username=foo&username=bar&username=user1&username=user2&password=123456");
    }

    @Test
    public void testRepeatableQuery2() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        assertThat(getClient.repeatableQuery("user1", "user2", "123456"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/hello/user")
                .assertHeaderEquals("Accept", "text/plain")
                .assertQueryEquals("username=foo&username=bar&username=user1&username=user2&password=123456");
    }

    @Test
    public void testRepeatableQuery3() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        List<String> usernames = Lists.newArrayList("foo", "bar", "user1", "user2");
        assertThat(getClient.repeatableQuery(usernames, "123456"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username=foo&username=bar&username=user1&username=user2&password=123456");
    }

    @Test
    public void testRepeatableQuery4() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String[] usernames = new String[] {"foo", "bar", "user1", "user2"};
        assertThat(getClient.repeatableQuery(usernames, "123456"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username=foo&username=bar&username=user1&username=user2&password=123456");
    }


    @Test
    public void testArrayQuery() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        List<String> usernames = Lists.newArrayList("foo", "bar", "user1", "user2");
        assertThat(getClient.arrayQuery(usernames, "123456"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/hello/user/array")
                .assertQueryEquals("username_0", "foo")
                .assertQueryEquals("username_1", "bar")
                .assertQueryEquals("username_2", "user1")
                .assertQueryEquals("username_3", "user2")
                .assertQueryEquals("password", "123456");
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
        assertThat(getClient.jsonQuery(idList, userInfo))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/hello/user")
                .assertQueryEquals("ids", JSON.toJSONString(idList))
                .assertQueryEquals("user", JSON.toJSONString(userInfo));
    }

    @Test
    public void testGetQueryStringWithoutName() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(getClient.getQueryStringWithoutName("foo"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/hello/user")
                .assertEncodedQueryEquals("foo");
    }

    @Test
    public void testGetQueryStringWithoutName2() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(getClient.getQueryStringWithoutName2("foo"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/hello/user")
                .assertEncodedQueryEquals("foo");
    }

    @Test
    public void testGetUrlEncoded() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String url1 = "http://www.gitee.com";
        String url2 = "http://search.gitee.com?type=repository&q=forest";
        assertThat(urlEncodedClient.getUrlEncoded(url1, url2, "中文", "AbcD12#$iTXI", "il&felUFO3o=P", "中文内容"))
            .isNotNull()
            .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/encoded/")
                .assertQueryEquals("url1", "http://www.gitee.com")
                .assertQueryEquals("url2", "http://search.gitee.com?type=repository&q=forest")
                .assertQueryEquals("lang", "中文")
                .assertQueryEquals("code", "AbcD12#$iTXI")
                .assertQueryEquals("data", "il&felUFO3o=P")
                .assertQueryEquals("content", "中文内容");
    }

    @Test
    public void testGetUrlEncodedWithQuery() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String url = "http://www.gitee.com";
        assertThat(urlEncodedClient.getUrlEncodedWithQuery(url, url, "中文", "AbcD12#$iTXI", "il&felUFO3o=P", "中文内容"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/encoded")
                .assertQueryEquals("url1", "http://www.gitee.com")
                .assertQueryEquals("url2", "http://www.gitee.com")
                .assertQueryEquals("lang", "中文")
                .assertQueryEquals("code", "AbcD12#$iTXI")
                .assertQueryEquals("data", "il&felUFO3o=P")
                .assertQueryEquals("content", "中文内容");
    }

}
