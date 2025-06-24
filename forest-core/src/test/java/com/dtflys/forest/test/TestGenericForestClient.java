package com.dtflys.forest.test;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.annotation.JSONField;
import com.dtflys.forest.Forest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.auth.BasicAuth;
import com.dtflys.forest.auth.BearerAuth;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.ConvertOptions;
import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.mapping.ForestExpressionException;
import com.dtflys.forest.exceptions.ForestExpressionNullException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.*;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.interceptor.InterceptorChain;
import com.dtflys.forest.interceptor.ResponseResult;
import com.dtflys.forest.logging.LogConfiguration;
import com.dtflys.forest.retryer.ForestRetryer;
import com.dtflys.forest.retryer.NoneRetryer;
import com.dtflys.forest.sse.SSELinesMode;
import com.dtflys.forest.test.http.BaseClientTest;
import com.dtflys.forest.test.http.model.UserParam;
import com.dtflys.forest.test.model.Contact;
import com.dtflys.forest.test.model.Result;
import com.dtflys.forest.test.model.TestUser;
import com.dtflys.forest.test.sse.MySSEHandler;
import com.dtflys.forest.utils.*;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import okio.Okio;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Stream;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class TestGenericForestClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\":\"1\", \"data\":\"2\"}";

    public final static String EXPECTED_SINGLE_USER = "{\"status\":\"ok\", \"data\": {\"name\": \"Foo\", \"age\": 12}}";

    public final static String EXPECTED_LIST_USER = "{\"status\":\"ok\", \"data\": [{\"name\": \"Foo\", \"age\": 12}, {\"name\": \"Bar\", \"age\": 22}]}";
    private static final Logger log = LoggerFactory.getLogger(TestGenericForestClient.class);


    @Rule
    public final MockWebServer server = new MockWebServer();

    public TestGenericForestClient(String backend, String jsonConverter) {
        super(backend, jsonConverter, Forest.config());
    }


    @Test
    public void testRequest() {
        assertThat(Forest.config().request()).isNotNull();
    }

    private static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS");
    }

    /**
     * 测试用的数据对象类型
     */
    private static class Data {
        // 属性 a
        private Integer a;
        // 属性 b
        private Integer b;

        public Integer getA() {
            return a;
        }

        public void setA(Integer a) {
            this.a = a;
        }

        public Integer getB() {
            return b;
        }

        public void setB(Integer b) {
            this.b = b;
        }
    }

    @Test
    public void testForestVersion() {
        System.out.println(Forest.VERSION);
    }


    @Test
    public void testRequest_url() throws MalformedURLException {
        ForestRequest<?> request = Forest.request();
        assertThat(request).isNotNull();
        assertThat(request.scheme()).isEqualTo("http");
        assertThat(request.host()).isNull();
        assertThat(request.path()).isEqualTo("/");
        request.url("http://127.0.0.1:8080/test");
        assertThat(request.urlString()).isEqualTo("http://127.0.0.1:8080/test");
        request.url("/abc");
        assertThat(request.urlString()).isEqualTo("http://127.0.0.1:8080/abc");
        request.url("http://forest.dtflyx.com/111");
        assertThat(request.urlString()).isEqualTo("http://forest.dtflyx.com/111");
        request.path("/222");
        assertThat(request.urlString()).isEqualTo("http://forest.dtflyx.com/222");
        request.url(new ForestURL(new URL("http://localhost:8080/333")));
        assertThat(request.urlString()).isEqualTo("http://localhost:8080/333");
        request.address(new ForestAddress("192.168.0.1", 8881));
        assertThat(request.urlString()).isEqualTo("http://192.168.0.1:8881/333");
        request.address("192.168.0.2", 8882);
        assertThat(request.urlString()).isEqualTo("http://192.168.0.2:8882/333");
    }


    @Test
    public void testRequest_query_with_arguments() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("http://localhost:{}/?a={}&b={}&c={}", server.getPort(), "1", "2", "3").execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&b=2&c=3");
    }

    @Test
    public void testRequest_query_with_numbered_arguments() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("http://localhost:{0}/?a={1}&b={2}&c={3}", server.getPort(), "1", "2", "3").execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&b=2&c=3");
    }


    @Test
    public void testRequest_query_repeat() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("/")
                .port(server.getPort())
                .addQuery("a", "1")
                .addQuery("a", "2")
                .addQuery("a", "3")
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&a=2&a=3");
    }

    @Test
    public void testRequest_query_repeat2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("/")
                .port(server.getPort())
                .addQuery("a", Arrays.asList(1, 2, 3))
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&a=2&a=3");
    }

    @Test
    public void testRequest_query_repeat3() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.config().setLogEnabled(true);
        Forest.get("/")
                .port(server.getPort())
                .addQuery("a", 1, 2, 3)
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&a=2&a=3");
    }

    @Test
    public void testRequest_query_array() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("/")
                .port(server.getPort())
                .addArrayQuery("a", Arrays.asList(1, 2, 3))
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a[]=1&a[]=2&a[]=3");
    }


    @Test
    public void testRequest_query_encode() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("/")
                .host("localhost")
                .port(server.getPort())
                .addBody("key", "https://www.baidu.com#/?modeversion%3Dminiprogram%26sourceCode%3DGDT-ID-23310731%26mark%3DXZX-WXZF-0805")
                .execute(String.class);
        mockRequest(server)
                .assertPathEquals("/")
                .assertBodyEquals("key=https://www.baidu.com#/?modeversion%3Dminiprogram%26sourceCode%3DGDT-ID-23310731%26mark%3DXZX-WXZF-0805");
    }

    public static interface TestClient {

        @Post("http://172.29.231.232:4433/predictions/spell")
        String post(@Var("port") int port, @Body("text") String text);

    }


    @Test
    public void testRequest_query_encode2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("/")
                .host("localhost")
                .port(server.getPort())
                .bodyType(ForestDataType.FORM)
                .addBody("value", "90%")
                .execute(String.class);
        mockRequest(server)
                .assertPathEquals("/")
                .assertBodyEquals("value=90%25");
    }

    @Test
    public void testRequest_query_encode3() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("/")
                .host("localhost")
                .port(server.getPort())
                .bodyType(ForestDataType.FORM)
                .addBody("value", "90%25%E9%85%92%E7%B2%BE")
                .execute(String.class);
        mockRequest(server)
                .assertPathEquals("/")
                .assertBodyEquals("value=90%25%E9%85%92%E7%B2%BE");
    }



    @Test
    public void testRequest_query_array2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("/")
                .port(server.getPort())
                .addArrayQuery("a", 1, 2, 3)
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a[]=1&a[]=2&a[]=3");
    }


    @Test
    public void testRequest_query_replace() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("/")
                .port(server.getPort())
                .addQuery("a", "1")
                .replaceQuery("a", 2)
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=2");
    }

    @Test
    public void testRequest_query_replace_add() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("/")
                .port(server.getPort())
                .replaceOrAddQuery("a", "1")
                .replaceOrAddQuery("a", "2")
                .addQuery("url", "http://localhost/test")
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=2&url=http://localhost/test");
    }

    @Test
    public void testRequest_query_replace_add2() throws UnsupportedEncodingException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<?> request = Forest.get("/")
                .port(server.getPort())
                .replaceOrAddQuery("a", "1")
                .replaceOrAddQuery("a", "2")
                .addQuery("url", "http://localhost/test", true, "UTF-8");
        request.execute();

        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=2&url=http://localhost/test");
    }

    @Test
    public void testRequest_query_map() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        Forest.get("/")
                .port(server.getPort())
                .addQuery(map)
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&b=2&c=3");
    }

    @Test
    public void testRequest_json_path() {
        server.enqueue(new MockResponse().setBody("{\"status\":\"1\", \"data\":[{\"name\": \"foo\", \"age\": \"18\", \"phone\": \"12345678\"}]}"));
        ForestResponse response = Forest.get("http://localhost:{}", server.getPort()).executeAsResponse();
        String result = response.get(String.class);
        final Object document = Configuration.defaultConfiguration().jsonProvider().parse(result);
        Object data = JsonPath.read(document, "$.data");
        Type type = new TypeReference<List<Contact>>() {}.getType();
        List<Contact> contact = JSON.parseObject(JSON.toJSONString(data), type);
        System.out.println(contact);
    }


    @Test
    public void testAuth_UsernamePassword() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest request =
                Forest.get("http://foo:bar@localhost:{}/xxx", server.getPort());
        assertThat(request.userInfo()).isEqualTo("foo:bar");
        request.execute();
        mockRequest(server)
                .assertHeaderEquals("Authorization", "Basic Zm9vOmJhcg==");
    }

    @Test
    public void testAuth_BasicAuth() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("/")
                .port(server.getPort())
                .authenticator(new BasicAuth("foo", "bar"))
                .execute();
        mockRequest(server)
                .assertHeaderEquals("Authorization", "Basic Zm9vOmJhcg==");
    }

    @Test
    public void testAuth_BasicAuth2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("/")
                .port(server.getPort())
                .authenticator(new BasicAuth("foo:bar"))
                .execute();
        mockRequest(server)
                .assertHeaderEquals("Authorization", "Basic Zm9vOmJhcg==");
    }

    @Test
    public void testAuth_BasicAuth3() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        BasicAuth basicAuth = new BasicAuth("xx", "yy");
        basicAuth.setUserInfo("foo:bar");

        Forest.get("/")
                .port(server.getPort())
                .authenticator(basicAuth)
                .execute();
        mockRequest(server)
                .assertHeaderEquals("Authorization", "Basic Zm9vOmJhcg==");
    }


    @Test
    public void testAuth_BearerAuth() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("/")
                .port(server.getPort())
                .authenticator(new BearerAuth("test-token-xxxx"))
                .execute();
        mockRequest(server)
                .assertHeaderEquals("Authorization", "Bearer test-token-xxxx");
    }



    @Test
    public void testAsyncPool() {
        ForestConfiguration config = ForestConfiguration.createConfiguration()
                .setMaxAsyncThreadSize(100)
                .setMaxAsyncQueueSize(100);
        LogConfiguration logConfiguration = new LogConfiguration();
        logConfiguration.setLogEnabled(false);
        for (int j = 0; j < 10; j++) {
            final int total = 10;
            for (int i = 0; i < total; i++) {
                MockResponse response = new MockResponse().setBody(EXPECTED);
                if (i == 9) {
                    response.setHeadersDelay(2, TimeUnit.SECONDS);
                }
                server.enqueue(response);
            }
            final CountDownLatch latch = new CountDownLatch(total);
            final AtomicInteger count = new AtomicInteger(0);
            final AtomicInteger errorCount = new AtomicInteger(0);
            for (int i = 0; i < total; i++) {
                config.get("/")
                        .host("localhost")
                        .port(server.getPort())
                        .addAttachment("num", i + "-" + j)
                        .connectTimeout(200)
                        .readTimeout(200)
                        .async()
                        .setLogConfiguration(logConfiguration)
                        .onSuccess((data, req, res) -> {
                            latch.countDown();
                            int c = count.incrementAndGet();
                            System.out.println(Thread.currentThread().getName() + " 成功: " + req.getAttachment("num"));
                            if (c == total) {
//                                System.out.println("第一阶段: 循环已完成");
                            } else {
//                                System.out.println("已成功 第一阶段: " + c);
                            }
                        })
                        .onError((ex, req, res) -> {
                            latch.countDown();
                            int c = count.incrementAndGet();
                            errorCount.incrementAndGet();
                            System.out.println(Thread.currentThread().getName() + " 失败: " + req.getAttachment("num"));
                            if (c == total) {
//                                System.out.println("第一阶段: 循环已完成");
                            } else {
//                                System.out.println("已失败 第一阶段: " + c);
                            }

                            ex.printStackTrace();
                        })
                        .execute();
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
            }
            assertThat(errorCount.get()).isGreaterThan(0);

            for (int i = 0; i < total; i++) {
                server.enqueue(new MockResponse().setHeader("Status", "Ok"));
            }
            final CountDownLatch latch2 = new CountDownLatch(total);
            final AtomicInteger count2 = new AtomicInteger(0);
            final AtomicInteger errorCount2 = new AtomicInteger(0);
            for (int i = 0; i < total; i++) {
                config.head("/")
                        .host("localhost")
                        .port(server.getPort())
                        .async()
                        .setLogConfiguration(logConfiguration)
                        .onSuccess((data, req, res) -> {
                            latch2.countDown();
                            int c = count2.incrementAndGet();
                            if (c == total) {
//                                System.out.println("第二阶段: 循环已完成");
                            } else {
//                                System.out.println("已成功 第二阶段: " + c);
                            }
                        })
                        .onError((ex, req, res) -> {
                            latch2.countDown();
                            int c = count2.incrementAndGet();
                            if (ex != null) {
//                                System.out.println("第二阶段 异常: " + ex);
                                errorCount2.incrementAndGet();
                            }
                            if (c == total) {
//                                System.out.println("第二阶段: 循环已失败");
                            } else {
                                System.out.println("已失败 第二阶段: " + c);
                            }
                        })
                        .execute();
            }
            try {
                latch2.await();
            } catch (InterruptedException e) {
            }
            assertThat(errorCount2.get()).isEqualTo(0);
//            System.out.println("全部已完成");
        }
    }


    @Test
    public void testRequest_query_map2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Map<String, Object> map = new LinkedHashMap<>();
        List<Integer> list = Arrays.asList(10, 20, 30);
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        map.put("x", list);
        Forest.get("/")
                .port(server.getPort())
                .addQuery(map)
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&b=2&c=3&x=10&x=20&x=30");
    }

    public static class MyQuery {

        @JSONField(ordinal = 0)
        private Integer a;

        @JSONField(ordinal = 1)
        private Integer b;

        @JSONField(ordinal = 2)
        private Integer c;

        @JSONField(ordinal = 3)
        private List<Integer> x;

        public Integer getA() {
            return a;
        }

        public void setA(Integer a) {
            this.a = a;
        }

        public Integer getB() {
            return b;
        }

        public void setB(Integer b) {
            this.b = b;
        }

        public Integer getC() {
            return c;
        }

        public void setC(Integer c) {
            this.c = c;
        }

        public List<Integer> getX() {
            return x;
        }

        public void setX(List<Integer> x) {
            this.x = x;
        }
    }

    @Test
    public void testRequest_query_obj() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        MyQuery myQuery = new MyQuery();
        myQuery.setA(1);
        myQuery.setB(2);
        myQuery.setC(3);
        Forest.get("/")
                .port(server.getPort())
                .addQuery(myQuery)
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&b=2&c=3");
    }


    @Test
    public void testRequest_query_obj2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        MyQuery myQuery = new MyQuery();
        myQuery.setA(1);
        myQuery.setB(2);
        myQuery.setC(3);
        myQuery.setX(Arrays.asList(10, 20, 30));
        Forest.get("/")
                .port(server.getPort())
                .addQuery(myQuery)
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&b=2&c=3&x=10&x=20&x=30");
    }

    @Test
    public void testRequest_query_json() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        MyQuery myQuery = new MyQuery();
        myQuery.setA(1);
        myQuery.setB(2);
        myQuery.setC(3);
        myQuery.setX(Arrays.asList(10, 20, 30));
        Forest.get("/")
                .port(server.getPort())
                .addJSONQuery("json", myQuery)
                .addQuery("x", "\"`~\"")
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("json", JSON.toJSONString(myQuery));
    }


    @Test
    public void testRequest_host_port() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("http://xxxxx:444/path")
                .host("localhost")
                .port(server.getPort())
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/path");
    }

    @Test
    public void testRequest_address() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<?> request = Forest.get("http://xxxxx:444/path")
                .address("localhost", server.getPort());
        assertThat(request.host()).isEqualTo("localhost");
        String result = request.executeAsString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/path");
    }


    @Test
    public void testRequest_path() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("/A")
                .host("localhost")
                .port(server.getPort())
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/A");
    }

    @Test
    public void testRequest_change_path() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("/A")
                .host("localhost")
                .port(server.getPort())
                .path("/B")
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/B");
    }

    @Test
    public void testRequest_change_base_path() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("/A")
                .host("localhost")
                .port(server.getPort())
                .basePath("1/2/3/a/b/c")
                .basePath("X/Y/Z")
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/X/Y/Z/A");
    }

    @Test
    public void testRequest_change_base_path2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("/A")
                .host("localhost")
                .port(server.getPort())
                .basePath("X")
                .path("B")
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/X/B");
    }

    @Test
    public void testRequest_change_base_path3() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("/A")
                .host("localhost")
                .port(server.getPort())
                .path("B")
                .basePath("X")
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/X/B");
    }

    @Test
    public void testRequest_change_base_path4() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("http://localhost:{}/abc/A", server.getPort())
                .basePath("X")
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/X/abc/A");
    }

    @Test
    public void testRequest_change_base_path5() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest request = Forest.get("/A")
                .basePath("http://localhost:" + server.getPort() + "/X1/X2");
        String result = request.executeAsString();
        assertThat(request.basePath()).isEqualTo("/X1/X2");
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/X1/X2/A");
    }

    @Test
    public void testRequest_change_base_path6() {
        ForestRequest request = Forest.get("/A")
                .host("baidu.com")
                .port(1234)
                .basePath("http://localhost:8080/X1/X2");
        assertThat(request).isNotNull();
        assertThat(request.host()).isEqualTo("localhost");
        assertThat(request.port()).isEqualTo(8080);
        assertThat(request.urlString()).isEqualTo("http://localhost:8080/X1/X2/A");
    }

    @Test
    public void testRequest_change_base_path7() {
        ForestRequest request = Forest.get("/A")
                .basePath("http://localhost:8080/X1/X2")
                .host("baidu.com")
                .port(1234);
        assertThat(request).isNotNull();
        assertThat(request.host()).isEqualTo("baidu.com");
        assertThat(request.port()).isEqualTo(1234);
        assertThat(request.urlString()).isEqualTo("http://baidu.com:1234/X1/X2/A");
    }


    @Test
    public void testRequest_change_url() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<?> request = Forest.get("/A")
                .host("127.0.0.1")
                .port(server.getPort())
                .url("/B");
        assertThat(request.getHost()).isEqualTo("127.0.0.1");
        String result = request.executeAsString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/B");
    }

    @Test
    public void testRequest_template_in_url() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.config()
                .setVariable("hello", "world")
                .setVariable("bar", "ok")
                .setVariable("foo", "foo/{bar}")
                .setVariable("testVar", "var/{foo}/{hello}");
        Forest.get("/test/{testVar}")
                .host(server.getHostName())
                .port(server.getPort())
                .execute();
        mockRequest(server)
                .assertPathEquals("/test/var/foo/ok/world");
    }


    @Test
    public void testRequest_template_in_url2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.config().setVariable("testVar", null);
        Forest.get("/test/{testVar ?? 'foo'}/{testVar?}/{testVar ?? ''}")
                .host(server.getHostName())
                .port(server.getPort())
                .execute();
        mockRequest(server)
                .assertPathEquals("/test/foo/null/");
    }

    @Test
    public void testRequest_template_in_url3() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.config()
                .setVariable("testVar", null)
                .setVariable("testVar2", "ok");
        Forest.get("/test/{testVar?.a}/{testVar?.a ?? 'foo'}/{testVar?.a?.b.c.d.e ?? 'bar'}/{testVar2 ?? 'xx'}/{testVar2?}")
                .host(server.getHostName())
                .port(server.getPort())
                .execute();

        mockRequest(server)
                .assertPathEquals("/test/null/foo/bar/ok/ok");
    }


    @Test
    public void testRequest_template_in_url4() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.config()
                .removeVariable("testVar")
                .setVariable("testVar2", "ok");
        Forest.get("/test/{testVar ?? `it_is_{testVar2}`}")
                .host(server.getHostName())
                .port(server.getPort())
                .execute();

        mockRequest(server)
                .assertPathEquals("/test/it_is_ok");
    }

    @Test
    public void testRequest_template_in_url5() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.config().removeVariable("testVar");
        Forest.get("/test/{testVar?}")
                .host(server.getHostName())
                .port(server.getPort())
                .execute();

        mockRequest(server)
                .assertPathEquals("/test/null");
    }

    @Test
    public void testRequest_template_in_url6() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.config()
                .removeVariable("foo")
                .setVariable("testVar3", "aaa\\{foo}")
                .setVariable("testVar2", "ok: {testVar3}")
                .setVariable("testVar1", "{testVar2}");
        Forest.get("/test/{testVar1}")
                .host(server.getHostName())
                .port(server.getPort())
                .execute();
        mockRequest(server)
                .assertPathEquals(URLEncoder.PATH.encode("/test/ok: aaa{foo}", "UTF-8"));
    }


    @Test
    public void testRequest_template_in_url7() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.config()
                .removeVariable("foo")
                .setVariable("testVar3", "aaa{foo}")
                .setVariable("testVar2", "ok: {testVar3}")
                .setVariable("testVar1", "{testVar2}");
        Forest.get("/test/{testVar1!}")
                .host(server.getHostName())
                .port(server.getPort())
                .execute();
        mockRequest(server)
                .assertPathEquals(URLEncoder.PATH.encode("/test/{testVar2}", "UTF-8"));
    }



    @Test
    public void testRequest_template_in_url8() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.config()
                .removeVariable("foo")
                .setVariable("testVar3", "aaa{foo}")
                .setVariable("testVar2", "ok: {testVar3?!}")
                .setVariable("testVar1", "{testVar2}");
        Forest.get("/test/{testVar1}")
                .host(server.getHostName())
                .port(server.getPort())
                .execute();
        mockRequest(server)
                .assertPathEquals(URLEncoder.PATH.encode("/test/ok: aaa{foo}", "UTF-8"));
    }


    @Test
    public void testRequest_json_template() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.config()
                .setVariable("testVar", "{\"a\": \"foo\", \"b\": \"bar\"}")
                .setVariable("testVar2", "{testVar}");
        Forest.post("/test")
                .host(server.getHostName())
                .port(server.getPort())
                .contentTypeJson()
                .addBody("{testVar2}")
                .execute();

        mockRequest(server)
                .assertPathEquals("/test")
                .assertBodyEquals("{\"a\": \"foo\", \"b\": \"bar\"}");
    }


    @Test
    public void testRequest_template_in_url_error1() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.config().setVariable("testVar", MapUtil.of("a", null));
        assertThatThrownBy(() -> {
            Forest.get("/test/{testVar.foo.value}")
                    .host(server.getHostName())
                    .port(server.getPort())
                    .execute();
        }).isInstanceOf(ForestExpressionNullException.class);
    }

    @Test
    public void testRequest_template_in_url_error2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.config().setVariable("testVar", MapUtil.of("a", null));
        assertThatThrownBy(() -> {
            Forest.get("/test/{testVar.foo ??}")
                    .host(server.getHostName())
                    .port(server.getPort())
                    .execute();
        })
                .isInstanceOf(ForestExpressionException.class)
                .hasMessageContaining("Unexpected token '??'");
    }

    @Test
    public void testRequest_template_in_url_error3() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.config()
                .setVariable("testVar", null)
                .setVariable("map", MapUtil.empty());

        assertThatThrownBy(() -> {
            Forest.get("/test/{testVar ?? `it_is_{map?.foo.bar.aaa.bbb}`}")
                    .host(server.getHostName())
                    .port(server.getPort())
                    .execute();
        })
                .isInstanceOf(ForestExpressionNullException.class)
                .hasMessageContaining("Null pointer error: map?.foo is null");
    }

    @Test
    public void testRequest_template_in_url_error4() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.config()
                .removeVariable("foo")
                .setVariable("testVar3", "aaa{foo}")
                .setVariable("testVar2", "ok: {testVar3}")
                .setVariable("testVar1", "{testVar2}");

        assertThatThrownBy(() -> {
            Forest.get("/test/{testVar1}")
                    .host(server.getHostName())
                    .port(server.getPort())
                    .execute();
        }).hasMessageContainingAll(
                "\"aaa{foo}\"",
                "\"ok: {testVar3}\"",
                "\"{testVar2}\"",
                "/test/{testVar1}",
                "^^^ Cannot resolve variable 'foo'",
                "^^^^^^^^ Reference error: testVar3 -> \"aaa{foo}\"",
                "^^^^^^^^ Reference error: testVar2 -> \"ok: {testVar3}\"",
                "^^^^^^^^ Reference error: testVar1 -> \"{testVar2}\""
        );
    }

    @Test
    public void testRequest_template_in_url_error5() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.config()
                .removeVariable("foo")
                .setVariable("testVar3", "aaa{foo}")
                .setVariable("testVar2", "ok: {temp ?? `haha {testVar3}`}")
                .setVariable("testVar1", MapUtil.of("ref", "{testVar2}"));

        assertThatThrownBy(() -> {
            Forest.get("/test/{testVar1.ref}")
                    .host(server.getHostName())
                    .port(server.getPort())
                    .execute();
        }).hasMessageContainingAll(
                "testVar3 -> \"aaa{foo}\"",
                "^^^ Cannot resolve variable 'foo'",

                "ok: {temp ?? `haha {testVar3}`}",
                "^^^^^^^^ Reference error: testVar3 -> \"aaa{foo}\"",

                "{testVar2}",
                "^^^^^^^^ Reference error: testVar2 -> \"ok: {temp ?? `haha {testVar3}`}\"",

                "/test/{testVar1.ref}",
                "^^^^^^^^^^^^ Reference error: testVar1.ref -> \"{testVar2}\""
        );
    }


    @Test
    public void testRequest_get_return_string() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("http://localhost:{}", server.getPort()).execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testRequest_get_return_string2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("http://localhost:{}", server.getPort()).executeAsString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }



/*
    @Test
    public void testRequest_get_http_1_0() {
        server.setProtocols(Lists.newArrayList(Protocol.HTTP_2));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest
                .get("http://localhost:" + server.getPort())
                .setProtocol(ForestProtocol.HTTP_2)
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }
*/


    @Test
    public void testRequest_get_return_map() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Map<String, String> result = Forest.get("/")
                .address("localhost", server.getPort())
                .executeAsMap();
        assertThat(result).isNotNull();
        assertThat(result.get("status")).isEqualTo("1");
        assertThat(result.get("data")).isEqualTo("2");
    }


    @Test
    public void testRequest_get_return_map_onResponse() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Map<String, String> result = Forest.get("/")
                .address("localhost", server.getPort())
                .onResponse((req, res) -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("a", "1");
                    map.put("b", "2");
                    return ResponseResult.success(map);
                })
                .executeAsMap();
        assertThat(result).isNotNull();
        assertThat(result.get("a")).isEqualTo("1");
        assertThat(result.get("b")).isEqualTo("2");
    }


    @Test
    public void testRequest_get_return_map2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Map<String, String> result = Forest.get("/")
                .address("localhost", server.getPort())
                .execute(new TypeReference<Map<String, String>>() {
                }.getType());
        assertThat(result).isNotNull();
        assertThat(result.get("status")).isEqualTo("1");
        assertThat(result.get("data")).isEqualTo("2");
    }


    @Test
    public void testRequest_get_return_list() {
        server.enqueue(new MockResponse().setBody("[1, 2, 3]"));
        List<Integer> result = Forest.get("http://localhost:{}", server.getPort()).executeAsList();
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(Lists.newArrayList(1, 2, 3));
    }

    @Test
    public void testRequest_get_return_list_onResponse() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        List<Integer> result = Forest.get("http://localhost:{}", server.getPort())
                .onResponse((req, res) -> ResponseResult.success(Lists.newArrayList(1, 2, 3)))
                .executeAsList();
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(Lists.newArrayList(1, 2, 3));
    }

    @Test
    public void testRequest_get_return_opt() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Optional<Integer> result = Forest.get("http://localhost:{}", server.getPort())
                .onResponse((req, res) -> ResponseResult.success(5))
                .executeAsOpt();
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(Optional.of(5));
    }

    @Test
    public void testRequest_get_return_opt_null() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Optional<Integer> result = Forest.get("http://localhost:{}", server.getPort())
                .onResponse((req, res) -> ResponseResult.success(null))
                .executeAsOpt();
        assertThat(result).isNotNull();
        assertThat(result.orElse(-1)).isEqualTo(-1);
    }

    @Test
    public void testRequest_get_return_opt2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Optional<String> result = Forest.get("http://localhost:{}", server.getPort())
                .onResponse((req, res) -> ResponseResult.success("xxx"))
                .executeAsOpt(String.class);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(Optional.of("xxx"));
    }

    @Test
    public void testRequest_get_return_opt3() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Optional<Map<String, String>> result = Forest.get("http://localhost:{}", server.getPort())
                .executeAsOpt(new TypeReference<Map<String, String>>() {});
        assertThat(result).isNotNull();
        assertThat(result.get().get("status")).isEqualTo("1");
        assertThat(result.get().get("data")).isEqualTo("2");
    }

    @Test
    public void testRequest_get_return_opt4() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Optional<List<Integer>> result = Forest.get("http://localhost:{}", server.getPort())
                .onResponse((req, res) -> ResponseResult.success(Lists.newArrayList(1, 2, 3)))
                .executeAsOpt(new TypeReference<List<Integer>>() {});
        assertThat(result).isNotNull();
        assertThat(result.get()).isEqualTo(Lists.newArrayList(1, 2, 3));
    }


    @Test
    public void testRequest_get_return_stream_onResponse() {
        server.enqueue(new MockResponse().setBody("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]"));
        List<Integer> result = Forest.get("http://localhost:{}", server.getPort())
                .onResponse((req, res) -> {
                    if (res.isError()) {
                        return ResponseResult.error();
                    }
                    try (JSONReader jsonReader = new JSONReader(new InputStreamReader(res.getInputStream()))) {
                        List<Integer> list = new ArrayList<>();
                        list.add(0);
                        jsonReader.startArray();
                        while (jsonReader.hasNext()) {
                            list.add(jsonReader.readInteger());
                        }
                        jsonReader.endArray();
                        return ResponseResult.success(list);
                    } catch (Exception e) {
                        return ResponseResult.error(e);
                    }
                })
                .onSuccess(((data, req, res) -> {}))
                .executeAsList();
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(Lists.newArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test
    public void testRequest_auto_closed_response_openStream() {
        server.enqueue(new MockResponse().setBody("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]"));
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
        Forest.get("http://localhost:{}", server.getPort())
                .executeAsResponse()
                .openStream((in, res) -> {
                    JSONReader jsonReader = new JSONReader(new InputStreamReader(in));
                    queue.add(0);
                    jsonReader.startArray();
                    while (jsonReader.hasNext()) {
                        queue.add(jsonReader.readInteger());
                    }
                    jsonReader.endArray();
                });
        assertThat(queue).isNotNull();
        assertThat(queue.toArray()).isEqualTo(new Integer[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
    }


    @Test
    public void testRequest_unclosed_response_openStream() {
        server.enqueue(new MockResponse().setBody("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]"));
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
        Forest.get("http://localhost:{}", server.getPort())
                .executeAsUnclosedResponse()
                .openStream((in, res) -> {
                    JSONReader jsonReader = new JSONReader(new InputStreamReader(in));
                    queue.add(0);
                    jsonReader.startArray();
                    while (jsonReader.hasNext()) {
                        queue.add(jsonReader.readInteger());
                    }
                    jsonReader.endArray();
                });
        assertThat(queue).isNotNull();
        assertThat(queue.toArray()).isEqualTo(new Integer[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
    }


    @Test
    public void testRequest_auto_closed_response() {
        server.enqueue(new MockResponse().setBody("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]"));
        ForestResponse response = Forest.get("http://localhost:{}", server.getPort())
                .executeAsResponse();
        assertThat(response).isNotNull();
        assertThat(response.isClosed()).isTrue();
    }

    @Test
    public void testRequest_unclosed_response() {
        server.enqueue(new MockResponse().setBody("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]"));
        UnclosedResponse response = Forest.get("http://localhost:{}", server.getPort())
                .executeAsUnclosedResponse();
        assertThat(response).isNotNull();
        assertThat(response.isClosed()).isFalse();
        response.close();
        assertThat(response.isClosed()).isTrue();
    }

    @Test
    public void testRequest_unclosed_response_get() {
        server.enqueue(new MockResponse().setBody("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]"));
        UnclosedResponse response = Forest.get("http://localhost:{}", server.getPort())
                .executeAsUnclosedResponse();
        assertThat(response).isNotNull();
        assertThat(response.isClosed()).isFalse();
        List<Integer> nums = response.get(new TypeReference<List<Integer>>() {});
        assertThat(response.isClosed()).isTrue();
        assertThat(nums).isNotNull();
        assertThat(nums).isEqualTo(Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }



    @Test
    public void testRequest_get_return_map_list() {
        server.enqueue(new MockResponse().setBody("[{\"a\": 1}, {\"b\": 2}, {\"c\": 3}]"));
        List<Map<String, Object>> result = Forest.get("http://localhost:{}", server.getPort()).executeAsList();
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).get("a")).isEqualTo(1);
        assertThat(result.get(1).get("b")).isEqualTo(2);
        assertThat(result.get(2).get("c")).isEqualTo(3);
    }

    @Test
    public void testRequest_get_return_response() {
        server.enqueue(new MockResponse().setBody("{\"a\": 1, \"b\": 2, \"c\": 3}"));
        ForestResponse<Map<String, Object>> response = Forest.get("http://localhost:{}", server.getPort())
                .execute(new TypeReference<ForestResponse<Map<String, Object>>>() {});
        assertThat(response).isNotNull();
        Map<String, Object> result = response.getResult();
        assertThat(result).isNotNull();
        assertThat(result.get("a")).isEqualTo(1);
        assertThat(result.get("b")).isEqualTo(2);
        assertThat(result.get("c")).isEqualTo(3);
        assertThat(response.isClosed()).isTrue();
        assertThat(response.getResult()).isNotNull();
    }


    @Test
    public void testRequest_get_return_list2() {
        server.enqueue(new MockResponse().setBody("[\"1\", \"2\", \"3\"]"));
        List<String> result = Forest.get("/")
                .address("localhost", server.getPort())
                .executeAsList();
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(Lists.newArrayList("1", "2", "3"));
    }


    @Test
    public void testRequest_get_return_type() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Type type = new TypeReference<Map<String, Integer>>() {
        }.getType();
        Map<String, Integer> result = Forest.get("http://localhost:{}", server.getPort()).execute(type);
        assertThat(result).isNotNull();
        assertThat(result.get("status")).isEqualTo(1);
        assertThat(result.get("data")).isEqualTo(2);
    }


    @Test
    public void testRequest_get_return_JavaObject() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Result result = Forest.get("http://localhost:{}", server.getPort()).execute(Result.class);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo("2");
    }

    @Test
    public void testRequest_get_return_JavaObject_with_genericType() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.get("http://localhost:{}", server.getPort()).execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
    }





    @Test
    public void testRequest_get_query_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.get("http://localhost:{}", server.getPort())
                .addHeader(ForestHeader.USER_AGENT, "forest")
                .addQuery("a", 1)
                .addQuery("b", 2)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/")
                .assertHeaderEquals(ForestHeader.USER_AGENT, "forest")
                .assertQueryEquals("a", "1")
                .assertQueryEquals("b", "2");
    }

    @Test
    public void testRequest_get_query_map() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        Result<Integer> result = Forest.get("http://localhost:{}", server.getPort())
                .addHeader(ForestHeader.USER_AGENT, "forest")
                .addQuery(map)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/")
                .assertHeaderEquals(ForestHeader.USER_AGENT, "forest")
                .assertQueryEquals("a", "1")
                .assertQueryEquals("b", "2");
    }


    @Test
    public void testRequest_get_query_obj() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Data data = new Data();
        data.setA(1);
        data.setB(2);
        Result<Integer> result = Forest.get("http://localhost:{}", server.getPort())
                .addHeader(ForestHeader.USER_AGENT, "forest")
                .addQuery(data)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("GET")
                .assertPathEquals("/")
                .assertHeaderEquals(ForestHeader.USER_AGENT, "forest")
                .assertQueryEquals("a", "1")
                .assertQueryEquals("b", "2");
    }


    @Test
    public void testRequest_post_text_without_content_type() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.post("http://localhost:{}", server.getPort())
                .addBody("xxxxxxxyyyyyyy")
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertBodyEquals("xxxxxxxyyyyyyy");
    }

    @Test
    public void testRequest_post_form_without_content_type() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("value", "bar");
        map.put("name", "foo");
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.post("http://localhost:{}", server.getPort())
                .addBody(map)
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertBodyEquals("value=bar&name=foo");
    }


    @Test
    public void testRequest_post_invalid_json() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.post("http://localhost:{}", server.getPort())
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .addBody("xxxxxxxyyyyyyy")
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertHeaderEquals("Content-Type", "application/json;charset=UTF-8")
                .assertBodyEquals("xxxxxxxyyyyyyy");
    }

    @Test
    public void testRequest_content_type_with_charset() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:{}/test", server.getPort())
                .addHeader("Content-Type", "multipart/form-data")
                .bodyType(ForestDataType.TEXT)
                .addBody("{\"id\":\"1972664191\", \"name\":\"XieYu20011008\"}")
                .executeAsString();
        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\", \"name\":\"XieYu20011008\"}")
                .assertHeaderEquals("Content-Type", "multipart/form-data");
    }

    @Test
    public void testRequest_lazy_query() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("/")
                .port(server.getPort())
                .addQuery("a", "1")
                .addQuery("b", "2")
                .addQuery("c", req -> "3")
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&b=2&c=3");
    }


    @Test
    public void testRequest_lazy_query2() throws UnsupportedEncodingException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("/")
                .port(server.getPort())
                .addQuery("a", 1)
                .addQuery("b", 2)
                .addQuery("c", req -> 3)
                .addQuery("token", req -> Base64.encode(req.getQueryString()))
                .execute();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&b=2&c=3&token=" + URLUtils.encode(Base64.encode("a=1&b=2&c=3"), "UTF-8"));
    }


    @Test
    public void testRequest_lazy_query3() throws UnsupportedEncodingException {
        server.enqueue(new MockResponse().setBody(EXPECTED));

        LazyData data = new LazyData();
        data.setId("foo");
        data.setName("bar");
        data.setToken(req -> Base64.encode(req.getQueryString()));

        Forest.get("/")
                .port(server.getPort())
                .addQuery(data)
                .execute();

        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("id=foo&name=bar&token=" + URLUtils.encode(Base64.encode("id=foo&name=bar"), "UTF-8"));
    }

    @Test
    public void testRequest_lazy_query4() throws UnsupportedEncodingException {
        server.enqueue(new MockResponse().setBody(EXPECTED));

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("a", "1");
        data.put("b", "2");
        data.put("c", (Lazy<Object>) (req -> "3"));
        data.put("token", (Lazy<Object>) (req -> Base64.encode(req.getQueryString())));

        ForestRequest request = Forest.get("/")
                .port(server.getPort())
                .addQuery(data);
        request.execute();

        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&b=2&c=3&token=" + URLUtils.encode(Base64.encode("a=1&b=2&c=3"), "UTF-8"));
    }


    @Test
    public void testRequest_body_encode_null_value() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        final UserParam param = new UserParam();
        param.setUsername("xxx");
        final ForestRequest request = Forest.post("/")
                .port(server.getPort())
                .bodyType(ForestDataType.JSON)
                .addBody(param);
        
        UserParam fromBody = request.body().get(UserParam.class);
        assertThat(fromBody).isNotNull();
        assertThat(fromBody.getUsername()).isEqualTo(param.getUsername());
        
        System.out.println(request.body()
                .encodeToString(
                        ConvertOptions.defaultOptions()
                                .nullValuePolicy(ConvertOptions.NullValuePolicy.WRITE_EMPTY_STRING)));
    }

    @Test
    public void testRequest_blank_header() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:{}/test", server.getPort())
                .contentTypeJson()
                .addHeader("name", " x xx ")
                .addBody("{\"id\":\"1972664191\", \"name\":\"XieYu20011008\"}")
                .execute();
        mockRequest(server)
                .assertHeaderEquals("name", "x xx");
    }

    @Test
    public void testRequest_lazy_header() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:{}/test", server.getPort())
                .contentTypeJson()
                .addHeader("Content-Type", req -> "application/json;charset=UTF-8")
                .addHeader("name", req -> "Forest.backend = " + req.getBackend().getName())
                .addBody("{\"id\":\"1972664191\", \"name\":\"XieYu20011008\"}")
                .execute();
        mockRequest(server)
                .assertHeaderEquals("name", "Forest.backend = " + Forest.config().getBackend().getName())
                .assertBodyEquals("{\"id\":\"1972664191\", \"name\":\"XieYu20011008\"}")
                .assertHeaderEquals("Content-Type", "application/json;charset=UTF-8");
    }

    @Test
    public void testRequest_lazy_header2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:{}/test", server.getPort())
                .addHeader("Content-Type", "application/json;charset=GBK")
                .addHeader("Authorization", req -> Base64.encode("Token=" + req.body().encodeToString()))
                .addBody("id", "1972664191")
                .addBody("name", "XieYu20011008")
                .execute();
        mockRequest(server)
                .assertHeaderEquals("Authorization",
                        Base64.encode("Token={\"id\":\"1972664191\",\"name\":\"XieYu20011008\"}"))
                .assertBodyEquals("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\"}")
                .assertHeaderEquals("Content-Type", "application/json;charset=GBK");
    }

    @Test
    public void testRequest_lazy_header3() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:{}/test", server.getPort())
                .contentFormUrlEncoded()
                .addHeader("Authorization", req -> Base64.encode("Token=" + req.body().encodeToString()))
                .addBody("id", "1972664191")
                .addBody("name", "XieYu20011008")
                .execute();
        mockRequest(server)
                .assertHeaderEquals("Authorization",
                        Base64.encode("Token=id=1972664191&name=XieYu20011008"))
                .assertBodyEquals("id=1972664191&name=XieYu20011008")
                .assertHeaderEquals("Content-Type", "application/x-www-form-urlencoded");
    }


    @Test
    public void testRequest_lazy_body() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:{}/test", server.getPort())
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .addHeader("_id", "20011008")
                .addBody("id", "1972664191")
                .addBody("name", req -> "XieYu" + req.headerValue("_id"))
                .execute();
        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\"}")
                .assertHeaderEquals("Content-Type", "application/json;charset=UTF-8");
    }


    @Test
    public void testRequest_lazy_body2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:{}/test", server.getPort())
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .addHeader("_id", "20011008")
                .addBody("id", "1972664191")
                .addBody("name", req -> "XieYu" + req.headerValue("_id"))
                .addBody("token", req -> Base64.encode(
                        req.body().encodeToString(
                                ConvertOptions.defaultOptions().exclude("token"))))
                .execute();
        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\",\"token\":\"" +
                        Base64.encode("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\"}") +
                        "\"}")
                .assertHeaderEquals("Content-Type", "application/json;charset=UTF-8");
    }

    @Test
    public void testRequest_lazy_body3() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:{}/test", server.getPort())
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .addHeader("_id", "20011008")
                .addBody("id", "1972664191")
                .addBody("name", req -> "XieYu" + req.headerValue("_id"))
                .addBody("token", req -> Base64.encode(req.body().encode()))
                .execute();
        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\",\"token\":\"" +
                        Base64.encode("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\"}") +
                        "\"}")
                .assertHeaderEquals("Content-Type", "application/json;charset=UTF-8");
    }

    @Test
    public void testRequest_lazy_body4() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:{}/test", server.getPort())
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .addHeader("_id", "20011008")
                .addBody("id", "1972664191")
                .addBody("name", req -> "XieYu" + req.headerValue("_id"))
                .addBody("token", req -> Base64.encode(req.body().encode(ForestDataType.FORM)))
                .execute();
        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\",\"token\":\"" +
                        Base64.encode("id=1972664191&name=XieYu20011008") +
                        "\"}")
                .assertHeaderEquals("Content-Type", "application/json;charset=UTF-8");
    }


    @Test
    public void testRequest_lazy_body5() {
        server.enqueue(new MockResponse().setBody(EXPECTED));

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", "1972664191");
        data.put("name", "XieYu20011008");

        ForestRequest request = Forest.post("http://localhost:{}/test", server.getPort())
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .addHeader("_id", "20011008")
                .addBody(data)
                .addBody("token", req -> Base64.encode(req.body().encode()));
        
        Map fromBody = request.body().get(Map.class);
        assertThat(fromBody).isNotNull();
        assertThat(fromBody.get("id")).isEqualTo("1972664191");
        assertThat(fromBody.get("name")).isEqualTo("XieYu20011008");

        request.execute();

        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\",\"token\":\"" +
                        Base64.encode("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\"}") +
                        "\"}")
                .assertHeaderEquals("Content-Type", "application/json;charset=UTF-8");
    }


    @Test
    public void testRequest_lazy_body6() {
        server.enqueue(new MockResponse().setBody(EXPECTED));

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", "1972664191");
        data.put("name", "XieYu20011008");
        data.put("token", (Lazy<Object>) (req -> Base64.encode(req.body().encode())));

        Forest.post("http://localhost:{}/test", server.getPort())
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .addHeader("_id", "20011008")
                .addBody(data)
                .execute();

        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\",\"token\":\"" +
                        Base64.encode("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\"}") +
                        "\"}")
                .assertHeaderEquals("Content-Type", "application/json;charset=UTF-8");
    }

    public static class LazyData {

        @JSONField(ordinal = 0)
        private String id;

        @JSONField(ordinal = 1)
        private String name;

        @JSONField(ordinal = 2)
        private Lazy<Object> token;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Lazy<Object> getToken() {
            return token;
        }

        public void setToken(Lazy<Object> token) {
            this.token = token;
        }
    }

    @Test
    public void testRequest_lazy_body7() {
        server.enqueue(new MockResponse().setBody(EXPECTED));

        LazyData data = new LazyData();
        data.setId("1972664191");
        data.setName("XieYu20011008");
        data.setToken(req -> Base64.encode(req.body().encode()));

        Forest.post("http://localhost:{}/test", server.getPort())
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .addHeader("_id", "20011008")
                .addBody(data)
                .execute();

        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\",\"token\":\"" +
                        Base64.encode("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\"}") +
                        "\"}")
                .assertHeaderEquals("Content-Type", "application/json;charset=UTF-8");
    }


    @Test
    public void testRequest_post_invalid_json_byte_array() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String body = "xxxxxxxyyyyyyy";
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ForestRequest request = Forest.post("http://localhost:{}", server.getPort())
                .contentTypeJson()
                .addBody(bytes);

        byte[] fromBody = request.body().get(byte[].class);
        assertThat(fromBody).isNotNull();
        assertThat(fromBody).isEqualTo(bytes);
        
        String result = request.executeAsString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertBodyEquals("xxxxxxxyyyyyyy");
    }


    @Test
    public void testRequest_post_form_body_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        ForestRequest request = Forest.post("http://localhost:{}/post", server.getPort())
                .contentFormUrlEncoded()
                .addBody("a", 1)
                .addBody("b", 2);
        assertThat(request.body().nameValuesMapWithObject()).extracting("a", "b").contains(1, 2);
        Result<Integer> result = (Result<Integer>) request.execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/post")
                .assertHeaderEquals(ForestHeader.CONTENT_TYPE, ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
                .assertBodyEquals("a=1&b=2");
    }

    @Test
    public void testRequest_post_form_body_map() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        Result<Integer> result = Forest.post("http://localhost:{}/post", server.getPort())
                .contentFormUrlEncoded()
                .addBody(map)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/post")
                .assertHeaderEquals(ForestHeader.CONTENT_TYPE, ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
                .assertBodyEquals("a=1&b=2");
    }

    @Test
    public void testRequest_post_form_body_map2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        Result<Integer> result = Forest.post("http://localhost:{}/post", server.getPort())
                .contentFormUrlEncoded()
                .addBody(map)
                .addBody("c", 3)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/post")
                .assertHeaderEquals(ForestHeader.CONTENT_TYPE, ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
                .assertBodyEquals("a=1&b=2&c=3");
    }


    @Test
    public void testRequest_post_json_body_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.post("http://localhost:{}/post", server.getPort())
                .contentTypeJson()
                .addBody("a", 1)
                .addBody("b", 2)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/post")
                .assertHeaderEquals(ForestHeader.CONTENT_TYPE, ContentType.APPLICATION_JSON)
                .assertBodyEquals("{\"a\":1,\"b\":2}");
    }


    @Test
    public void testRequest_post_json_body_string() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.post("http://localhost:{}/post", server.getPort())
                .contentTypeJson()
                .addBody("{\"a\":1,\"b\":2}")
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/post")
                .assertHeaderEquals(ForestHeader.CONTENT_TYPE, ContentType.APPLICATION_JSON)
                .assertBodyEquals("{\"a\":1,\"b\":2}");
    }

    @Test
    public void testRequest_post_json_body_map() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        Result<Integer> result = Forest.post("http://localhost:{}/post", server.getPort())
                .contentTypeJson()
                .addBody(map)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/post")
                .assertHeaderEquals(ForestHeader.CONTENT_TYPE, ContentType.APPLICATION_JSON)
                .assertBodyEquals("{\"a\":1,\"b\":2}");
    }

    @Test
    public void testRequest_post_json_body_map2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        ForestRequest request = Forest.post("http://localhost:{}/post", server.getPort())
                .contentTypeJson()
                .addBody(map)
                .addBody("c", 3);
        assertThat(request.body().nameValuesMapWithObject())
                .extracting("a", "b", "c")
                .contains(1, 2, 3);
        Result<Integer> result = (Result<Integer>) request.execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/post")
                .assertHeaderEquals(ForestHeader.CONTENT_TYPE, ContentType.APPLICATION_JSON)
                .assertBodyEquals("{\"a\":1,\"b\":2,\"c\":3}");
    }

    @Test
    public void testRequest_post_json_body_obj() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Data data = new Data();
        data.setA(1);
        data.setB(2);
        Result<Integer> result = Forest.post("http://localhost:{}/post", server.getPort())
                .contentTypeJson()
                .addBody(data)
                .addBody("c", 3)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/post")
                .assertHeaderEquals(ForestHeader.CONTENT_TYPE, ContentType.APPLICATION_JSON)
                .assertBodyEquals("{\"a\":1,\"b\":2,\"c\":3}");
    }

    @Test
    public void testRequest_post_xml_body_text() {
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<misc>\n" +
                "    <a>1</a>\n" +
                "    <b>2</b>\n" +
                "</misc>\n";
        server.enqueue(new MockResponse().setBody(EXPECTED));
        final String result = Forest.post("http://localhost:{}/post", server.getPort())
                .addHeader("Content-Type", "application/xml")
                .addBody(xml)
                .executeAsString();
        assertThat(result).isEqualTo(EXPECTED);
        mockRequest(server)
                .assertBodyEquals(xml);
    }


    @Test
    public void testRequest_post_xml_body_gzip() {
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<misc>\n" +
                "    <a>1</a>\n" +
                "    <b>2</b>\n" +
                "</misc>\n";
        final byte[] compress = GzipUtils.compressGzip(xml);
        server.enqueue(new MockResponse().setBody(EXPECTED));
        final String result = Forest.post("http://localhost:{}/post", server.getPort())
                .addHeader("Content-Encoding", "gzip")
                .addHeader("Content-Type", "application/xml")
                .addBody(compress)
                .executeAsString();
        assertThat(result).isEqualTo(EXPECTED);
        mockRequest(server)
                .assertBodyEquals(compress);
    }


    @Test
    public void testRequest_put_form_body_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.put("http://localhost:{}/put", server.getPort())
                .contentFormUrlEncoded()
                .addBody("a", 1)
                .addBody("b", 2)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("PUT")
                .assertPathEquals("/put")
                .assertHeaderEquals(ForestHeader.CONTENT_TYPE, ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
                .assertBodyEquals("a=1&b=2");
    }

    @Test
    public void testRequest_on_body_encode() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.post("http://localhost:{}/encoded", server.getPort())
                .contentFormUrlEncoded()
                .addBody("a", 1)
                .addBody("b", 2)
                .addInterceptor(new Interceptor() {
                    @Override
                    public byte[] onBodyEncode(ForestRequest request, ForestEncoder encoder, byte[] encodedData) {
                        String str = new String(encodedData);
                        byte[] bytes = (str + "&c=3").getBytes();
                        return bytes;
                    }
                })
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/encoded")
                .assertHeaderEquals(ForestHeader.CONTENT_TYPE, ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
                .assertBodyEquals("a=1&b=2&c=3");
    }

    @Test
    public void testRequest_on_body_encode_multi() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.post("http://localhost:{}/encoded", server.getPort())
                .contentFormUrlEncoded()
                .addBody("a", 1)
                .addBody("b", 2)
                .addInterceptor(new Interceptor() {
                    @Override
                    public byte[] onBodyEncode(ForestRequest request, ForestEncoder encoder, byte[] encodedData) {
                        String str = new String(encodedData);
                        return (str + "&c=3").getBytes();
                    }
                })
                .addInterceptor(new Interceptor() {
                    @Override
                    public byte[] onBodyEncode(ForestRequest request, ForestEncoder encoder, byte[] encodedData) {
                        String str = new String(encodedData);
                        return (str + "&d=4").getBytes();
                    }
                })
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/encoded")
                .assertHeaderEquals(ForestHeader.CONTENT_TYPE, ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
                .assertBodyEquals("a=1&b=2&c=3&d=4");
    }


    @Test
    public void testRequest_delete_query_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.delete("http://localhost:{}", server.getPort())
                .addHeader(ForestHeader.USER_AGENT, "forest")
                .addQuery("a", 1)
                .addQuery("b", 2)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("DELETE")
                .assertPathEquals("/")
                .assertHeaderEquals(ForestHeader.USER_AGENT, "forest")
                .assertQueryEquals("a", "1")
                .assertQueryEquals("b", "2");
    }


    @Test
    public void testRequest_head_query_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.head("http://localhost:{}", server.getPort())
                .addHeader(ForestHeader.USER_AGENT, "forest")
                .addQuery("a", 1)
                .addQuery("b", 2)
                .execute();
        mockRequest(server)
                .assertMethodEquals("HEAD")
                .assertPathEquals("/")
                .assertHeaderEquals(ForestHeader.USER_AGENT, "forest")
                .assertQueryEquals("a", "1")
                .assertQueryEquals("b", "2");
    }

    @Test
    public void testRequest_options_query_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.options("http://localhost:{}", server.getPort())
                .addHeader(ForestHeader.USER_AGENT, "forest")
                .addQuery("a", 1)
                .addQuery("b", 2)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("OPTIONS")
                .assertPathEquals("/")
                .assertHeaderEquals(ForestHeader.USER_AGENT, "forest")
                .assertQueryEquals("a", "1")
                .assertQueryEquals("b", "2");
    }

    @Test
    public void testRequest_patch_query_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.patch("http://localhost:{}", server.getPort())
                .addHeader(ForestHeader.USER_AGENT, "httpclient")
                .addHeader(ForestHeader.USER_AGENT, "forest")
                .addQuery("a", 1)
                .addQuery("b", 2)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("PATCH")
                .assertPathEquals("/")
                .assertHeaderEquals(ForestHeader.USER_AGENT, "forest")
                .assertQueryEquals("a", "1")
                .assertQueryEquals("b", "2");
    }

    @Test
    public void testRequest_trace_query_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.trace("http://localhost:{}", server.getPort())
                .addHeader(ForestHeader.USER_AGENT, "forest")
                .addQuery("a", 1)
                .addQuery("b", 2)
                .execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("TRACE")
                .assertPathEquals("/")
                .assertHeaderEquals(ForestHeader.USER_AGENT, "forest")
                .assertQueryEquals("a", "1")
                .assertQueryEquals("b", "2");
    }

    @Test
    public void testRequest_upload_file() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String path = Objects.requireNonNull(this.getClass().getResource("/test-img.jpg")).getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        File file = new File(path);
        Result<Integer> result = Forest.post("http://localhost:{}", server.getPort())
                .contentTypeMultipartFormData()
                .addFile("file", file)
                .executeAsResponse()
                .get(new TypeReference<Result<Integer>>() {
                });
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/");
    }


    @Test
    public void testRequest_sync_retryWhen_success() {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        AtomicInteger count = new AtomicInteger(0);
        ForestRequest<?> request = Forest.get("http://localhost:{}", server.getPort())
                .maxRetryCount(3)
                .maxRetryInterval(2)
                .retryWhen(((req, res) -> res.statusIs(203)))
                .onRetry((req, res) -> count.incrementAndGet());
        request.execute();
        assertThat(request.getCurrentRetryCount()).isEqualTo(3);
        assertThat(count.get()).isEqualTo(3);
    }

    @Test
    public void testRequest_noneRetryer() {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        AtomicInteger count = new AtomicInteger(0);
        ForestResponse response = Forest.get("http://localhost:{}", server.getPort())
                .maxRetryCount(3)
                .maxRetryInterval(0)
                .retryer(NoneRetryer.class)
                .onRetry((req, res) -> count.incrementAndGet())
                .execute(ForestResponse.class);
        ForestRetryer retryer = response.getRequest().getRetryer();
        assertThat(retryer).isNotNull().isInstanceOf(NoneRetryer.class);
        assertThat(response.getRequest().getCurrentRetryCount()).isEqualTo(0);
        assertThat(count.get()).isEqualTo(0);
    }


    @Test
    public void testRequest_async_mode() {
        assertThat(Forest.get("/").asyncMode())
                .isNotNull()
                .isEqualTo(ForestAsyncMode.PLATFORM);

        Forest.config().setAsyncMode(ForestAsyncMode.KOTLIN_COROUTINE);
        assertThat(Forest.get("/").asyncMode())
                .isNotNull()
                .isEqualTo(ForestAsyncMode.KOTLIN_COROUTINE);

        Forest.config().setAsyncMode(ForestAsyncMode.PLATFORM);
        assertThat(Forest.get("/").asyncMode())
                .isNotNull()
                .isEqualTo(ForestAsyncMode.PLATFORM);
    }
    
    @Test
    public void testRequest_async_onSuccess() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<String> result = new AtomicReference<>("");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Forest.get("http://localhost:{}", server.getPort())
                .async()
                .onSuccess((data, req, res) -> {
                    result.set(data.toString());
                    countDownLatch.countDown();
                })
                .execute();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertThat(result.get()).isEqualTo(EXPECTED);
    }

    @Test
    public void testRequest_async_future() throws ExecutionException, InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Future<String> future = Forest.get("/")
                .port(server.getPort())
                .async()
                .addQuery("a", "1")
                .addQuery("a", "2")
                .addQuery("a", "3")
                .execute(new TypeReference<Future<String>>() {});
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&a=2&a=3");
        String result = future.get();
        assertThat(result).isEqualTo(EXPECTED);
    }


    @Test
    public void testRequest_async_future2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("/")
                .port(server.getPort())
                .async()
                .addQuery("a", "1")
                .executeAsFuture()
                .get(String.class);
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1");
        assertThat(result).isEqualTo(EXPECTED);
    }

    @Test
    public void testRequest_async_completable_future() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        CompletableFuture<String> future = Forest.get("/")
                .port(server.getPort())
                .async()
                .addQuery("a", "1")
                .addQuery("a", "2")
                .addQuery("a", "3")
                .execute(new TypeReference<CompletableFuture<String>>() {});
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&a=2&a=3");
        AtomicBoolean accepted = new AtomicBoolean(false);
        AtomicBoolean asyncAccepted = new AtomicBoolean(false);
        future.thenAccept(result -> {
            accepted.set(true);
            log.info("result: {}", result);
            assertThat(result).isEqualTo(EXPECTED);
        }).thenAcceptAsync(Result -> {
            asyncAccepted.set(true);
        }).join();
        assertThat(accepted.get()).isTrue();
        assertThat(asyncAccepted.get()).isTrue();
    }


    @Test
    public void testRequest_async_completable_future2() {
        AtomicBoolean accepted = new AtomicBoolean(false);
        AtomicBoolean asyncAccepted = new AtomicBoolean(false);
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("/")
                .port(server.getPort())
                .async()
                .addQuery("a", "1")
                .addQuery("a", "2")
                .addQuery("a", "3")
                .executeAsCompletableFuture(String.class)
                .thenAccept(result -> {
                    accepted.set(true);
                    log.info("result: {}", result);
                    assertThat(result).isEqualTo(EXPECTED);
                }).thenAccept(res -> {
                    asyncAccepted.set(true);
                }).join();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&a=2&a=3");
        assertThat(accepted.get()).isTrue();
        assertThat(asyncAccepted.get()).isTrue();
    }


    @Test
    public void testRequest_async_completable_future3() {
        AtomicBoolean accepted = new AtomicBoolean(false);
        AtomicBoolean asyncAccepted = new AtomicBoolean(false);
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.get("/")
                .port(server.getPort())
                .async()
                .addQuery("a", "1")
                .addQuery("a", "2")
                .addQuery("a", "3")
                .executeAsCompletableFuture(new TypeReference<ForestResponse<String>>() {})
                .thenAccept(res -> {
                    String result = res.getResult();
                    accepted.set(true);
                    log.info("result: {}", result);
                    assertThat(result).isEqualTo(EXPECTED);
                }).thenAccept(res -> {
                    asyncAccepted.set(true);
                }).join();
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1&a=2&a=3");
        assertThat(accepted.get()).isTrue();
        assertThat(asyncAccepted.get()).isTrue();
    }



    @Test
    public void testRequest_async_await() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Map map = Forest.get("/")
                .port(server.getPort())
                .async()
                .addQuery("a", "1")
                .executeAsFuture()
                .await()
                .get(Map.class);
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1");
        assertThat(map.get("status")).isEqualTo("1");
        assertThat(map.get("data")).isEqualTo("2");
    }

    @Test
    public void testRequest_async_await2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Map<String, Object> map = Forest.get("/")
                .port(server.getPort())
                .async()
                .addQuery("a", "1")
                .executeAsFuture()
                .await()
                .get(new TypeReference<Map<String, Object>>() {
                });
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1");
        assertThat(map.get("status")).isEqualTo("1");
        assertThat(map.get("data")).isEqualTo("2");
    }


    @Test
    public void testRequest_async_await3() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Map map = Forest.get("/")
                .port(server.getPort())
                .async()
                .addQuery("a", "1")
                .executeAsFuture()
                .await(1, TimeUnit.SECONDS)
                .get(Map.class);
        mockRequest(server)
                .assertPathEquals("/")
                .assertQueryEquals("a=1");
        assertThat(map.get("status")).isEqualTo("1");
        assertThat(map.get("data")).isEqualTo("2");
    }


    @Test
    public void testRequest_async_await4() {
        server.enqueue(new MockResponse().setBody(EXPECTED).setBodyDelay(1200, TimeUnit.MILLISECONDS));
        Throwable err = null;
        try {
            Forest.get("/")
                    .port(server.getPort())
                    .async()
                    .addQuery("a", "1")
                    .executeAsFuture()
                    .await(800, TimeUnit.MILLISECONDS);
        } catch (Throwable th) {
            err = th;
        }
        assertThat(err).isNotNull();
    }

    @Test
    public void testRequest_async_await_list() {
        int count = 20;
        for (int i = 0; i < count; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED));
        }
        List<ForestFuture> futures = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            futures.add(Forest.get("/")
                    .port(server.getPort())
                    .async()
                    .addQuery("a", i)
                    .executeAsFuture());
        }
        Forest.await(futures, res -> {
            String result = res.get(String.class);
            assertThat(result).isNotNull().isEqualTo(EXPECTED);
        });
    }



    @Test
    public void testRequest_async_await_list2() {
        int count = 10;
        for (int i = 0; i < count; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED));
        }
        List<ForestFuture> futures = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            futures.add(Forest.get("/")
                    .port(server.getPort())
                    .async()
                    .addQuery("a", i)
                    .executeAsFuture());
        }
        Forest.await(futures).forEach(res -> {
            String result = res.get(String.class);
            assertThat(result).isNotNull().isEqualTo(EXPECTED);
        });
    }

    @Test
    public void testRequest_async_await_array() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        server.enqueue(new MockResponse().setBody(EXPECTED));

        ForestFuture future1 = Forest.get("/")
                .port(server.getPort())
                .async()
                .addQuery("a", 0)
                .executeAsFuture();

        ForestFuture future2 = Forest.get("/")
                .port(server.getPort())
                .async()
                .addQuery("a", 1)
                .executeAsFuture();

        Forest.await(future1, future2).forEach(res -> {
            String result = res.get(String.class);
            assertThat(result).isNotNull().isEqualTo(EXPECTED);
        });
    }


    @Test
    public void testRequest_async_retryWhen_success() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        AtomicInteger atomicRetryCount = new AtomicInteger(0);
        ForestRequest<?> request = Forest.get("http://localhost")
                .port(server.getPort())
                .async()
                .maxRetryCount(3)
                .retryWhen(((req, res) -> res.statusIs(203)))
                .onRetry(((req, res) -> {
                    atomicRetryCount.incrementAndGet();
                }))
                .onSuccess(((data, req, res) -> {
                    isSuccess.set(true);
                    latch.countDown();
                }));
        request.execute();
        latch.await();
        assertThat(atomicRetryCount.get()).isEqualTo(3);
        assertThat(request.getCurrentRetryCount()).isEqualTo(3);
        assertThat(isSuccess.get()).isTrue();
    }

    @Test
    public void testRequest_sync_retryWhen_error_not_retry() {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        AtomicBoolean isError = new AtomicBoolean(false);
        ForestRequest<?> request = Forest.get("http://localhost")
                .port(server.getPort())
                .maxRetryCount(3)
                .retryWhen(((req, res) -> res.statusIs(200)))
                .onError(((ex, req, res) -> isError.set(true)));
        request.execute();
        assertThat(isError.get()).isTrue();
        assertThat(request.getCurrentRetryCount()).isEqualTo(0);
    }


    @Test
    public void testRequest_sync_error_retry() {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        AtomicBoolean isError = new AtomicBoolean(false);
        ForestRequest<?> request = Forest.get("/")
                .host("localhost")
                .port(server.getPort())
                .maxRetryCount(3)
                .onError(((ex, req, res) -> isError.set(true)));
        request.execute();
        assertThat(isError.get()).isTrue();
        assertThat(request.getCurrentRetryCount()).isEqualTo(3);
    }


    @Test
    public void testRequest_async_retryWhen_error_not_retry() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean isError = new AtomicBoolean(false);
        ForestRequest<?> request = Forest.get("http://localhost:{}", server.getPort())
                .maxRetryCount(3)
                .retryWhen(((req, res) -> res.getStatusCode() == 200))
                .setOnError(((ex, req, res) -> {
                    isError.set(true);
                    latch.countDown();
                }));
        request.execute(InputStream.class);
        latch.await();
        assertThat(isError.get()).isTrue();
        assertThat(request.getCurrentRetryCount()).isEqualTo(0);
    }


    @Test
    public void testRequest_async_error_retry() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean isError = new AtomicBoolean(false);
        ForestRequest<?> request = Forest.get("http://localhost:{}", server.getPort())
                .maxRetryCount(3)
                .maxRetryInterval(10L)
                .setOnError(((ex, req, res) -> {
                    isError.set(true);
                    latch.countDown();
                }));
        request.execute();
        latch.await();
        assertThat(isError.get()).isTrue();
        assertThat(request.getCurrentRetryCount()).isEqualTo(3);
    }

    @Test
    public void testRequest_post_add_interceptor() {
        InterceptorChain interceptorChain = Forest.post("http://localhost:{}", server.getPort() + "/post")
                .addInterceptor(TestInterceptor.class)
                .getInterceptorChain();
        assertThat(interceptorChain).isNotNull();
        assertThat(interceptorChain.getInterceptorSize()).isEqualTo(1);
        assertFalse(interceptorChain.beforeExecute(null));
        assertTrue(inter3Before.get());
    }

    static final AtomicBoolean inter3Before = new AtomicBoolean(false);

    public static class TestInterceptor implements Interceptor {

        public TestInterceptor() {
        }

        @Override
        public boolean beforeExecute(ForestRequest request) {
            inter3Before.set(true);
            return false;
        }

        @Override
        public void onSuccess(Object data, ForestRequest request, ForestResponse response) {
        }

        @Override
        public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
        }

        @Override
        public void afterExecute(ForestRequest request, ForestResponse response) {
        }
    }

    @Test
    public void testRequest_upload() {
        URL url = this.getClass().getResource("/test-img.jpg");
        byte[] byteArray = new byte[0];
        try {
            byteArray = IOUtils.toByteArray(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.put("http://localhost:{}", server.getPort() + "/")
                .contentTypeOctetStream()
                .addBody(byteArray)
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertBodyEquals(byteArray);
    }

    public Buffer getImageBuffer() {
        URL url = this.getClass().getResource("/test-img.jpg");
        byte[] byteArray = new byte[0];
        try {
            byteArray = IOUtils.toByteArray(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Buffer buffer = new Buffer();
        try {
            buffer.readFrom(new ByteArrayInputStream(byteArray));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buffer;
    }


    @Test
    public void testDownloadFile() throws IOException {
        Buffer buffer = getImageBuffer();
        server.enqueue(new MockResponse().setBody(buffer));
        AtomicReference<ForestProgress> atomicProgress = new AtomicReference<>(null);
        String dir = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "TestDownload";
        ForestRequest<?> request = Forest.get("http://localhost:{}", server.getPort())
                .setDownloadFile(dir, "")
                .setOnProgress(progress -> {
                    System.out.println("------------------------------------------");
                    System.out.println("total bytes: " + progress.getTotalBytes());
                    System.out.println("current bytes: " + progress.getCurrentBytes());
                    System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");
                    if (progress.isDone()) {
                        atomicProgress.set(progress);
                    }
                });

        ForestResponse<File> response = request.execute(new TypeReference<ForestResponse<File>>() {
        });

        Assertions.assertThat(response)
                .isNotNull()
                .extracting(ForestResponse::getStatusCode)
                .isEqualTo(200);

        File file = response.getResult();

        Assertions.assertThat(file)
                .isNotNull()
                .isFile();
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        buffer.readAll(Okio.sink(bytesOut));
        byte[] out = bytesOut.toByteArray();
        byte[] fileBytes = IOUtils.toByteArray(new FileInputStream(file));
        Assertions.assertThat(fileBytes)
                .hasSize(out.length)
                .isEqualTo(out);
        Assertions.assertThat(atomicProgress.get())
                .isNotNull()
                .extracting(
                        ForestProgress::isDone,
                        ForestProgress::getRate,
                        ForestProgress::getRequest)
                .contains(true, 1D, response.getRequest());
    }

    @Test
    public void testProxyFromHTTPUrl() {
        final ForestRequest req = Forest.get("https://www.google.com")
                .proxy("http://root:123456@localhost:1082")
                .connectTimeout(20000);
        final ForestProxy proxy = req.getProxy();
        assertThat(proxy).isNotNull();
        assertThat(proxy.getType()).isEqualTo(ForestProxyType.HTTP);
        assertThat(proxy.getHost()).isEqualTo("localhost");
        assertThat(proxy.getPort()).isEqualTo(1082);
        assertThat(proxy.getUsername()).isEqualTo("root");
        assertThat(proxy.getPassword()).isEqualTo("123456");
    }



    @Test
    public void testIfThen() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(EXPECTED));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(EXPECTED));

        int a = 1;
        Forest.get("/test")
                .port(server.getPort())
                .host("localhost")
                .addHeader("A", 0)
                .ifThen(a > 0, q -> q.addHeader("A", a + 1))
                .endIf()
                .execute();

        mockRequest(server)
                .assertHeaderEquals("A", "2");


        int b = 0;
        Forest.get("/test")
                .port(server.getPort())
                .host("localhost")
                .addHeader("B", -1)
                .ifThen(b > 0, q -> q.addHeader("B", b + 1))
                .endIf()
                .execute();

        mockRequest(server)
                .assertHeaderEquals("B", "-1");
    }


    @Test
    public void testIfThenElse() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(EXPECTED));

        int a = -1;
        Forest.get("/test")
                .port(server.getPort())
                .host("localhost")
                .addHeader("A", 0)
                .ifThen(a > 0, q -> q.addHeader("A", a + 1))
                .elseThen(q -> q.addHeader("A", 10))
                .execute();

        mockRequest(server)
                .assertHeaderEquals("A", "10");
    }


    @Test
    public void testElseIfThen() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(EXPECTED));
        int a = -1;
        int b = 110;
        int c = 220;
        Forest.get("http://localhost:{}/test", server.getPort())
                .addHeader("A", 0)
                .cond(b > 100, q -> q.addHeader("B", 100))
                .cond(c > 200, q -> q.addHeader("C", 200))
                .ifThen(a > 0, q -> q.addHeader("A", a + 1))
                .elseIfThen(a == 0, q -> q.addHeader("A", 0))
                .elseIfThen(a == -1, q -> q.addHeader("A", -1))
                .elseIfThen(a == -2, q -> q.addHeader("A", -2))
                .elseThen(q -> q.addHeader("A", 10))
                .execute();

        mockRequest(server)
                .assertHeaderEquals("A", "-1")
                .assertHeaderEquals("B", "100")
                .assertHeaderEquals("C", "200");
    }

    @Test
    public void testIfThenLambda() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(EXPECTED));
        int a = -1;
        Forest.get("/test")
                .addAttachment("a", -2)
                .port(server.getPort())
                .host("localhost")
                .addHeader("A", 0)
                .ifThen(a < 0, q -> q.addHeader("A", q.getAttachment("a", Integer.class)))
                .elseIfThen(a == -1, q -> q.addHeader("A", a))
                .endIf()
                .execute();

        mockRequest(server)
                .assertHeaderEquals("A", "-2");
    }

    @Test
    public void testIfThenLambdaCondition() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(EXPECTED));
        int a = -1;
        Forest.get("/test")
                .addAttachment("a", a)
                .port(server.getPort())
                .host("localhost")
                .addHeader("A", 0)
                .ifThen(q -> q.getAttachment("a", Integer.class) < 0,
                        q -> q.addHeader("A", a))
                .endIf()
                .execute();

        mockRequest(server)
                .assertHeaderEquals("A", "-1");
    }


    @Test
    public void testSSE() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(
                "data:start\n" +
                "data:hello\n" +
                "event:ignore\n" +
                "event:close\n" +
                "data:dont show"
        ));
        StringBuffer buffer = new StringBuffer();

        Forest.get("http://localhost:{}/sse", server.getPort())
                .sse()
                .setOnOpen(eventSource -> {
                    buffer.append("SSE Open\n");
                })
                .setOnClose(eventSource -> {
                    buffer.append("SSE Close");
                })
                .addOnData((eventSource, name, value) -> {
                    buffer.append("Receive data: ").append(value).append("\n");
                })
                .addOnEventMatchesPrefix("close", (eventSource, name, value) -> {
                    buffer.append("Receive event: ").append(value).append("\n");
                    eventSource.close();
                })
                .listen();
        System.out.println(buffer);

        assertThat(buffer.toString()).isEqualTo(
                "SSE Open\n" +
                "Receive data: start\n" +
                "Receive data: hello\n" +
                "Receive event: close\n" +
                "SSE Close"
        );
    }
    

    @Test
    public void testAsyncSSE() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(
                "data:start\n" +
                "data:hello\n" +
                "event:ignore\n" +
                "event:close\n" +
                "data:dont show"
        ));
        StringBuffer buffer = new StringBuffer();

        ForestSSE sse = Forest.get("http://localhost:{}/sse", server.getPort())
                .contentType("text/event-stream")
                .sse()
                .setOnOpen(eventSource -> {
                    buffer.append("SSE Open\n");
                })
                .setOnClose(eventSource -> {
                    buffer.append("SSE Close");
                })
                .addOnData((eventSource, name, value) -> {
                    buffer.append("Receive data: ").append(value).append("\n");
                })
                .addOnEventMatchesPrefix("close", (eventSource, name, value) -> {
                    buffer.append("Receive event: ").append(value).append("\n");
                    eventSource.close();
                })
                .asyncListen();

        sse.await();
        System.out.println(buffer);
        assertThat(buffer.toString()).isEqualTo(
                "SSE Open\n" +
                "Receive data: start\n" +
                "Receive data: hello\n" +
                "Receive event: close\n" +
                "SSE Close"
        );
    }



    @Test
    public void testSSE_withCustomClass() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(
                "data:start\n" +
                "data:hello\n" +
                "event:close\n" +
                "data:dont show"
        ));

        MySSEHandler sse = Forest.get("http://localhost:{}/sse", server.getPort())
                .sse(MySSEHandler.class)
                .listen();
        System.out.println(sse.getStringBuffer());
        assertThat(sse.getStringBuffer().toString()).isEqualTo(
                "SSE Open\n" +
                "Receive data: start\n" +
                "Receive data: hello\n" +
                "receive close --- close\n" +
                "SSE Close"
        );
    }

    @Test
    public void testAsyncSSE_withCustomClass() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(
                "data:start\n" +
                "data:hello\n" +
                "event:close\n" +
                "data:dont show"
        ));

        MySSEHandler sse = Forest.get("http://localhost:{}/sse", server.getPort())
                .sse(MySSEHandler.class)
                .asyncListen();

        sse.await();

        System.out.println(sse.getStringBuffer());
        
        assertThat(sse.getStringBuffer().toString()).isEqualTo(
            "SSE Open\n" +
            "Receive data: start\n" +
            "Receive data: hello\n" +
            "receive close --- close\n" +
            "SSE Close"
        );
    }


    @Test
    public void testSSE_withOnMultilinesMessage() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(
                "\n\n\n" +
                "id:1\n" +
                "event:json\n" +
                "data:{\"name\": \"a\", \"age\": 10}\n" +
                "\n\n" +
                "id:2\n" +
                "event:json\n" +
                "data:{\"name\": \"b\", \"age\": 12}\n" +
                "\n" +
                "id:3\n" +
                "event:json\n" +
                "data:{\"name\": \"c\", \"age\": 9}\n"
        ));

        final StringBuffer buffer = new StringBuffer();

        Forest.get("http://localhost:{}/sse", server.getPort())
                .sse()
                .setOnMessage(event -> {
                    buffer.append("--------------\n");
                    buffer.append("id => ").append(event.id(int.class) * 100).append("\n");
                    buffer.append("event => ").append(event.event()).append("\n");
                    TestUser user = event.data(TestUser.class);
                    buffer.append("name => ").append(user.getName()).append("\n");
                    buffer.append("age => ").append(user.getAge()).append("\n");
                })
                .listen(SSELinesMode.MULTI_LINES);

        System.out.println(buffer);

        assertThat(buffer.toString()).isEqualTo(
                "--------------\n" +
                "id => 100\n" +
                "event => json\n" +
                "name => a\n" +
                "age => 10\n" +
                "--------------\n" +
                "id => 200\n" +
                "event => json\n" +
                "name => b\n" +
                "age => 12\n" +
                "--------------\n" +
                "id => 300\n" +
                "event => json\n" +
                "name => c\n" +
                "age => 9\n");
    }


    @Test
    public void testSSE_withOnSingleLineJSONMessage() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(
                "\n\n" +
                "{\"name\": \"a\"}\n" +
                "{\"name\": \"b\"}\n" +
                "{\"name\": \"c\"}\n"
        ));
        
        final StringBuffer buffer = new StringBuffer();

        Forest.get("http://localhost:{}/sse", server.getPort())
                .sse()
                .setOnMessage(event -> {
                    buffer.append("--------------\n");
                    Map map = event.value(Map.class);
                    buffer.append("map => ").append(map.toString()).append("\n");
                })
                .listen(SSELinesMode.SINGLE_LINE);
        
        System.out.println(buffer);
        
        assertThat(buffer.toString()).isEqualTo(
                "--------------\n" +
                "map => {name=a}\n" +
                "--------------\n" +
                "map => {name=b}\n" +
                "--------------\n" +
                "map => {name=c}\n");
    }

    @Test
    public void testSSE_withAutoLines() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(
                "id:1\n" +
                "event:json\n" +
                "data:{\"name\": \"a\", \"age\": 10}\n" +
                "\n" +
                "id:2\n" +
                "event:json\n" +
                "data:{\"name\": \"b\", \"age\": 12}\n" +
                "\n" +
                "id:3\n" +
                "event:json\n" +
                "data:{\"name\": \"c\", \"age\": 9}\n"
        ));

        final StringBuffer buffer = new StringBuffer();

        Forest.get("http://localhost:{}/sse", server.getPort())
                .sse()
                .setOnMessage(event -> {
                    buffer.append("--------------\n");
                    buffer.append("id => ").append(event.id()).append("\n");
                    buffer.append("event => ").append(event.event()).append("\n");
                    TestUser user = event.value(TestUser.class);
                    buffer.append("name => ").append(user.getName()).append("\n");
                    buffer.append("age => ").append(user.getAge()).append("\n");
                })
                .listen();

        System.out.println(buffer);

        assertThat(buffer.toString()).isEqualTo(
                "--------------\n" +
                "id => 1\n" +
                "event => json\n" +
                "name => a\n" +
                "age => 10\n" +
                "--------------\n" +
                "id => 2\n" +
                "event => json\n" +
                "name => b\n" +
                "age => 12\n" +
                "--------------\n" +
                "id => 3\n" +
                "event => json\n" +
                "name => c\n" +
                "age => 9\n");
    }

    @Test
    public void testSSE_withAutoLineJSONMessage() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(
                "\n\n" +
                        "{\"name\": \"a\"}\n" +
                        "{\"name\": \"b\"}\n" +
                        "{\"name\": \"c\"}\n"
        ));

        final StringBuffer buffer = new StringBuffer();

        Forest.get("http://localhost:{}/sse", server.getPort())
                .sse()
                .setOnMessage(event -> {
                    buffer.append("--------------\n");
                    Map map = event.value(Map.class);
                    buffer.append("map => ").append(map.toString()).append("\n");
                })
                .listen();

        System.out.println(buffer);

        assertThat(buffer.toString()).isEqualTo(
                "--------------\n" +
                        "map => {name=a}\n" +
                        "--------------\n" +
                        "map => {name=b}\n" +
                        "--------------\n" +
                        "map => {name=c}\n");
    }



    @Test
    public void testJsonpath_single_data() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(EXPECTED_SINGLE_USER));
        TestUser user = Forest.get("http://localhost:{}", server.getPort())
                .executeAsResponse()
                .getByPath("$.data", TestUser.class);
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("Foo");
        assertThat(user.getAge()).isEqualTo(12);
    }


    @Test
    public void testJsonpath_single_data_as_list() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(EXPECTED_SINGLE_USER));
        List<TestUser> userList = Forest.get("http://localhost:{}", server.getPort())
                .executeAsResponse()
                .getByPath("$..data", new TypeReference<List<TestUser>>() {});
        assertThat(userList).isNotNull();
        assertThat(userList.get(0).getName()).isEqualTo("Foo");
        assertThat(userList.get(0).getAge()).isEqualTo(12);
    }


    @Test
    public void testJsonpath_list_data() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(EXPECTED_LIST_USER));
        List<TestUser> userList = Forest.get("http://localhost:{}", server.getPort())
                .executeAsResponse()
                .getByPath("$.data", new TypeReference<List<TestUser>>() {});
        assertThat(userList).isNotNull();
        assertThat(userList.get(0).getName()).isEqualTo("Foo");
        assertThat(userList.get(0).getAge()).isEqualTo(12);
    }


    @Test
    public void testJsonpath_list_data_with_condition() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(EXPECTED_LIST_USER));
        List<TestUser> userList = Forest.get("http://localhost:{}", server.getPort())
                .executeAsResponse()
                .getByPath("$.data[?(@.age>20)]", new TypeReference<List<TestUser>>() {});
        assertThat(userList).isNotNull();
        assertThat(userList.get(0).getName()).isEqualTo("Bar");
        assertThat(userList.get(0).getAge()).isEqualTo(22);
    }

    @Test
    public void testJsonpath_list_data_with_condition_and_variable() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(EXPECTED_LIST_USER));
        Forest.config().setVariable("maxAge", 20);
        List<TestUser> userList = Forest.get("http://localhost:{}", server.getPort())
                .executeAsResponse()
                .getByPath("$.data[?(@.age>{maxAge})]", new TypeReference<List<TestUser>>() {});
        assertThat(userList).isNotNull();
        assertThat(userList.get(0).getName()).isEqualTo("Bar");
        assertThat(userList.get(0).getAge()).isEqualTo(22);
    }


    @Test
    public void testJsonpath_list_user_ages_with_condition_and_variable() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(EXPECTED_LIST_USER));
        Forest.config().setVariable("minAge", 20);
        List<Integer> ageList = Forest.get("http://localhost:{}", server.getPort())
                .executeAsResponse()
                .getByPath("$.data[?(@.age>{minAge})].age", new TypeReference<List<Integer>>() {});
        assertThat(ageList).isNotNull();
        assertThat(ageList.get(0)).isEqualTo(22);
    }


    @Test
    public void testLoadBalance() {
        int count = 100;
        for (int i = 0; i < count; i++) {
            server.enqueue(new MockResponse().setResponseCode(200).setBody(EXPECTED_LIST_USER));
        }

        Forest.config("test_lb").setVariable("host", Arrays.asList("localhost", "127.0.0.1"));

        for (int i = 0; i < count; i++) {
            Forest.config("test_lb").get("http://{host}:{}/abc", server.getPort())
                    .execute();
        }

    }
    
    public static class T4 {
        public void printA(Thread thread) {
            try {
                Thread.sleep(3000L);
                System.out.println("A");
                LockSupport.unpark(thread);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public void printB(Thread thread) {
            try {
                Thread.sleep(10L);
                LockSupport.park();
                System.out.println("B");
                LockSupport.unpark(thread);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public void printC() {
            try {
                Thread.sleep(5L);
                LockSupport.park();
                System.out.println("C");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    



/*
    @Test
    public void testSocksHttpProxy() {
        final String ret = Forest.get("http://www.google.com")
                .proxy(ForestProxy.socks("localhost", 1089))
                .connectTimeout(20000)
                .executeAsString();
        System.out.println(ret);
    }

    @Test
    public void testSocksHttpsProxy() {
        final String ret = Forest.get("https://www.google.com")
                .proxy(ForestProxy.socks("localhost", 1089)
                        .username("dtflys")
                        .password("123456"))
                .connectTimeout(20000)
                .executeAsString();
        System.out.println(ret);
    }
*/


}
