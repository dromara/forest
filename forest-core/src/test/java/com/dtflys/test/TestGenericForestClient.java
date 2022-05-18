package com.dtflys.test;

import com.alibaba.fastjson.JSON;
import com.dtflys.forest.Forest;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.http.ForestAddress;
import com.dtflys.forest.http.ForestHeader;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestType;
import com.dtflys.forest.http.ForestURL;
import com.dtflys.forest.utils.TypeReference;
import com.dtflys.test.http.BaseClientTest;
import com.dtflys.test.model.Result;
import com.google.common.collect.Lists;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.entry;
import static org.assertj.core.api.AssertionsForClassTypes.linesOf;

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
        assertThat(request.host()).isEqualTo("localhost");
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
        String result = Forest.get("/A")
                .basePath("http://localhost:" + server.getPort() +  "/X1/X2")
                .execute(String.class);
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
                .execute(new TypeReference<Map<String, String>>() {}.getType());
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
        Type type = new TypeReference<Map<String, Integer>>() {}.getType();
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
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {};
        Result<Integer> result = Forest.get("http://localhost:" + server.getPort()).execute(typeReference);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getData()).isEqualTo(2);
    }

    @Test
    public void testRequest_get_query_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {};
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
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {};
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
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {};
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
    public void testRequest_post_form_body_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {};
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
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {};
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
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {};
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
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {};
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
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {};
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
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {};
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
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {};
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
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {};
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
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {};
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
    public void testRequest_delete_query_keys() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {};
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
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {};
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
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {};
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
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {};
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
        TypeReference<Result<Integer>> typeReference = new TypeReference<Result<Integer>>() {};
        String path = Objects.requireNonNull(this.getClass().getResource("/test-img.jpg")).getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        File file = new File(path);
        Result<Integer> result = Forest.post("http://localhost:" + server.getPort())
                .contentTypeMultipartFormData()
                .addFile("file", file)
                .execute(typeReference);
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
        ForestRequest<?> request = Forest.get("http://localhost:" + server.getPort())
                .maxRetryCount(3)
                .maxRetryInterval(2)
                .retryWhen(((req, res) -> res.statusIs(203)));
        request.execute();
        assertThat(request.getCurrentRetryCount()).isEqualTo(3);
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
                .onError(((ex, req, res) -> {
                    isError.set(true);
                }));
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
                .onError(((ex, req, res) -> {
                    isError.set(true);
                }));
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
        request.execute();
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

}
