package org.forest.http;

import org.forest.callback.OnSuccess;
import org.forest.config.ForestConfiguration;
import org.forest.http.client.GetClient;
import org.forest.mock.AsyncGetMockServer;
import org.forest.mock.GetMockServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 15:02
 */
public class TestAsyncGetClient {

    private final static Logger log = LoggerFactory.getLogger(TestAsyncGetClient.class);

    @Rule
    public AsyncGetMockServer server = new AsyncGetMockServer(this);

    private static ForestConfiguration configuration;

    private static GetClient getClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        getClient = configuration.createInstance(GetClient.class);
    }


    @Before
    public void prepareMockServer() {
        server.initServer();
    }



    @Test
    public void testAsyncSimpleGet() throws InterruptedException {
        final AtomicBoolean success = new AtomicBoolean(false);
        getClient.asyncSimpleGet(new OnSuccess<String>() {
            @Override
            public void onSuccess(String data, ForestRequest request, ForestResponse response) {
                log.info("data: " + data);
                success.set(true);
            }
        });
        log.info("send async get request");
        assertFalse(success.get());
        Thread.sleep(2000L);
        assertTrue(success.get());
    }


    public void testAsyncSimpleGetWithFuture() {

    }

}
