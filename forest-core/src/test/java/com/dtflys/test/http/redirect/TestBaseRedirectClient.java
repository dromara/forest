package com.dtflys.test.http.redirect;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.BaseClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestBaseRedirectClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private RedirectClient redirectClient;

    private BaseRedirectClient baseRedirectClient;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }


    public TestBaseRedirectClient(String backend, String jsonConverter) {
        super(backend, jsonConverter, configuration);
        configuration.setVariableValue("port", server.getPort());
        redirectClient = configuration.createInstance(RedirectClient.class);
        baseRedirectClient = configuration.createInstance(BaseRedirectClient.class);
    }

    /**
     * ====================================================== 测试异步自动重定向 ======================================================
     */

    @Test
    public void testAsyncAutoRedirect() throws InterruptedException {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(301));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        AtomicReference<ForestResponse> atomicRes = new AtomicReference<>(null);
        redirectClient.testAutoRedirect_async(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }), ((data, req, res) -> {
            atomicRes.set(res);
            latch.countDown();
        }));
        latch.await(10, TimeUnit.SECONDS);
        assertThat(atomicReq.get()).isNotNull();
        assertThat(atomicReq.get().path()).isEqualTo("/b");
        assertThat(atomicRes.get()).isNotNull();
        assertThat(atomicRes.get().getStatusCode()).isEqualTo(200);
        String result = atomicRes.get().getContent();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }



    /**
     * ====================================================== 测试非自动重定向 ======================================================
     */

    @Test
    public void testNotAutoRedirect_301() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(301));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = redirectClient.testNotAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(atomicReq.get()).isNull();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(301);
        assertThat(response.isRedirection()).isTrue();
        assertThat(response.getRedirectionLocation()).isEqualTo("http://localhost:" + server.getPort() + "/b");
        String result = response.redirectionRequest().execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        assertThat(atomicReq.get()).isNotNull();
        assertThat(atomicReq.get().getPath()).isEqualTo("/b");
    }

    @Test
    public void testNotAutoRedirect_302() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(302));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = redirectClient.testNotAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(302);
        assertThat(response.isRedirection()).isTrue();
        assertThat(response.getRedirectionLocation()).isEqualTo("http://localhost:" + server.getPort() + "/b");
        String result = response.redirectionRequest().execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        assertThat(atomicReq.get()).isNotNull();
        assertThat(atomicReq.get().getPath()).isEqualTo("/b");
    }

    @Test
    public void testNotAutoRedirect_303() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(303));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = redirectClient.testNotAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(atomicReq.get()).isNull();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(303);
        assertThat(response.isRedirection()).isTrue();
        assertThat(response.getRedirectionLocation()).isEqualTo("http://localhost:" + server.getPort() + "/b");
        String result = response.redirectionRequest().execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        assertThat(atomicReq.get()).isNotNull();
        assertThat(atomicReq.get().getPath()).isEqualTo("/b");
    }

    @Test
    public void testNotAutoRedirect_304() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(304));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = redirectClient.testNotAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(atomicReq.get()).isNull();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(304);
        assertThat(response.isRedirection()).isTrue();
        assertThat(response.getRedirectionLocation()).isEqualTo("http://localhost:" + server.getPort() + "/b");
        String result = response.redirectionRequest().execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        assertThat(atomicReq.get()).isNotNull();
        assertThat(atomicReq.get().getPath()).isEqualTo("/b");
    }

    @Test
    public void testNotAutoRedirect_305() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(305));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = redirectClient.testNotAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(atomicReq.get()).isNull();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(305);
        assertThat(response.isRedirection()).isTrue();
        assertThat(response.getRedirectionLocation()).isEqualTo("http://localhost:" + server.getPort() + "/b");
        String result = response.redirectionRequest().execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        assertThat(atomicReq.get()).isNotNull();
        assertThat(atomicReq.get().getPath()).isEqualTo("/b");
    }

    @Test
    public void testNotAutoRedirect_306() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(306));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = redirectClient.testNotAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(atomicReq.get()).isNull();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(306);
        assertThat(response.isRedirection()).isTrue();
        assertThat(response.getRedirectionLocation()).isEqualTo("http://localhost:" + server.getPort() + "/b");
        String result = response.redirectionRequest().execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        assertThat(atomicReq.get()).isNotNull();
        assertThat(atomicReq.get().path()).isEqualTo("/b");
    }

    @Test
    public void testNotAutoRedirect_307() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(307));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = redirectClient.testNotAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(atomicReq.get()).isNull();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(307);
        assertThat(response.isRedirection()).isTrue();
        assertThat(response.getRedirectionLocation()).isEqualTo("http://localhost:" + server.getPort() + "/b");
        String result = response.redirectionRequest().execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        assertThat(atomicReq.get()).isNotNull();
        assertThat(atomicReq.get().path()).isEqualTo("/b");
    }


    /**
     * ====================================================== 测试自动重定向 ======================================================
     */

    @Test
    public void testAutoRedirect_301() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(301));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = redirectClient.testAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(atomicReq.get()).isNotNull();
        assertThat(atomicReq.get().path()).isEqualTo("/b");
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(200);
        String result = response.getResult();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testAutoRedirect_302() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(302));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = redirectClient.testAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(atomicReq.get()).isNotNull();
        assertThat(atomicReq.get().path()).isEqualTo("/b");
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(200);
        String result = response.getResult();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testAutoRedirect_303() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(303));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = redirectClient.testAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(atomicReq.get()).isNotNull();
        assertThat(atomicReq.get().path()).isEqualTo("/b");
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(200);
        String result = response.getResult();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testAutoRedirect_304() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(304));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = redirectClient.testAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(atomicReq.get()).isNotNull();
        assertThat(atomicReq.get().path()).isEqualTo("/b");
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(200);
        String result = response.getResult();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testAutoRedirect_305() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(305));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = redirectClient.testAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(atomicReq.get()).isNotNull();
        assertThat(atomicReq.get().path()).isEqualTo("/b");
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(200);
        String result = response.getResult();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testAutoRedirect_306() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(306));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = redirectClient.testAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(atomicReq.get()).isNotNull();
        assertThat(atomicReq.get().path()).isEqualTo("/b");
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(200);
        String result = response.getResult();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testAutoRedirect_307() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(307));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = redirectClient.testAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(atomicReq.get()).isNotNull();
        assertThat(atomicReq.get().path()).isEqualTo("/b");
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(200);
        String result = response.getResult();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }



    /**
     * ====================================================== 测试Base拦截器自动重定向 ======================================================
     */


    @Test
    public void testBaseAutoRedirect_301() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(301));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = baseRedirectClient.testAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(atomicReq.get()).isNotNull();
        assertThat(atomicReq.get().path()).isEqualTo("/b");
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(200);
        String result = response.getResult();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server).assertPathEquals("/");
        mockRequest(server)
                .assertPathEquals("/b")
                .assertBodyEquals("body=" + RedirectInterceptor.BODY);
    }

    @Test
    public void testBaseAutoRedirect_302() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(302));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = baseRedirectClient.testAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(atomicReq.get()).isNotNull();
        assertThat(atomicReq.get().path()).isEqualTo("/b");
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(200);
        String result = response.getResult();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server).assertPathEquals("/");
        mockRequest(server)
                .assertPathEquals("/b")
                .assertBodyEquals("body=" + RedirectInterceptor.BODY);
    }

    @Test
    public void testBaseAutoRedirect_303() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(303));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = baseRedirectClient.testAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(atomicReq.get()).isNotNull();
        assertThat(atomicReq.get().path()).isEqualTo("/b");
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(200);
        String result = response.getResult();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server).assertPathEquals("/");
        mockRequest(server)
                .assertPathEquals("/b")
                .assertBodyEquals("body=" + RedirectInterceptor.BODY);
    }

    @Test
    public void testBaseAutoRedirect_304() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(304));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = baseRedirectClient.testAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(atomicReq.get()).isNotNull();
        assertThat(atomicReq.get().path()).isEqualTo("/b");
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(200);
        String result = response.getResult();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server).assertPathEquals("/");
        mockRequest(server)
                .assertPathEquals("/b")
                .assertBodyEquals("body=" + RedirectInterceptor.BODY);
    }

    @Test
    public void testBaseAutoRedirect_305() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(305));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = baseRedirectClient.testAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(atomicReq.get()).isNotNull();
        assertThat(atomicReq.get().path()).isEqualTo("/b");
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(200);
        String result = response.getResult();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server).assertPathEquals("/");
        mockRequest(server)
                .assertPathEquals("/b")
                .assertBodyEquals("body=" + RedirectInterceptor.BODY);
    }

    @Test
    public void testBaseAutoRedirect_306() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(306));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = baseRedirectClient.testAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(atomicReq.get()).isNotNull();
        assertThat(atomicReq.get().path()).isEqualTo("/b");
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(200);
        String result = response.getResult();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server).assertPathEquals("/");
        mockRequest(server)
                .assertPathEquals("/b")
                .assertBodyEquals("body=" + RedirectInterceptor.BODY);
    }

    @Test
    public void testBaseAutoRedirect_307() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(307));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = baseRedirectClient.testAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(atomicReq.get()).isNotNull();
        assertThat(atomicReq.get().path()).isEqualTo("/b");
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(200);
        String result = response.getResult();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server).assertPathEquals("/");
        mockRequest(server)
                .assertPathEquals("/b")
                .assertBodyEquals("body=" + RedirectInterceptor.BODY);
    }

    /**
     * ====================================================== 测试Base拦截器非自动重定向 ======================================================
     */

    @Test
    public void testBaseNotAutoRedirect_301() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(301));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = baseRedirectClient.testNotAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(301);
        assertThat(response.isRedirection()).isTrue();
        assertThat(response.getRedirectionLocation()).isEqualTo("http://localhost:" + server.getPort() + "/b");
        String result = response.redirectionRequest().execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server).assertPathEquals("/");
        mockRequest(server)
                .assertPathEquals("/b")
                .assertBodyEquals("body=" + RedirectInterceptor.BODY);
    }

    @Test
    public void testBaseNotAutoRedirect_302() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(302));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = baseRedirectClient.testNotAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(302);
        assertThat(response.isRedirection()).isTrue();
        assertThat(response.getRedirectionLocation()).isEqualTo("http://localhost:" + server.getPort() + "/b");
        String result = response.redirectionRequest().execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server).assertPathEquals("/");
        mockRequest(server)
                .assertPathEquals("/b")
                .assertBodyEquals("body=" + RedirectInterceptor.BODY);
    }

    @Test
    public void testBaseNotAutoRedirect_303() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(303));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = baseRedirectClient.testNotAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(303);
        assertThat(response.isRedirection()).isTrue();
        assertThat(response.getRedirectionLocation()).isEqualTo("http://localhost:" + server.getPort() + "/b");
        String result = response.redirectionRequest().execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server).assertPathEquals("/");
        mockRequest(server)
                .assertPathEquals("/b")
                .assertBodyEquals("body=" + RedirectInterceptor.BODY);
    }

    @Test
    public void testBaseNotAutoRedirect_304() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(304));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = baseRedirectClient.testNotAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(304);
        assertThat(response.isRedirection()).isTrue();
        assertThat(response.getRedirectionLocation()).isEqualTo("http://localhost:" + server.getPort() + "/b");
        String result = response.redirectionRequest().execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server).assertPathEquals("/");
        mockRequest(server)
                .assertPathEquals("/b")
                .assertBodyEquals("body=" + RedirectInterceptor.BODY);
    }

    @Test
    public void testBaseNotAutoRedirect_305() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(305));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = baseRedirectClient.testNotAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(305);
        assertThat(response.isRedirection()).isTrue();
        assertThat(response.getRedirectionLocation()).isEqualTo("http://localhost:" + server.getPort() + "/b");
        String result = response.redirectionRequest().execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server).assertPathEquals("/");
        mockRequest(server)
                .assertPathEquals("/b")
                .assertBodyEquals("body=" + RedirectInterceptor.BODY);
    }

    @Test
    public void testBaseNotAutoRedirect_306() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(306));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = baseRedirectClient.testNotAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(306);
        assertThat(response.isRedirection()).isTrue();
        assertThat(response.getRedirectionLocation()).isEqualTo("http://localhost:" + server.getPort() + "/b");
        String result = response.redirectionRequest().execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server).assertPathEquals("/");
        mockRequest(server)
                .assertPathEquals("/b")
                .assertBodyEquals("body=" + RedirectInterceptor.BODY);
    }

    @Test
    public void testBaseNotAutoRedirect_307() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(307));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicReference<ForestRequest> atomicReq = new AtomicReference<>(null);
        ForestResponse<String> response = baseRedirectClient.testNotAutoRedirect(((redirectReq, prevReq, prevRes) -> {
            atomicReq.set(redirectReq);
        }));
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(307);
        assertThat(response.isRedirection()).isTrue();
        assertThat(response.getRedirectionLocation()).isEqualTo("http://localhost:" + server.getPort() + "/b");
        String result = response.redirectionRequest().execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server).assertPathEquals("/");
        mockRequest(server)
                .assertPathEquals("/b")
                .assertBodyEquals("body=" + RedirectInterceptor.BODY);
    }

}
