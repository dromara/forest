package com.dtflys.test;

import com.dtflys.forest.Forest;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.http.ForestHeader;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.utils.TypeReference;
import com.dtflys.test.http.BaseClientTest;
import com.dtflys.test.model.Result;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
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
    public void testRequest_get_return_string() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = Forest.get("http://localhost:" + server.getPort()).execute(String.class);
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
        Map result = Forest.get("http://localhost:" + server.getPort()).execute(Map.class);
        assertThat(result).isNotNull();
        assertThat(result.get("status")).isEqualTo("1");
        assertThat(result.get("data")).isEqualTo("2");
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
        Result<Integer> result = Forest.post("http://localhost:" + server.getPort() + "/post")
                .contentFormUrlEncoded()
                .addBody("a", 1)
                .addBody("b", 2)
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
        Result<Integer> result = Forest.post("http://localhost:" + server.getPort() + "/post")
                .contentTypeJson()
                .addBody(map)
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
                .setRetryCount(3)
                .setRetryWhen(((req, res) -> res.getStatusCode() == 203));
        request.execute();
        assertThat(request.getCurrentRetryCount()).isEqualTo(3);
    }

    @Test
    public void testRequest_async_retryWhen_success() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        ForestRequest<?> request = Forest.get("http://localhost:" + server.getPort())
                .setAsync(true)
                .setRetryCount(3)
                .setRetryWhen(((req, res) -> res.getStatusCode() == 203))
                .setOnSuccess(((data, req, res) -> {
                    isSuccess.set(true);
                    latch.countDown();
                }));
        request.execute();
        latch.await();
        assertThat(request.getCurrentRetryCount()).isEqualTo(3);
        assertThat(isSuccess.get()).isTrue();
    }

    @Test
    public void testRequest_sync_retryWhen_error_not_retry() {
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(400));
        AtomicBoolean isError = new AtomicBoolean(false);
        ForestRequest<?> request = Forest.get("http://localhost:" + server.getPort())
                .setRetryCount(3)
                .setRetryWhen(((req, res) -> res.getStatusCode() == 200))
                .setOnError(((ex, req, res) -> {
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
        ForestRequest<?> request = Forest.get("http://localhost:" + server.getPort())
                .setRetryCount(3)
                .setOnError(((ex, req, res) -> {
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
                .setRetryCount(3)
                .setRetryWhen(((req, res) -> res.getStatusCode() == 200))
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
                .setRetryCount(3)
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
