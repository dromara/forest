package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestNetworkException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.mock.AsyncGetMockServer;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestNetworkException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.client.GetClient;
import com.dtflys.test.model.Result;
import com.dtflys.test.model.TestResult;
import org.junit.Before;
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

import static org.junit.Assert.*;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 15:02
 */
public class TestAsyncGetClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestAsyncGetClient.class);

    @Rule
    public AsyncGetMockServer server = new AsyncGetMockServer(this);

    private static ForestConfiguration configuration;

    private static GetClient getClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
    }

    public TestAsyncGetClient(HttpBackend backend) {
        super(backend, configuration);
        getClient = configuration.createInstance(GetClient.class);
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }



    @Test
    public void testAsyncSimpleGet() throws InterruptedException {
        final AtomicBoolean success = new AtomicBoolean(false);
        getClient.asyncSimpleGet(
                new OnSuccess<String>() {
                    @Override
                    public void onSuccess(String data, ForestRequest request, ForestResponse response) {
                        log.info("data: " + data);
                        success.set(true);
                        assertEquals(AsyncGetMockServer.EXPECTED, data);
                    }
                });
        log.info("send async get request");
        assertFalse(success.get());
        Thread.sleep(2000L);
        assertTrue(success.get());
    }

    @Test
    public void testAsyncSimpleGet2() throws InterruptedException {
        final AtomicBoolean success = new AtomicBoolean(false);
        getClient.asyncSimpleGet2(
                (data, request, response) -> {
                    log.info("data: " + data.getStatus());
                    success.set(true);
                    assertEquals(AsyncGetMockServer.EXPECTED, data);
                });
        log.info("send async get request");
        assertFalse(success.get());
        Thread.sleep(2000L);
        assertTrue(success.get());
    }



    @Test
    public void testAsyncSimpleGetWithFuture() throws ExecutionException, InterruptedException {
        Future<String> future = getClient.asyncSimpleGetWithFuture();
        log.info("send async get request");
        assertNotNull(future);
        String data = future.get();
        log.info("data: " + data);
        assertNotNull(data);
        assertEquals(AsyncGetMockServer.EXPECTED, data);
    }


    @Test
    public void testAsyncVarParamGet() throws InterruptedException, ExecutionException {
        final AtomicBoolean success = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);
        Future<String> future = getClient.asyncVarParamGet("foo", (data, request, response) -> {
            log.info("data: " + data);
            success.set(true);
            assertEquals(AsyncGetMockServer.EXPECTED, data);
            latch.countDown();
        }, (ex, request, response) -> {
            latch.countDown();
        });
        log.info("send async get request");
        assertFalse(success.get());
        assertNotNull(future);
        latch.await(5, TimeUnit.SECONDS);
        assertTrue(success.get());
        assertTrue(future.isDone());
        assertEquals(AsyncGetMockServer.EXPECTED, future.get());
    }


    @Test
    public void testAsyncVarParamGetError() throws InterruptedException {
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
        assertNotNull(future);
        latch.await(5, TimeUnit.SECONDS);
        assertFalse(success.get());
        assertTrue(error.get());

    }

}
