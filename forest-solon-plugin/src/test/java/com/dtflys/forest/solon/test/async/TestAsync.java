package com.dtflys.forest.solon.test.async;

import com.dtflys.forest.Forest;
import com.dtflys.forest.annotation.BindingVar;
import com.dtflys.forest.backend.AsyncHttpExecutor;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestAsyncAbortException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Component
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(env = "async")
public class TestAsync {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    @Inject
    private AsyncClient asyncClient;

    @BindingVar("port") //需要在类上加 @Component
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
        ForestConfiguration config = ForestConfiguration.configuration();
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
        assertThat(configuration.getMaxAsyncThreadSize()).isEqualTo(300);
        assertThat(configuration.getMaxAsyncQueueSize()).isEqualTo(350);

        int size = 16;
        int threads = 8;
        boolean threadPoolFull = false;
        configuration.setMaxAsyncThreadSize(threads);
        configuration.setMaxAsyncQueueSize(0);
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
        System.out.println("max async thread size: " + AsyncHttpExecutor.getMaxAsyncThreadSize());
        System.out.println("async thread size: " + AsyncHttpExecutor.getAsyncThreadSize());
        assertThat(threadPoolFull).isTrue();
        assertThat(AsyncHttpExecutor.getMaxAsyncThreadSize()).isEqualTo(threads);
        assertThat(AsyncHttpExecutor.getAsyncThreadSize()).isEqualTo(threads);
    }


    @Test
    public void testFuture3() {
        assertThat(configuration.getMaxAsyncThreadSize()).isEqualTo(300);
        assertThat(configuration.getMaxAsyncQueueSize()).isEqualTo(350);

        int size = 16;
        int threads = 8;
        boolean threadPoolFull = false;
        configuration.setMaxAsyncThreadSize(threads);
        configuration.setMaxAsyncQueueSize(0);
        for (int i = 0; i < size; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED).setHeadersDelay(500, TimeUnit.MILLISECONDS));
        }
        try {
            for (int i = 0; i < size; i++) {
                Forest.head("/")
                        .host("localhost")
                        .port(server.getPort())
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
        System.out.println("max async thread size: " + AsyncHttpExecutor.getMaxAsyncThreadSize());
        System.out.println("async thread size: " + AsyncHttpExecutor.getAsyncThreadSize());
        assertThat(threadPoolFull).isTrue();
        assertThat(AsyncHttpExecutor.getMaxAsyncThreadSize()).isEqualTo(threads);
        assertThat(AsyncHttpExecutor.getAsyncThreadSize()).isEqualTo(threads);
    }

}
