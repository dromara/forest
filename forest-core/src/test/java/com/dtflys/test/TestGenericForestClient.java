package com.dtflys.test;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.dtflys.forest.Forest;
import com.dtflys.forest.auth.BasicAuth;
import com.dtflys.forest.auth.ForestAuthenticator;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestAddress;
import com.dtflys.forest.http.ForestAsyncMode;
import com.dtflys.forest.http.ForestFuture;
import com.dtflys.forest.http.ForestHeader;
import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestURL;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.interceptor.InterceptorChain;
import com.dtflys.forest.logging.LogConfiguration;
import com.dtflys.forest.retryer.ForestRetryer;
import com.dtflys.forest.retryer.NoneRetryer;
import com.dtflys.forest.ssl.SSLSocketFactoryBuilder;
import com.dtflys.forest.ssl.TrustAllManager;
import com.dtflys.forest.utils.ForestProgress;
import com.dtflys.forest.utils.TypeReference;
import com.dtflys.test.http.BaseClientTest;
import com.dtflys.test.model.Result;
import com.google.common.collect.Lists;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import okio.Okio;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.bytebuddy.asm.Advice;
import org.junit.Rule;
import org.junit.Test;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestGenericForestClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\":\"1\", \"data\":\"2\"}";

    @Rule
    public final MockWebServer server = new MockWebServer();

    public TestGenericForestClient(HttpBackend backend) {
        super(backend, Forest.config());
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
    public void testAuth_UsernamePassword() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest request =
                Forest.get("http://foo:bar@localhost:" + server.getPort() + "/xxx");
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
    public void testAsyncPool() {
        Forest.config()
                .setMaxAsyncThreadSize(100)
                .setMaxAsyncQueueSize(100);
        LogConfiguration logConfiguration = new LogConfiguration();
        logConfiguration.setLogEnabled(false);
        for (int j = 0; j < 100; j++) {
            final int total = 10;
            for (int i = 0; i < total; i++) {
                server.enqueue(new MockResponse().setBody(EXPECTED));
            }
            final CountDownLatch latch = new CountDownLatch(total);
            final AtomicInteger count = new AtomicInteger(0);
            final AtomicInteger errorCount = new AtomicInteger(0);
            for (int i = 0; i < total; i++) {
                Forest.get("/")
                        .host("localhost")
                        .port(server.getPort())
                        .async()
                        .setLogConfiguration(logConfiguration)
                        .onSuccess((data, req, res) -> {
                            latch.countDown();
                            int c = count.incrementAndGet();
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
            assertThat(errorCount.get()).isEqualTo(0);
//            System.out.println("第一阶段: 全部已完成");

            for (int i = 0; i < total; i++) {
                server.enqueue(new MockResponse().setHeader("Status", "Ok"));
            }
            final CountDownLatch latch2 = new CountDownLatch(total);
            final AtomicInteger count2 = new AtomicInteger(0);
            final AtomicInteger errorCount2 = new AtomicInteger(0);
            for (int i = 0; i < total; i++) {
                Forest.head("/")
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
        private Integer a;
        private Integer b;
        private Integer c;
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
        String result = Forest.get("http://localhost:" + server.getPort() + "/abc/A")
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
        Forest.config().setVariableValue("testVar", "foo");
        Forest.get("/test/{testVar}")
                .host("127.0.0.1")
                .port(server.getPort())
                .execute();
        mockRequest(server)
                .assertPathEquals("/test/foo");
    }

    @Test
    public void testRequest_get_return_string() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("http://localhost:" + server.getPort()).execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testRequest_get_return_string2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("http://localhost:" + server.getPort()).executeAsString();
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
        List<Integer> result = Forest.get("http://localhost:" + server.getPort()).executeAsList();
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(Lists.newArrayList(1, 2, 3));
    }

    @Test
    public void testRequest_get_return_map_list() {
        server.enqueue(new MockResponse().setBody("[{\"a\": 1}, {\"b\": 2}, {\"c\": 3}]"));
        List<Map<String, Object>> result = Forest.get("http://localhost:" + server.getPort()).executeAsList();
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).get("a")).isEqualTo(1);
        assertThat(result.get(1).get("b")).isEqualTo(2);
        assertThat(result.get(2).get("c")).isEqualTo(3);
    }

    @Test
    public void testRequest_get_return_response() {
        server.enqueue(new MockResponse().setBody("{\"a\": 1, \"b\": 2, \"c\": 3}"));
        ForestResponse<Map<String, Object>> response = Forest.get("http://localhost:" + server.getPort())
                .execute(new TypeReference<ForestResponse<Map<String, Object>>>() {
                });
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
        Map<String, Integer> result = Forest.get("http://localhost:" + server.getPort()).execute(type);
        assertThat(result).isNotNull();
        assertThat(result.get("status")).isEqualTo(1);
        assertThat(result.get("data")).isEqualTo(2);
    }


    @Test
    public void testRequest_get_return_JavaObject() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Result result = Forest.get("http://localhost:" + server.getPort()).execute(Result.class);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo("2");
    }

    @Test
    public void testRequest_get_return_JavaObject_with_genericType() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.get("http://localhost:" + server.getPort()).execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
    }

    @Test
    public void testRequest_get_query_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.get("http://localhost:" + server.getPort())
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
        Result<Integer> result = Forest.get("http://localhost:" + server.getPort())
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
        Result<Integer> result = Forest.get("http://localhost:" + server.getPort())
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
        String result = Forest.post("http://localhost:" + server.getPort())
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
        String result = Forest.post("http://localhost:" + server.getPort())
                .addBody(map)
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertBodyEquals("name=foo&value=bar");
    }


    @Test
    public void testRequest_post_invalid_json() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.post("http://localhost:" + server.getPort())
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addBody("xxxxxxxyyyyyyy")
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertBodyEquals("xxxxxxxyyyyyyy");
    }

    @Test
    public void testRequest_content_type_with_charset() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        final String s = Forest
                .post("http://localhost:" + server.getPort() + "/test")
                .contentTypeJson()
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("name", "Forest.backend = okhttp3")
                .addBody("{\"id\":\"1972664191\", \"name\":\"XieYu20011008\"}")
                .executeAsString();
        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\", \"name\":\"XieYu20011008\"}")
                .assertHeaderEquals("Content-Type", "application/json; charset=UTF-8");
    }


    @Test
    public void testRequest_post_invalid_json_byte_array() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String body = "xxxxxxxyyyyyyy";
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        String result = Forest.post("http://localhost:" + server.getPort())
                .contentTypeJson()
                .addBody(bytes)
                .execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertBodyEquals("xxxxxxxyyyyyyy");
    }


    @Test
    public void testRequest_post_form_body_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        ForestRequest request = Forest.post("http://localhost:" + server.getPort() + "/post")
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
        Result<Integer> result = Forest.post("http://localhost:" + server.getPort() + "/post")
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
        Result<Integer> result = Forest.post("http://localhost:" + server.getPort() + "/post")
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
        Result<Integer> result = Forest.post("http://localhost:" + server.getPort() + "/post")
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
        Result<Integer> result = Forest.post("http://localhost:" + server.getPort() + "/post")
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
        Result<Integer> result = Forest.post("http://localhost:" + server.getPort() + "/post")
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
        ForestRequest request = Forest.post("http://localhost:" + server.getPort() + "/post")
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
        Result<Integer> result = Forest.post("http://localhost:" + server.getPort() + "/post")
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
    public void testRequest_put_form_body_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.put("http://localhost:" + server.getPort() + "/put")
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
        Result<Integer> result = Forest.post("http://localhost:" + server.getPort() + "/encoded")
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
        Result<Integer> result = Forest.post("http://localhost:" + server.getPort() + "/encoded")
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
        Result<Integer> result = Forest.delete("http://localhost:" + server.getPort())
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
        Forest.head("http://localhost:" + server.getPort())
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
        Result<Integer> result = Forest.options("http://localhost:" + server.getPort())
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
        Result<Integer> result = Forest.patch("http://localhost:" + server.getPort())
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
        Result<Integer> result = Forest.trace("http://localhost:" + server.getPort())
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
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        String path = Objects.requireNonNull(this.getClass().getResource("/test-img.jpg")).getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        File file = new File(path);
        Result<Integer> result = Forest.post("http://localhost:" + server.getPort())
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
        ForestRequest<?> request = Forest.get("http://localhost:" + server.getPort())
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
        ForestResponse response = Forest.get("http://localhost:" + server.getPort())
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
    public void testRequest_async_future() throws ExecutionException, InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Future<String> future = Forest.get("/")
                .port(server.getPort())
                .async()
                .addQuery("a", "1")
                .addQuery("a", "2")
                .addQuery("a", "3")
                .execute(new TypeReference<Future<String>>() {
                });
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
        int count = 200;
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
        ForestRequest<?> request = Forest.get("http://localhost:" + server.getPort())
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
        ForestRequest<?> request = Forest.get("http://localhost:" + server.getPort())
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
        InterceptorChain interceptorChain = Forest.post("http://localhost:" + server.getPort() + "/post")
                .addInterceptor(TestInterceptor.class)
                .getInterceptorChain();
        assertThat(interceptorChain).isNotNull();
        assertThat(interceptorChain.getInterceptorSize()).isEqualTo(2);
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
        String result = Forest.put("http://localhost:" + server.getPort() + "/")
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
        ForestRequest<?> request = Forest.get("http://localhost:" + server.getPort())
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

}
