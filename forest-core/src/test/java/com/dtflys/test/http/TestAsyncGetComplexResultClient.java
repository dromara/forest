package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestNetworkException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.client.GetClient;
import com.dtflys.test.mock.AsyncGetComplexResultMockServer;
import com.dtflys.test.mock.AsyncGetMockServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 15:02
 */
public class TestAsyncGetComplexResultClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestAsyncGetComplexResultClient.class);

    @Rule
    public AsyncGetComplexResultMockServer server = new AsyncGetComplexResultMockServer(this);

    private static ForestConfiguration configuration;

    private static GetClient getClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
    }

    public TestAsyncGetComplexResultClient(HttpBackend backend) {
        super(backend, configuration);
        getClient = configuration.createInstance(GetClient.class);
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }


    @Test
    public void testAsyncSimpleGet3() throws InterruptedException {
        final AtomicBoolean success = new AtomicBoolean(false);
        getClient.asyncSimpleGet3(
                (data, request, response) -> {
                    log.info("data: " + data.getStatus());
                    success.set(true);
                });
        log.info("send async get request");
        assertFalse(success.get());
        Thread.sleep(2000L);
        assertTrue(success.get());
    }


}
