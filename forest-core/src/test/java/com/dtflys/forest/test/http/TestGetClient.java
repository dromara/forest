package com.dtflys.forest.test.http;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.dtflys.forest.Forest;
import com.dtflys.forest.annotation.LogEnabled;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestURL;
import com.dtflys.forest.http.UnclosedResponse;
import com.dtflys.forest.test.http.backend.BackendClient;
import com.dtflys.forest.test.http.client.GetClient;
import com.dtflys.forest.test.http.client.PostClient;
import com.dtflys.forest.test.http.client.UrlEncodedClient;
import com.dtflys.forest.test.http.model.JsonTestUser;
import com.dtflys.forest.test.model.Contact;
import com.dtflys.forest.test.model.TokenResult;
import com.dtflys.forest.utils.TypeReference;
import com.google.common.collect.Lists;
import okhttp3.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 15:02
 */
public class TestGetClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\":\"ok\"}";
    private static final Logger log = LoggerFactory.getLogger(TestGetClient.class);

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


    public TestGetClient(String backend, String jsonConverter) {
        super(backend, jsonConverter, configuration);
        configuration.setVariableValue("port", server.getPort());
        getClient = configuration.createInstance(GetClient.class);
        urlEncodedClient = configuration.createInstance(UrlEncodedClient.class);
    }

    @Test
    public void testHash() {
        PostClient postClient = Forest.client(PostClient.class);
        int hashCode1 = getClient.hashCode();
        int hashCode2 = postClient.hashCode();
        int hashCode3 = getClient.hashCode();
        assertThat(hashCode1).isNotEqualTo(hashCode2);
        assertThat(hashCode1).isEqualTo(hashCode3);
    }

    @Test
    public void testClass() {
        Class<?> clientClass = getClient.getClass();
        assertThat(clientClass).isNotNull();
        assertThat(getClient).isInstanceOf(GetClient.class);
        assertThat(GetClient.class.isAssignableFrom(clientClass)).isTrue();
    }


    @Test
    public void testGet() {
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
    public void testGet2() {
        String res = getClient.simpleGet2();
        System.out.println(res);
/*
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(getClient.simpleGet2())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo");
*/
    }

    public interface PerformanceClient {

        @Post(
                url = "http://{}:{}/abc",
                headers = "Accept: text/plain",
                data = "username=foo"
        )
        @LogEnabled(false)
        UnclosedResponse test(String hostname, int port);
    }


    @Test
    public void performance_interface() {
        int count = 10000;
        for (int i = 0; i < count; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED));
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        PerformanceClient client = Forest.client(PerformanceClient.class);
        for (int i = 0; i < count; i++) {
            log.info("i = " + i);
            client.test("localhost", server.getPort()).close();
        }
        stopWatch.stop();
        System.out.println("总耗时: " + stopWatch.getTotalTimeMillis() + "ms");
    }


    @Test
    public void performance() {
        int count = 10000;
        for (int i = 0; i < count; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED));
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        HttpBackend httpBackend = Forest.backend("httpclient");
        for (int i = 0; i < count; i++) {
            log.info("i = " + i);
            Forest.post("http://{}:{}/abc", server.getHostName(), server.getPort())
                    .logEnabled(false)
                    .backend(httpBackend)
                    .addHeader("Accept", "text/plain")
                    .addBody("username=foo")
                    .executeAsUnclosedResponse()
                    .close();
        }
        stopWatch.stop();
        System.out.println("总耗时: " + stopWatch.getTotalTimeMillis() + "ms");
    }


//    @Test
    public void performance_okhttp() {
        int count = 10000;
        ConnectionPool pool = new ConnectionPool(10000, 100000, TimeUnit.SECONDS);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectionPool(pool)
                .build();

        MediaType mediaType = MediaType.parse(ContentType.APPLICATION_X_WWW_FORM_URLENCODED);
        for (int i = 0; i < count; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED));
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < count; i++) {
            log.info("i = " + i);
            Request.Builder builder = new Request.Builder();
            Request request = builder.url("http://localhost:" + server.getPort() + "/abc")
                    .header("Accept", "text/plain")
                    .method("POST", RequestBody.create(mediaType, "username=foo".getBytes(StandardCharsets.UTF_8)))
                    .build();
            Call call = client.newCall(request);
            try {
                Response response = call.execute();
                response.body().close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        stopWatch.stop();
        System.out.println("总耗时: " + stopWatch.getTotalTimeMillis() + "ms");
    }


//    @Test
    public void performance_hutool() {
        int count = 10000;
        for (int i = 0; i < count; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED));
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < count; i++) {
            log.info("i = " + i);
            HttpUtil.createPost("http://localhost:" + server.getPort() + "/abc")
                            .header("Accept", "text/plain")
                            .body("username=foo")
                            .execute();
        }
        stopWatch.stop();
        System.out.println("总耗时: " + stopWatch.getTotalTimeMillis() + "ms");
    }


    //    @Test
    public void performance_concurrent() throws InterruptedException {
        int count = 10000;
        for (int i = 0; i < count; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED));
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final CountDownLatch latch = new CountDownLatch(count);
        ExecutorService service = Executors.newFixedThreadPool(80);
        for (int i = 0; i < count; i++) {
            service.submit(() -> {
                getClient.testPath("abc");
                latch.countDown();
            });
        }
        latch.await();
        stopWatch.stop();
        System.out.println("总耗时: " + stopWatch.getTotalTimeMillis() + "ms");
    }


    @Test
    public void testGet3() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(getClient.simpleGet3())
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo");
    }

    @Test
    public void testPath() {
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
    public void testPathWithAt() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(getClient.testPath("aaa@bbb"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/aaa@bbb");
    }


    @Test
    public void testPath2() throws MalformedURLException {
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
    public void testPath3() {
        ForestRequest request = getClient.testPath3();
        assertThat(request).isNotNull();
        assertThat(request.urlString()).isEqualTo("https://localhost/xxx:yyy");
        System.out.println(request.urlString());
    }

    @Test
    public void testPath4() {
        ForestRequest request = getClient.testPath4();
        assertThat(request).isNotNull();
        assertThat(request.urlString()).isEqualTo("https://localhost/xxx:111");
        System.out.println(request.urlString());
    }

    @Test
    public void testPath_userInfo() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest request = getClient.testPath_userInfo();
        assertThat(request).isNotNull();
        assertThat(request.urlString()).isEqualTo("http://aaa/bbb/skip:123456@localhost:" + server.getPort());
        System.out.println(request.urlString());
    }


    @Test
    public void testJsonMapGet() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestResponse<Map> response = getClient.jsonMapGet();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getContentType()).isNull();
        assertThat(response.getCharset()).isEqualTo("UTF-8");
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
        assertThat(response.getCharset()).isEqualTo("GBK");
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
        server.enqueue(new MockResponse().setBody(buffer));
        ForestResponse<Map> response = getClient.jsonMapGetWithResponseEncoding();
        assertThat(response).isNotNull();
        assertThat(response.getContentType()).isNull();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getCharset()).isEqualTo("GBK");
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
        final ForestResponse<Map> response = getClient.jsonMapGet();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getContentType())
                .isNotNull()
                .extracting(ContentType::toStringWithoutParameters)
                .isEqualTo(ContentType.APPLICATION_JSON);
        assertThat(response.getCharset()).isEqualTo("UTF-8");
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
        user.setPassword("xxx");
        assertThat(getClient.queryObjectGet(user))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertHeaderEquals("Accept", "text/plain")
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo")
                .assertQueryEquals("password", "xxx");
    }

    @Test
    public void testQueryObjectGet_with_nullObject() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        assertThat(getClient.queryObjectGet((JsonTestUser) null))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertHeaderEquals("Accept", "text/plain")
                .assertPathEquals("/hello/user");
    }

    @Test
    public void testQueryObjectGet_with_nullObjectField() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        JsonTestUser user = new JsonTestUser();
        user.setUsername("foo");
        user.setPassword(null);
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
    public void testUrl() {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String token = "YmZlNDYzYmVkMWZjYzgwNjExZDVhMWM1ODZmMWRhYzg0NTcyMGEwMg==";
        ForestResponse<String> response = getClient.testUrl();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(200);
        assertThat(response.getCharset()).isNotNull().isEqualTo("UTF-8");
        assertThat(response.getContentType()).isNull();
        assertThat(response.getResult()).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/")
                .assertEncodedQueryEquals("token=" + token)
                .assertQueryEquals("token", token);
    }

    @Test
    public void testUrl2() {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String token = "YmZlNDYzYmVkMWZjYzgwNjExZDVhMWM1ODZmMWRhYzg0NTcyMGEwMg==";
        ForestResponse<String> response = getClient.testUrl2("http", "localhost", "xxx$@!");
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(200);
        assertThat(response.getCharset()).isNotNull().isEqualTo("UTF-8");
        assertThat(response.getContentType()).isNull();
        assertThat(response.getResult()).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/xxx$@!")
                .assertEncodedQueryEquals("token=" + token)
                .assertQueryEquals("token", token);
    }

    @Test
    public void testUrl3() {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String url = StrUtil.format("http://localhost:{}/test", server.getPort());
        ForestResponse<String> response = getClient.testUrl3(url);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(200);
        assertThat(response.getCharset()).isNotNull().isEqualTo("UTF-8");
        assertThat(response.getContentType()).isNull();
        assertThat(response.getResult()).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/test");
    }

    @Test
    public void testUrl4() {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String url = StrUtil.format("/test", server.getPort());
        ForestRequest<String> request = getClient.testUrl4(url);
        request.port(server.getPort());
        ForestResponse<String> response = request.execute(new TypeReference<ForestResponse<String>>() {
        });
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(200);
        assertThat(response.getCharset()).isNotNull().isEqualTo("UTF-8");
        assertThat(response.getContentType()).isNull();
        assertThat(response.getResult()).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/test");
    }

    @Test
    public void testUrl4_2() {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String url = StrUtil.format("test/xxx", server.getPort());
        ForestRequest<String> request = getClient.testUrl4(url);
        request.port(server.getPort());
        ForestResponse<String> response = request.execute(new TypeReference<ForestResponse<String>>() {
        });
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(200);
        assertThat(response.getCharset()).isNotNull().isEqualTo("UTF-8");
        assertThat(response.getContentType()).isNull();
        assertThat(response.getResult()).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/test/xxx");
    }

    @Test
    public void testUrl4_3() {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String url = StrUtil.format("test/xxx", server.getPort());
        ForestRequest<String> request = getClient.testUrl4(url);
        request.setPort(server.getPort());
        ForestResponse<String> response = request.execute(new TypeReference<ForestResponse<String>>() {
        });
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(200);
        assertThat(response.getCharset()).isNotNull().isEqualTo("UTF-8");
        assertThat(response.getContentType()).isNull();
        assertThat(response.getResult()).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/test/xxx");
    }

    @Test
    public void testUrl4_4() {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String url = StrUtil.format("/test?a=1&b=2#?ref=ok", server.getPort());
        ForestRequest<String> request = getClient.testUrl4(url);
        assertThat(request.ref()).isEqualTo("?ref=ok");
        request.port(server.getPort());
        ForestResponse<String> response = request.execute(new TypeReference<ForestResponse<String>>() {
        });
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(200);
        assertThat(response.getCharset()).isNotNull().isEqualTo("UTF-8");
        assertThat(response.getContentType()).isNull();
        assertThat(response.getResult()).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/test")
                .assertQueryEquals("a", "1")
                .assertQueryEquals("b", "2");
    }


    @Test
    public void testUrl4_5() {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String url = StrUtil.format("/test#?ref=ok", server.getPort());
        ForestRequest<String> request = getClient.testUrl4(url);
        assertThat(request.ref()).isEqualTo("?ref=ok");
        request.port(server.getPort());
        ForestResponse<String> response = request.execute(new TypeReference<ForestResponse<String>>() {
        });
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(200);
        assertThat(response.getCharset()).isNotNull().isEqualTo("UTF-8");
        assertThat(response.getContentType()).isNull();
        assertThat(response.getResult()).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/test");
    }

    @Test
    public void testUrl4_6() {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        String url = StrUtil.format("/test#xxx/yyy", server.getPort());
        ForestRequest<String> request = getClient.testUrl4(url);
        assertThat(request.ref()).isEqualTo("xxx/yyy");
        request.port(server.getPort());
        ForestResponse<String> response = request.execute(new TypeReference<ForestResponse<String>>() {
        });
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(200);
        assertThat(response.getCharset()).isNotNull().isEqualTo("UTF-8");
        assertThat(response.getContentType()).isNull();
        assertThat(response.getResult()).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/test");
    }

    @Test
    public void testRef() {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        ForestResponse<String> response = getClient.testRef("xxx/yyy");
        assertThat(response).isNotNull();
        assertThat(response.getRequest().getRef()).isEqualTo("xxx/yyy");
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(200);
        assertThat(response.getCharset()).isNotNull().isEqualTo("UTF-8");
        assertThat(response.getContentType()).isNull();
        assertThat(response.getResult()).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/test");
    }


    @Test
    public void testRef_2() {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        ForestResponse<String> response = getClient.testRef("?ref=ok");
        assertThat(response).isNotNull();
        assertThat(response.getRequest().getRef()).isEqualTo("?ref=ok");
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(200);
        assertThat(response.getCharset()).isNotNull().isEqualTo("UTF-8");
        assertThat(response.getContentType()).isNull();
        assertThat(response.getResult()).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/test");
    }

    @Test
    public void testRef2() {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        ForestResponse<String> response = getClient.testRef2("#xxx/yyy");
        assertThat(response).isNotNull();
        assertThat(response.getRequest().getRef()).isEqualTo("xxx/yyy");
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(200);
        assertThat(response.getCharset()).isNotNull().isEqualTo("UTF-8");
        assertThat(response.getContentType()).isNull();
        assertThat(response.getResult()).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/test");
    }

    @Test
    public void testRef2_2() {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        ForestResponse<String> response = getClient.testRef2("#?ref=#abc");
        assertThat(response).isNotNull();
        assertThat(response.getRequest().getRef()).isEqualTo("?ref=#abc");
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(200);
        assertThat(response.getCharset()).isNotNull().isEqualTo("UTF-8");
        assertThat(response.getContentType()).isNull();
        assertThat(response.getResult()).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/test");
    }


    @Test
    public void testRef3() {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        ForestResponse<String> response = getClient.testRef3("#?a=1", "&b=2");
        assertThat(response).isNotNull();
        assertThat(response.getRequest().getRef()).isEqualTo("?a=1&b=2");
        assertThat(response.getStatusCode()).isNotNull().isEqualTo(200);
        assertThat(response.getCharset()).isNotNull().isEqualTo("UTF-8");
        assertThat(response.getContentType()).isNull();
        assertThat(response.getResult()).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/test");
    }

    @Test
    public void testDomain() {
        ForestRequest<String> request = getClient.testDomain("dtflyx");
        assertThat(request).isNotNull();
        ForestURL url = request.url();
        assertThat(url).isNotNull();
        assertThat(url.getScheme()).isEqualTo("https");
        assertThat(url.getHost()).isEqualTo("www.dtflyx.com");
        assertThat(url.getPath()).isEqualTo("/xxx");
        assertThat(url.getPort()).isEqualTo(443);
        assertThat(request.getUrl()).isEqualTo("https://www.dtflyx.com/xxx");
    }

    @Test
    public void testDomain2() {
        ForestRequest<String> request = getClient.testDomain2("www.dtflyx.com");
        assertThat(request).isNotNull();
        ForestURL url = request.url();
        assertThat(url).isNotNull();
        assertThat(url.getScheme()).isEqualTo("https");
        assertThat(url.getHost()).isEqualTo("www.dtflyx.com");
        assertThat(url.getPath()).isEqualTo("/xxx");
        assertThat(url.getPort()).isEqualTo(443);
        assertThat(request.getUrl()).isEqualTo("https://www.dtflyx.com/xxx");


        request = getClient.testDomain2("www.dtflyx.com/yyy");
        assertThat(request).isNotNull();
        url = request.url();
        assertThat(url).isNotNull();
        assertThat(url.getScheme()).isEqualTo("https");
        assertThat(url.getHost()).isEqualTo("www.dtflyx.com");
        assertThat(url.getPath()).isEqualTo("/yyy/xxx");
        assertThat(url.getPort()).isEqualTo(443);
        assertThat(request.getUrl()).isEqualTo("https://www.dtflyx.com/yyy/xxx");
    }


    @Test
    public void testDomain3() {
        ForestRequest<String> request = getClient.testDomain3("forest");
        assertThat(request).isNotNull();
        ForestURL url = request.url();
        assertThat(url).isNotNull();
        assertThat(url.getScheme()).isEqualTo("https");
        assertThat(url.getHost()).isEqualTo("forest.dtflyx.com");
        assertThat(url.getPath()).isEqualTo("/xxx");
        assertThat(url.getPort()).isEqualTo(443);
        assertThat(request.getUrl()).isEqualTo("https://forest.dtflyx.com/xxx");
    }


    @Test
    public void testSimpleGetMultiQuery() {
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
    public void testSimpleGetMultiQuery_encoded() throws UnsupportedEncodingException {
        String pwd = "中文";
        String encoded = URLEncoder.encode(pwd, "UTF-8");
        System.out.println(pwd + " => " + encoded);
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        assertThat(getClient.simpleGetMultiQuery(encoded))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertHeaderEquals("Accept", "text/plain")
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "foo")
                .assertQueryEquals("password", pwd);
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
    public void testSimpleGetMultiQuery3WithVar() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));

        assertThat(getClient.simpleGetMultiQuery2WithVar(null, "bar"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertHeaderEquals("Accept", "text/plain")
                .assertPathEquals("/hello/user")
                .assertQueryEquals("username", "null")
                .assertQueryEquals("password", "bar");
    }

    @Test
    public void testSimpleGetMultiQuery3WithLazy() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        assertThat(getClient.simpleGetMultiQuery2WithLazy("foo", "bar",
                req -> Base64.encode(req.queryString().getBytes())))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertHeaderEquals("Accept", "text/plain")
                .assertPathEquals("/hello/user")
                .assertQueryEquals("a", "foo")
                .assertQueryEquals("b", "bar")
                .assertQueryEquals("token", Base64.encode("a=foo&b=bar".getBytes()));
    }



    @Test
    public void testSimpleGetMultiQuery3() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeader("Content-Type", "text/plain")
                        .setHeader("Content-Encoding", "UTF-8")
                        .setBody(EXPECTED));
        assertThat(getClient.simpleGetMultiQuery2())
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
        assertThat(response.getCharset()).isNotNull().isEqualTo("UTF-8");
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
        assertThat(response.getCharset()).isNotNull().isEqualTo("UTF-8");
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
        TokenResult result = getClient.getToken();
        assertThat(result)
                .isNotNull()
                .extracting(TokenResult::getTokenTimeout, TokenResult::getURLToken)
                .contains(604800L, token);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/token");
    }

    @Test
    public void testJsonPath() {
        server.enqueue(new MockResponse().setBody("{\"status\":\"1\", \"data\":[{\"name\": \"foo\", \"age\": \"18\", \"phone\": \"12345678\"}]}"));
        List<Contact> contacts = getClient.testJsonPathResponse();
        assertThat(contacts).isNotNull();
        assertThat(contacts.get(0).getName()).isNotEmpty().isEqualTo("foo");
        assertThat(contacts.get(0).getAge()).isEqualTo(18);
        assertThat(contacts.get(0).getPhone()).isNotEmpty().isEqualTo("12345678");
    }

    @Test
    public void testEmptyResultObject() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Object result = getClient.testEmptyResultObject();
        assertThat(result).isNull();
    }

    @Test
    public void testEmptyResultOptional() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Optional<?> opt = getClient.testEmptyResultOptional();
        assertThat(opt).isNotNull();
        assertThat(opt.isPresent()).isFalse();
    }

    @Test
    public void testNotEmptyResultOptional() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Optional<String> opt = getClient.testNotEmptyResultOptional();
        assertThat(opt).isNotNull();
        assertThat(opt.isPresent()).isTrue();
        String result = opt.orElse("");
        assertThat(result).isEqualTo(EXPECTED);
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
        String[] usernames = new String[]{"foo", "bar", "user1", "user2"};
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
    public void testGetEncodedArgs1() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(urlEncodedClient.getEncodedArgs("1&x=10&y=20", "http://www.baidu.com"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/encoded/")
                .assertQueryEquals("a", "1")
                .assertQueryEquals("x", "10")
                .assertQueryEquals("y", "20")
                .assertQueryEquals("b", "http://www.baidu.com");
    }

    @Test
    public void testGetEncodedArgs2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(urlEncodedClient.getEncodedArgs2("1&x=10&y=20", "http://www.baidu.com"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/encoded/")
                .assertQueryEquals("a", "1&x=10&y=20")
                .assertQueryEquals("b", "http://www.baidu.com");
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
