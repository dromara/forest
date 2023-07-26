package org.dromara.forest.springboot.test.async;

import org.dromara.forest.Forest;
import org.dromara.forest.annotation.BindingVar;
import org.dromara.forest.backend.AsyncHttpExecutor;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.exceptions.ForestAsyncAbortException;
import org.dromara.forest.springboot.test.address.TestAddress;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import jakarta.annotation.Resource;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@ActiveProfiles("async")
@SpringBootTest(classes = TestAddress.class)
@ComponentScan(basePackages = "org.dromara.forest.springboot.test.async")
@EnableAutoConfiguration
public class TestAsync {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    @Resource
    private AsyncClient asyncClient;

    @BindingVar("port")
    public int getPort() {
        return server.getPort();
    }

    @Resource
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
                System.out.println("=====" + i);
                Forest.head("/")
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
        System.out.println("max async thread size: " + AsyncHttpExecutor.getMaxAsyncThreadSize());
        System.out.println("async thread size: " + AsyncHttpExecutor.getAsyncThreadSize());
        assertThat(threadPoolFull).isTrue();
        assertThat(AsyncHttpExecutor.getMaxAsyncThreadSize()).isEqualTo(threads);
        assertThat(AsyncHttpExecutor.getAsyncThreadSize()).isEqualTo(threads);
    }

}