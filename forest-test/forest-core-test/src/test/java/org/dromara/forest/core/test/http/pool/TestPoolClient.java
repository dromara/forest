package org.dromara.forest.core.test.http.pool;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.dromara.forest.backend.HttpBackend;
import org.dromara.forest.backend.KotlinCoroutineHttpExecutor;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.core.test.http.BaseClientTest;
import org.dromara.forest.exceptions.ForestAbortException;
import org.dromara.forest.exceptions.ForestPoolException;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.pool.ForestRequestPool;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestPoolClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    public TestPoolClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
    }

    @Test
    public void testPool_timeout() throws InterruptedException {
        ForestConfiguration poolConf = ForestConfiguration.createConfiguration()
                .setVariableValue("port", server.getPort())
                .setMaxConnections(10)
                .setMaxRequestQueueSize(1);
        PoolClient poolClient = poolConf.client(PoolClient.class);
        int count = 100;
        for (int i = 0; i < count; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED).setHeadersDelay(1, TimeUnit.SECONDS));
        }
        ForestRequestPool pool = poolConf.getPool();
        ExecutorService executorService = Executors.newFixedThreadPool(count);
        CountDownLatch latch = new CountDownLatch(count);
        AtomicBoolean hasOut = new AtomicBoolean(false);
        AtomicReference<ForestAbortException> exceptionRef = new AtomicReference<>(null);
        for (int i = 0; i < count; i++) {
            executorService.execute(() -> {
                ForestResponse<String> response = poolClient.send();
                if (pool.getRunningPoolSize() > pool.getMaxPoolSize()) {
                    hasOut.set(true);
                } else {
                    System.out.println("pool running size: " + pool.getRunningPoolSize() + ", max size: " + pool.getMaxPoolSize() + ", queue size: " + pool.getQueueSize());
                }
                if (response.getException() != null && response.getException() instanceof ForestAbortException) {
                    response.getException().printStackTrace();
                    exceptionRef.set((ForestAbortException) response.getException());
                }
                latch.countDown();
            });
        }
        latch.await();
        assertThat(hasOut.get()).isFalse();
        assertThat(exceptionRef.get()).isNotNull();
    }


    @Test
    public void testPool_not_timeout() throws InterruptedException {
        ForestConfiguration poolConf = ForestConfiguration.createConfiguration()
                .setVariableValue("port", server.getPort())
                .setMaxConnections(100)
                .setMaxRequestQueueSize(100);
        PoolClient poolClient = poolConf.client(PoolClient.class);
        int count = 100;
        for (int i = 0; i < count; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED).setHeadersDelay(1, TimeUnit.SECONDS));
        }
        ForestRequestPool pool = poolConf.getPool();
        ExecutorService executorService = Executors.newFixedThreadPool(count);
        CountDownLatch latch = new CountDownLatch(count);
        AtomicBoolean hasOut = new AtomicBoolean(false);
        AtomicReference<ForestAbortException> exceptionRef = new AtomicReference<>(null);
        for (int i = 0; i < count; i++) {
            executorService.execute(() -> {
                ForestResponse<String> response = poolClient.send();
                if (pool.getRunningPoolSize() > pool.getMaxPoolSize()) {
                    hasOut.set(true);
                } else {
                    System.out.println("pool running size: " + pool.getRunningPoolSize() + ", max size: " + pool.getMaxPoolSize() + ", queue size: " + pool.getQueueSize());
                }
                if (response.getException() != null && response.getException() instanceof ForestAbortException) {
                    response.getException().printStackTrace();
                    exceptionRef.set((ForestAbortException) response.getException());
                }
                latch.countDown();
            });
        }
        latch.await();
        assertThat(hasOut.get()).isFalse();
        assertThat(exceptionRef.get()).isNull();
    }




    @Test
    public void testPoolPerRoute() throws InterruptedException {
        KotlinCoroutineHttpExecutor.Companion.restartPool();
        ForestConfiguration poolConf = ForestConfiguration.createConfiguration()
                .setVariableValue("port", server.getPort())
                .setMaxConnections(100)
                .setMaxRouteConnections(10)
                .setMaxRequestQueueSize(1);
        PoolClient poolClient = poolConf.client(PoolClient.class);
        int count = 100;
        for (int i = 0; i < count; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED).setHeadersDelay(1, TimeUnit.SECONDS));
        }
        ForestRequestPool pool = poolConf.getPool();
        ExecutorService executorService = Executors.newFixedThreadPool(count);
        CountDownLatch latch = new CountDownLatch(count);
        AtomicBoolean hasOut = new AtomicBoolean(false);
        AtomicReference<ForestAbortException> exceptionRef = new AtomicReference<>(null);
        for (int i = 0; i < count; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    String host = finalI % 2 == 0 ? "localhost" : "127.0.0.1";
                    ForestRequest request = poolClient.send(host);
                    request.execute();
                    if (pool.getRunningPoolSize() > pool.getMaxPoolSize()) {
                        hasOut.set(true);
                    } else {
                        System.out.println("pool running size: " + pool.getRunningPoolSize() +
                                ", max size: " + pool.getMaxPoolSize() +
                                ", queue size: " + pool.getQueueSize() +
                                ", max route size: " + pool.getMaxPoolSizePerRoute() +
                                ", route size: " + request.route().getRequestCount().get());
                    }
                } catch (ForestAbortException e) {
                    e.printStackTrace();
                    exceptionRef.set(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        assertThat(hasOut.get()).isFalse();
        assertThat(exceptionRef.get()).isNotNull();
    }



    @Test
    public void testPoolAsync_globalPool() throws InterruptedException {
        KotlinCoroutineHttpExecutor.Companion.restartPool();
        ForestConfiguration poolConf = ForestConfiguration.createConfiguration()
                .setVariableValue("port", server.getPort())
                .setMaxConnections(10)
                .setMaxAsyncThreadSize(300)
                .setMaxRequestQueueSize(1);
        PoolClient poolClient = poolConf.client(PoolClient.class);
        int count = 300;
        for (int i = 0; i < count; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED).setHeadersDelay(1, TimeUnit.SECONDS));
        }
        ForestRequestPool pool = poolConf.getPool();
        CountDownLatch latch = new CountDownLatch(count);
        AtomicBoolean hasOut = new AtomicBoolean(false);
        AtomicReference<ForestPoolException> exceptionRef = new AtomicReference<>(null);
        for (int i = 0; i < count; i++) {
            poolClient.sendAsync(i, (data, req, res) -> {
                if (pool.getRunningPoolSize() > pool.getMaxPoolSize()) {
                    hasOut.set(true);
                } else {
                    System.out.println("pool running size: " + pool.getRunningPoolSize() + ", max size: " + pool.getMaxPoolSize() + ", queue size: " + pool.getQueueSize());
                }
                latch.countDown();
            }, (e, req, res) -> {
                e.printStackTrace();
                if (e instanceof ForestPoolException) {
                    exceptionRef.set((ForestPoolException) e);
                }
                latch.countDown();
            });
        }
        latch.await();
        assertThat(hasOut.get()).isFalse();
        assertThat(exceptionRef.get()).isNotNull();
    }


    @Test
    public void testPoolAsync_asyncPool() throws InterruptedException {
        ForestConfiguration poolConf = ForestConfiguration.createConfiguration()
                .setVariableValue("port", server.getPort())
                .setMaxConnections(30)
                .setMaxAsyncThreadSize(30)
                .setMaxAsyncQueueSize(10);
        PoolClient poolClient = poolConf.client(PoolClient.class);
        int count = 100;
        for (int i = 0; i < count; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED));
        }
        ForestRequestPool pool = poolConf.getPool();
        CountDownLatch latch = new CountDownLatch(count);
        AtomicBoolean hasOut = new AtomicBoolean(false);
        AtomicReference<ForestPoolException> exceptionRef = new AtomicReference<>(null);
        for (int i = 0; i < count; i++) {
            poolClient.sendAsync(i, (data, req, res) -> {
                latch.countDown();
                if (pool.getRunningPoolSize() > pool.getMaxPoolSize()) {
                    hasOut.set(true);
                } else {
                    System.out.println("[" + Thread.currentThread().getName() + "]\tpool running size: " + pool.getRunningPoolSize() + ", max size: " + pool.getMaxPoolSize() + ", queue size: " + pool.getQueueSize() + ", count: " + latch.getCount());
                }
            }, (e, req, res) -> {
                latch.countDown();
                System.out.println("Error ==> pool running size: " + pool.getRunningPoolSize() + ", max size: " + pool.getMaxPoolSize() + ", queue size: " + pool.getQueueSize() + ", count: " + latch.getCount());
                e.printStackTrace();
                if (e instanceof ForestPoolException) {
                    exceptionRef.set((ForestPoolException) e);
                }
            });
        }
        latch.await();
        assertThat(hasOut.get()).isFalse();
        assertThat(exceptionRef.get()).isNotNull();
    }



}
