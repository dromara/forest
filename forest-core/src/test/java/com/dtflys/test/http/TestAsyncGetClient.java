package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestNetworkException;
import com.dtflys.forest.retryer.BackOffRetryer;
import com.dtflys.test.http.client.GetClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 15:02
 */
public class TestAsyncGetClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestAsyncGetClient.class);

    public final static String EXPECTED = "{\"status\":\"ok\"}";

    @Rule
    public final MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private final GetClient getClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
    }

    @Override
    public void afterRequests() {
    }

    public TestAsyncGetClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        getClient = configuration.createInstance(GetClient.class);
    }

    @Test
    public void testAsyncSimpleGet1() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean success = new AtomicBoolean(false);
        getClient.asyncSimpleGet(
                (data, request, response) -> {
                    log.info("data: " + data);
                    success.set(true);
                    assertEquals(EXPECTED, data);
                    latch.countDown();
                });
        log.info("send async get request");
        assertThat(success.get()).isFalse();
        latch.await(2, TimeUnit.SECONDS);
        assertThat(success.get()).isTrue();
    }

    @Test
    public void testAsyncSimpleGetError() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(404));
        CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean success = new AtomicBoolean(true);
        getClient.asyncSimpleGetError((ex, request, response) -> {
            success.set(false);
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        assertThat(success.get()).isFalse();
    }

    @Test
    public void testRetrySimpleGetError() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(404));
        AtomicReference<BackOffRetryer> retryerAtomicReference = new AtomicReference<>(null);
        CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean success = new AtomicBoolean(true);
        getClient.retrySimpleGetError((ex, request, response) -> {
            retryerAtomicReference.set((BackOffRetryer) request.getRetryer());
            success.set(false);
            latch.countDown();
        });
        latch.await(10, TimeUnit.SECONDS);
        assertThat(success.get()).isFalse();
        BackOffRetryer retryer = retryerAtomicReference.get();
        Assertions.assertThat(retryer).isNotNull();
        Assertions.assertThat(retryer.getMaxRetryCount()).isEqualTo(2);
        Assertions.assertThat(retryer.getMaxRetryInterval()).isEqualTo(500);
        Assertions.assertThat(retryer.getWaitedTime()).isEqualTo(500 + 500);
    }

    @Test
    public void testSimpleGetTimeout() throws InterruptedException {
        server.enqueue(new MockResponse()
                .setHeadersDelay(10, TimeUnit.SECONDS)
                .setBody(EXPECTED));
        CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean success = new AtomicBoolean(true);
        getClient.asyncSimpleGetTimeout((ex, request, response) -> {
            success.set(false);
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);
        assertThat(success.get()).isFalse();
    }


    @Test
    public void testRetrySimpleGetTimeout() throws InterruptedException {
        server.enqueue(new MockResponse()
                .setHeadersDelay(10, TimeUnit.SECONDS)
                .setBody(EXPECTED));
        AtomicReference<BackOffRetryer> retryerAtomicReference = new AtomicReference<>(null);
        CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean success = new AtomicBoolean(true);
        getClient.retrySimpleGetTimeout((ex, request, response) -> {
            success.set(false);
            retryerAtomicReference.set((BackOffRetryer) request.getRetryer());
            latch.countDown();
        });
        latch.await(20, TimeUnit.SECONDS);
        assertThat(success.get()).isFalse();
        BackOffRetryer retryer = retryerAtomicReference.get();
        Assertions.assertThat(retryer).isNotNull();
        Assertions.assertThat(retryer.getMaxRetryCount()).isEqualTo(2);
        Assertions.assertThat(retryer.getMaxRetryInterval()).isEqualTo(500);
        Assertions.assertThat(retryer.getWaitedTime()).isEqualTo(500 + 500);
    }



    @Test
    public void testAsyncSimpleGetWithBodyDelay() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setBodyDelay(500, TimeUnit.MILLISECONDS)
                        .setBody(EXPECTED));
        CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean success = new AtomicBoolean(false);
        getClient.asyncSimpleGet(
                (data, request, response) -> {
                    log.info("data: " + data);
                    success.set(true);
                    assertEquals(EXPECTED, data);
                    latch.countDown();
                });
        log.info("send async get request");
        assertFalse(success.get());
        latch.await(2, TimeUnit.SECONDS);
        assertTrue(success.get());
    }


    @Test
    public void testAsyncSimpleGetWithHeadersDelay() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setBodyDelay(500, TimeUnit.MILLISECONDS)
                        .setBody(EXPECTED));
        CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean success = new AtomicBoolean(false);
        getClient.asyncSimpleGet(
                (data, request, response) -> {
                    log.info("data: " + data);
                    success.set(true);
                    assertEquals(EXPECTED, data);
                    latch.countDown();
                });
        log.info("send async get request");
        assertFalse(success.get());
        latch.await(2, TimeUnit.SECONDS);
        assertTrue(success.get());
    }

    @Test
    public void testAsyncSimpleGetWithHeadersAndBodyDelay() throws InterruptedException {
        server.enqueue(
                new MockResponse()
                        .setHeadersDelay(500, TimeUnit.MILLISECONDS)
                        .setBodyDelay(500, TimeUnit.MILLISECONDS)
                        .setBody(EXPECTED));
        CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean success = new AtomicBoolean(false);
        getClient.asyncSimpleGet(
                (data, request, response) -> {
                    log.info("data: " + data);
                    success.set(true);
                    assertThat(response.getContent()).isEqualTo(EXPECTED);
                    latch.countDown();
                });
        log.info("send async get request");
        assertFalse(success.get());
        latch.await(2, TimeUnit.SECONDS);
        assertTrue(success.get());
    }


    @Test
    public void testAsyncSimpleGet2() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean success = new AtomicBoolean(false);
        getClient.asyncSimpleGet2(
                (data, request, response) -> {
                    assertThat(data).isNotNull();
                    assertThat(data.getStatus()).isEqualTo("ok");
                    success.set(true);
                    assertThat(response.getContent()).isEqualTo(EXPECTED);
                    latch.countDown();
                });
        log.info("send async get request");
        assertFalse(success.get());
        latch.await(2, TimeUnit.SECONDS);
        assertTrue(success.get());
    }


    @Test
    public void testAsyncSimpleGet3() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean success = new AtomicBoolean(false);
        getClient.asyncSimpleGet3(
                (data, request, response) -> {
                    assertThat(data).isNotNull();
                    assertEquals("ok", data.getStatus());
                    success.set(true);
                    latch.countDown();
                });
        log.info("send async get request");
        assertFalse(success.get());
        latch.await(2, TimeUnit.SECONDS);
        assertTrue(success.get());
    }



    @Test
    public void testAsyncSimpleGetWithFuture() throws ExecutionException, InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Future<String> future = getClient.asyncSimpleGetWithFuture();
        log.info("send async get request");
        assertThat(future).isNotNull();
        String data = future.get();
        log.info("data: " + data);
        assertThat(data).isNotNull();
        assertEquals(EXPECTED, data);
    }


    @Test
    public void testAsyncVarParamGet() throws InterruptedException, ExecutionException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        final AtomicBoolean success = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);
        Future<String> future = getClient.asyncVarParamGet("foo", (data, request, response) -> {
            log.info("data: " + data);
            success.set(true);
            assertEquals(EXPECTED, data);
            latch.countDown();
        }, (ex, request, response) -> {
            latch.countDown();
        });
        log.info("send async get request");
        assertFalse(success.get());
        assertThat(future).isNotNull();
        latch.await(5, TimeUnit.SECONDS);
        assertTrue(success.get());
        assertTrue(future.isDone());
        assertEquals(EXPECTED, future.get());
    }


    @Test
    public void testAsyncVarParamGetError_404() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(404).setBody(EXPECTED));
        final AtomicBoolean success = new AtomicBoolean(false);
        final AtomicBoolean error = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);
        Future<String> future = getClient.asyncVarParamGet(
                "error param",
                (data, request, response) -> {
                    error.set(false);
                    success.set(true);
                    latch.countDown();
                }, (ex, request, response) -> {
                    error.set(true);
                    success.set(false);
                    assertTrue(ex instanceof ForestNetworkException);
                    int statusCode = ((ForestNetworkException) ex).getStatusCode();
                    log.error("status code = " + statusCode);
                    assertEquals(404, statusCode);
                    latch.countDown();
                });
        log.info("send async get request");
        assertFalse(error.get());
        assertThat(future).isNotNull();
        latch.await(5, TimeUnit.SECONDS);
        assertFalse(success.get());
        assertTrue(error.get());
    }

    @Test
    public void testAsyncVarParamGetError_400() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(400).setBody(EXPECTED));
        final AtomicBoolean success = new AtomicBoolean(false);
        final AtomicBoolean error = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);
        Future<String> future = getClient.asyncVarParamGet(
                "error param",
                (data, request, response) -> {
                    error.set(false);
                    success.set(true);
                    latch.countDown();
                }, (ex, request, response) -> {
                    error.set(true);
                    success.set(false);
                    assertTrue(ex instanceof ForestNetworkException);
                    int statusCode = ((ForestNetworkException) ex).getStatusCode();
                    log.error("status code = " + statusCode);
                    assertEquals(400, statusCode);
                    latch.countDown();
                });
        log.info("send async get request");
        assertFalse(error.get());
        assertThat(future).isNotNull();
        latch.await(5, TimeUnit.SECONDS);
        assertFalse(success.get());
        assertTrue(error.get());
    }


}
