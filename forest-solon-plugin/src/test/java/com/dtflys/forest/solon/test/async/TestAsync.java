package com.dtflys.forest.solon.test.async;

import com.dtflys.forest.Forest;
import com.dtflys.forest.annotation.BindingVar;
import com.dtflys.forest.backend.AsyncHttpExecutor;
import com.dtflys.forest.config.AsyncThreadPools;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestAsyncAbortException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(env = "async")
public class TestAsync {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    @Inject
    private AsyncClient asyncClient;

    @BindingVar("port")
    public int getPort() {
        return server.getPort();
    }

    @Inject
    private ForestConfiguration configuration;


    @Test
    public void testFuture() {
        server.enqueue(new MockResponse().setBody(EXPECTED).setHeadersDelay(500, TimeUnit.MILLISECONDS));
        server.enqueue(new MockResponse().setBody(EXPECTED).setHeadersDelay(500, TimeUnit.MILLISECONDS));
        server.enqueue(new MockResponse().setBody(EXPECTED).setHeadersDelay(500, TimeUnit.MILLISECONDS));
        ForestConfiguration config = ForestConfiguration.createConfiguration();
        config.setMaxAsyncThreadSize(1);
        config.setMaxAsyncQueueSize(0);
        Throwable throwable = null;
        for (int i = 0; i < 3; i++) {
            try {
                asyncClient.postFuture();
            } catch (Throwable th) {
                throwable = th;
            }
        }
        assertThat(throwable).isNotNull().isInstanceOf(ForestAsyncAbortException.class);
    }

    @Test
    public void testFuture2() {

        int size = 16;
        int threads = 8;
        boolean threadPoolFull = false;
        configuration.setMaxAsyncThreadSize(threads);
        configuration.setMaxAsyncQueueSize(0);
        asyncClient = configuration.client(AsyncClient.class);
        for (int i = 0; i < size; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED).setHeadersDelay(500, TimeUnit.MILLISECONDS));
        }
        try {
            for (int i = 0; i < size; i++) {
                asyncClient.postFuture();
            }
        } catch (RuntimeException e) {
            if (e instanceof ForestAsyncAbortException) {
                assertThat(e.getMessage()).isEqualTo("[Forest] Asynchronous thread pool is full! " +
                        "[Thread name: main, Max pool size: 8, Core pool size: 8, Active pool size: 8, Task count: 8]");
                threadPoolFull = true;
            } else {
                throw e;
            }
        }

        System.out.println("max async thread size: " + AsyncHttpExecutor.getMaxAsyncThreadSize(configuration));
        System.out.println("async thread size: " + AsyncHttpExecutor.getAsyncThreadSize(configuration));
        assertThat(threadPoolFull).isTrue();
        assertThat(AsyncHttpExecutor.getMaxAsyncThreadSize(configuration)).isEqualTo(threads);
        assertThat(AsyncHttpExecutor.getAsyncThreadSize(configuration)).isEqualTo(threads);
    }


    @Test
    public void testFuture3() {
        int size = 16;
        int threads = 8;
        boolean threadPoolFull = false;
        configuration = ForestConfiguration.createConfiguration();
        configuration.setMaxAsyncThreadSize(threads);
        configuration.setMaxAsyncQueueSize(0);
        for (int i = 0; i < size; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED).setHeadersDelay(500, TimeUnit.MILLISECONDS));
        }
        try {
            for (int i = 0; i < size; i++) {
                System.out.println("=====" + i);
                configuration.get("/")
                        .host("localhost")
                        .port(server.getPort())
                        .async()
                        .execute();
            }
        } catch (RuntimeException e) {
            if (e instanceof ForestAsyncAbortException) {
                assertThat(e.getMessage()).isEqualTo("[Forest] Asynchronous thread pool is full! " +
                        "[Thread name: main, Max pool size: 8, Core pool size: 8, Active pool size: 8, Task count: 8]");
                threadPoolFull = true;
            } else {
                throw e;
            }
        }
        System.out.println("max async thread size: " + AsyncHttpExecutor.getAsyncThreadSize(configuration));
        System.out.println("async thread size: " + AsyncHttpExecutor.getAsyncThreadSize(configuration));
        assertThat(threadPoolFull).isTrue();
        assertThat(AsyncHttpExecutor.getAsyncThreadSize(configuration)).isEqualTo(threads);
        assertThat(AsyncHttpExecutor.getAsyncThreadSize(configuration)).isEqualTo(threads);
    }
}
