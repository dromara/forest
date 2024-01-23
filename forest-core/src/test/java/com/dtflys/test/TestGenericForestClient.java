package com.dtflys.test;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.StopWatch;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.dtflys.forest.Forest;
import com.dtflys.forest.auth.BasicAuth;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.ConvertOptions;
import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestAddress;
import com.dtflys.forest.http.ForestAsyncMode;
import com.dtflys.forest.http.ForestFuture;
import com.dtflys.forest.http.ForestHeader;
import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestProxyType;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestURL;
import com.dtflys.forest.http.Lazy;
import com.dtflys.forest.interceptor.ForestJointPoint;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.interceptor.InterceptorChain;
import com.dtflys.forest.logging.LogConfiguration;
import com.dtflys.forest.retryer.ForestRetryer;
import com.dtflys.forest.retryer.NoneRetryer;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.ForestProgress;
import com.dtflys.forest.utils.GzipUtils;
import com.dtflys.forest.utils.TypeReference;
import com.dtflys.forest.utils.URLUtils;
import com.dtflys.test.http.BaseClientTest;
import com.dtflys.test.http.model.UserParam;
import com.dtflys.test.model.Result;
import com.google.common.collect.Lists;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import okio.Okio;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;

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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
    public void testRequest_query_repeat() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestResponse<?> res = Forest.get("/")
                .port(server.getPort())
                .addQuery("a", "1")
                .addQuery("a", "2")
                .addQuery("a", "3")
                .execute();
        assertThat(res.result()).isNotNull();
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
                .as(String.class);
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
                Forest.get("/")
                        .host("localhost")
                        .port(server.getPort())
                        .addAttachment("num", i + "-" + j)
                        .connectTimeout(200)
                        .readTimeout(200)
                        .async()
                        .setLogConfiguration(logConfiguration)
                        .onSuccess((req, res) -> {
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
                        .onSuccess((req, res) -> {
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
                .as(String.class);
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
        String result = request.asString();
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
                .as(String.class);
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
                .as(String.class);
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
                .as(String.class);
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
                .as(String.class);
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
                .as(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/X/B");
    }

    @Test
    public void testRequest_change_base_path4() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("http://localhost:" + server.getPort() + "/abc/A")
                .basePath("X")
                .as(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/X/abc/A");
    }

    @Test
    public void testRequest_change_base_path5() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest request = Forest.get("/A")
                .basePath("http://localhost:" + server.getPort() + "/X1/X2");
        String result = request.asString();
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
        String result = request.asString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/B");
    }

    @Test
    public void testRequest_template_in_url() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.config().setVar("testVar", "foo");
        Forest.get("/")
                .setUrl("/test/{testVar}")
                .host("127.0.0.1")
                .port(server.getPort())
                .execute();
        mockRequest(server)
                .assertPathEquals("/test/foo");
    }

    @Test
    public void testRequest_get_return_string() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("http://localhost:" + server.getPort()).as(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testRequest_get_return_string2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("http://localhost:" + server.getPort()).asString();
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
                .asMap();
        assertThat(result).isNotNull();
        assertThat(result.get("status")).isEqualTo("1");
        assertThat(result.get("data")).isEqualTo("2");
    }

    @Test
    public void testRequest_get_return_map2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Map<String, String> result = Forest.get("/")
                .address("localhost", server.getPort())
                .as(new TypeReference<Map<String, String>>() {}.getType());
        assertThat(result).isNotNull();
        assertThat(result.get("status")).isEqualTo("1");
        assertThat(result.get("data")).isEqualTo("2");
    }


    @Test
    public void testRequest_get_return_list() {
        server.enqueue(new MockResponse().setBody("[1, 2, 3]"));
        List<Integer> result = Forest.get("http://localhost:" + server.getPort()).asList();
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(Lists.newArrayList(1, 2, 3));
    }

    @Test
    public void testRequest_get_return_map_list() {
        server.enqueue(new MockResponse().setBody("[{\"a\": 1}, {\"b\": 2}, {\"c\": 3}]"));
        List<Map<String, Object>> result = Forest.get("http://localhost:" + server.getPort()).asList();
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
                .as(new TypeReference<ForestResponse<Map<String, Object>>>() {
                });
        assertThat(response).isNotNull();
        Map<String, Object> result = response.result();
        assertThat(result).isNotNull();
        assertThat(result.get("a")).isEqualTo(1);
        assertThat(result.get("b")).isEqualTo(2);
        assertThat(result.get("c")).isEqualTo(3);
        assertThat(response.isClosed()).isTrue();
        assertThat(response.result()).isNotNull();
    }


    @Test
    public void testRequest_get_return_list2() {
        server.enqueue(new MockResponse().setBody("[\"1\", \"2\", \"3\"]"));
        List<String> result = Forest.get("/")
                .address("localhost", server.getPort())
                .asList();
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(Lists.newArrayList("1", "2", "3"));
    }


    @Test
    public void testRequest_get_return_type() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Type type = new TypeReference<Map<String, Integer>>() {}.getType();
        Map<String, Integer> result = Forest.get("http://localhost:" + server.getPort()).as(type);
        assertThat(result).isNotNull();
        assertThat(result.get("status")).isEqualTo(1);
        assertThat(result.get("data")).isEqualTo(2);
    }


    @Test
    public void testRequest_get_return_JavaObject() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Result result = Forest.get("http://localhost:" + server.getPort()).as(Result.class);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo("2");
    }

    @Test
    public void testRequest_get_return_JavaObject_with_genericType() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {
        };
        Result<Integer> result = Forest.get("http://localhost:" + server.getPort()).as(typeReference);
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
                .as(typeReference);
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
                .as(typeReference);
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
                .as(typeReference);
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
                .as(String.class);
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
                .as(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertBodyEquals("value=bar&name=foo");
    }


    @Test
    public void testRequest_post_invalid_json() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.post("http://localhost:" + server.getPort())
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addBody("xxxxxxxyyyyyyy")
                .as(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertBodyEquals("xxxxxxxyyyyyyy");
    }

    @Test
    public void testRequest_content_type_with_charset() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:" + server.getPort() + "/test")
                .contentTypeJson()
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("name", "Forest.backend = okhttp3")
                .addBody("{\"id\":\"1972664191\", \"name\":\"XieYu20011008\"}")
                .asString();
        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\", \"name\":\"XieYu20011008\"}")
                .assertHeaderEquals("Content-Type", "application/json; charset=UTF-8");
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

        BeanUtil.beanToMap(data);

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

        Forest.get("/")
                .port(server.getPort())
                .addQuery(data)
                .execute();

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
        System.out.println(request.body()
                .encodeToString(
                        ConvertOptions.defaultOptions()
                                .nullValuePolicy(ConvertOptions.NullValuePolicy.WRITE_EMPTY_STRING)));
    }


    @Test
    public void testRequest_lazy_header() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:" + server.getPort() + "/test")
                .contentTypeJson()
                .addHeader("Content-Type", req -> "application/json; charset=UTF-8")
                .addHeader("name", req -> "Forest.backend = " + req.getBackend().getName())
                .addBody("{\"id\":\"1972664191\", \"name\":\"XieYu20011008\"}")
                .execute();
        mockRequest(server)
                .assertHeaderEquals("name", "Forest.backend = " + Forest.config().getBackend().getName())
                .assertBodyEquals("{\"id\":\"1972664191\", \"name\":\"XieYu20011008\"}")
                .assertHeaderEquals("Content-Type", "application/json; charset=UTF-8");
    }

    @Test
    public void testRequest_lazy_header2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:" + server.getPort() + "/test")
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("Authorization", req -> Base64.encode("Token=" + req.body().encodeToString()))
                .addBody("id", "1972664191")
                .addBody("name", "XieYu20011008")
                .execute();
        mockRequest(server)
                .assertHeaderEquals("Authorization",
                        Base64.encode("Token={\"id\":\"1972664191\",\"name\":\"XieYu20011008\"}"))
                .assertBodyEquals("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\"}")
                .assertHeaderEquals("Content-Type", "application/json; charset=UTF-8");
    }

    @Test
    public void testRequest_lazy_header3() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:" + server.getPort() + "/test")
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
        Forest.post("http://localhost:" + server.getPort() + "/test")
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("_id", "20011008")
                .addBody("id", "1972664191")
                .addBody("name", req -> "XieYu" + req.headerValue("_id"))
                .execute();
        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\"}")
                .assertHeaderEquals("Content-Type", "application/json; charset=UTF-8");
    }


    @Test
    public void testRequest_lazy_body2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:" + server.getPort() + "/test")
                .addHeader("Content-Type", "application/json; charset=UTF-8")
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
                .assertHeaderEquals("Content-Type", "application/json; charset=UTF-8");
    }

    @Test
    public void testRequest_lazy_body3() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:" + server.getPort() + "/test")
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("_id", "20011008")
                .addBody("id", "1972664191")
                .addBody("name", req -> "XieYu" + req.headerValue("_id"))
                .addBody("token", req -> Base64.encode(req.body().encode()))
                .execute();
        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\",\"token\":\"" +
                        Base64.encode("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\"}") +
                        "\"}")
                .assertHeaderEquals("Content-Type", "application/json; charset=UTF-8");
    }

    @Test
    public void testRequest_lazy_body4() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Forest.post("http://localhost:" + server.getPort() + "/test")
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("_id", "20011008")
                .addBody("id", "1972664191")
                .addBody("name", req -> "XieYu" + req.headerValue("_id"))
                .addBody("token", req -> Base64.encode(req.body().encode(ForestDataType.FORM)))
                .execute();
        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\",\"token\":\"" +
                        Base64.encode("id=1972664191&name=XieYu20011008") +
                        "\"}")
                .assertHeaderEquals("Content-Type", "application/json; charset=UTF-8");
    }


    @Test
    public void testRequest_lazy_body5() {
        server.enqueue(new MockResponse().setBody(EXPECTED));

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", "1972664191");
        data.put("name", "XieYu20011008");

        Forest.post("http://localhost:" + server.getPort() + "/test")
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("_id", "20011008")
                .addBody(data)
                .addBody("token", req -> Base64.encode(req.body().encode()))
                .execute();

        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\",\"token\":\"" +
                        Base64.encode("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\"}") +
                        "\"}")
                .assertHeaderEquals("Content-Type", "application/json; charset=UTF-8");
    }


    @Test
    public void testRequest_lazy_body6() {
        server.enqueue(new MockResponse().setBody(EXPECTED));

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", "1972664191");
        data.put("name", "XieYu20011008");
        data.put("token", (Lazy<Object>) (req -> Base64.encode(req.body().encode())));

        Forest.post("http://localhost:" + server.getPort() + "/test")
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("_id", "20011008")
                .addBody(data)
                .execute();

        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\",\"token\":\"" +
                        Base64.encode("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\"}") +
                        "\"}")
                .assertHeaderEquals("Content-Type", "application/json; charset=UTF-8");
    }

    public static class LazyData {
        private String id;
        private String name;
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

        Forest.post("http://localhost:" + server.getPort() + "/test")
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("_id", "20011008")
                .addBody(data)
                .execute();

        mockRequest(server)
                .assertBodyEquals("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\",\"token\":\"" +
                        Base64.encode("{\"id\":\"1972664191\",\"name\":\"XieYu20011008\"}") +
                        "\"}")
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
                .as(String.class);
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
        Result<Integer> result = (Result<Integer>) request.as(typeReference);
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
                .as(typeReference);
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
                .as(typeReference);
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
                .as(typeReference);
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
                .as(typeReference);
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
                .as(typeReference);
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
        Result<Integer> result = (Result<Integer>) request.as(typeReference);
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
                .as(typeReference);
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
        final String result = Forest.post("http://localhost:" + server.getPort() + "/post")
                .addHeader("Content-Type", "application/xml")
                .addBody(xml)
                .asString();
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
        final String result = Forest.post("http://localhost:" + server.getPort() + "/post")
                .addHeader("Content-Encoding", "gzip")
                .addHeader("Content-Type", "application/xml")
                .addBody(compress)
                .asString();
        assertThat(result).isEqualTo(EXPECTED);
        mockRequest(server)
                .assertBodyEquals(compress);
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
                .as(typeReference);
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
                .as(typeReference);
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
                .as(typeReference);
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
                .as(typeReference);
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
                .as(typeReference);
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
                .as(typeReference);
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
                .as(typeReference);
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
        Result<Integer> result = Forest.post("http://localhost:" + server.getPort())
                .contentTypeMultipartFormData()
                .addFile("file", file)
                .execute()
                .result(new TypeReference<Result<Integer>>() {});
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
                .as(ForestResponse.class);
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
                .as(new TypeReference<Future<String>>() {
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
                .asFuture()
                .result(String.class);
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
                .asFuture()
                .await()
                .result(Map.class);
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
                .asFuture()
                .await()
                .result(new TypeReference<Map<String, Object>>() {
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
                .asFuture()
                .await(1, TimeUnit.SECONDS)
                .result(Map.class);
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
                    .asFuture()
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
                    .asFuture());
        }
        Forest.await(futures, res -> {
            String result = res.result(String.class);
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
                    .asFuture());
        }
        Forest.await(futures).forEach(res -> {
            String result = res.result(String.class);
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
                .asFuture();

        ForestFuture future2 = Forest.get("/")
                .port(server.getPort())
                .async()
                .addQuery("a", 1)
                .asFuture();

        Forest.await(future1, future2).forEach(res -> {
            String result = res.result(String.class);
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
                .onSuccess(((req, res) -> {
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
        request.as(InputStream.class);
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
        assertThat(interceptorChain.getInterceptorSize()).isEqualTo(1);
        assertThat(interceptorChain.beforeExecute(null).isProceed()).isFalse();
        assertThat(inter3Before.get()).isTrue();
    }

    static final AtomicBoolean inter3Before = new AtomicBoolean(false);

    public static class TestInterceptor implements Interceptor {

        public TestInterceptor() {
        }

        @Override
        public ForestJointPoint beforeExecute(ForestRequest request) {
            inter3Before.set(true);
            return cutoff();
        }

        @Override
        public void onSuccess(ForestRequest request, ForestResponse response) {
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
                .as(String.class);
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
                .downloadFile(dir, "")
                .onProgress(progress -> {
                    System.out.println("------------------------------------------");
                    System.out.println("total bytes: " + progress.getTotalBytes());
                    System.out.println("current bytes: " + progress.getCurrentBytes());
                    System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");
                    if (progress.isDone()) {
                        atomicProgress.set(progress);
                    }
                });

        ForestResponse<File> response = request.as(new TypeReference<ForestResponse<File>>() {});

        Assertions.assertThat(response)
                .isNotNull()
                .extracting(ForestResponse::getStatusCode)
                .isEqualTo(200);

        File file = response.result();

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
    public void performance() {
        int count = 10000;
        for (int i = 0; i < count; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED));
        }

        Forest.config()
                .setMaxRetryCount(0)
                .setLogEnabled(false)
                .setMaxConnections(10000)
                .setVar("port", server.getPort())
                .setVar("accept", "text/plain");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < count; i++) {
            Forest.get("http://localhost:{port}/abc")
                    .addHeader("Accept", "{accept}")
                    .execute();
        }
        stopWatch.stop();
        System.out.println("总耗时: " + stopWatch.getTotalTimeMillis() + "ms");
    }


    @Test
    public void performance_hutool() {
        int count = 10000;
        for (int i = 0; i < count; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED));
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < count; i++) {
            HttpUtil.createGet("http://localhost:" + server.getPort() + "/abc")
                    .header("Accept", "text/plain")
                    .execute();
        }
        stopWatch.stop();
        System.out.println("总耗时: " + stopWatch.getTotalTimeMillis() + "ms");
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
