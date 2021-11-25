package com.dtflys.test.http.pool;

import com.dtflys.forest.backend.ForestConnectionManager;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.backend.okhttp3.OkHttp3Backend;
import com.dtflys.forest.backend.okhttp3.conn.OkHttp3ConnectionManager;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.BaseClientTest;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestPoolClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    @Resource
    private PoolClient poolClient;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration()
                .setMaxConnections(10);
    }

    public TestPoolClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        poolClient = configuration.client(PoolClient.class);
    }

    @Test
    public void testPool() throws InterruptedException {
        int count = 100;
        for (int i = 0; i < count; i++) {
            server.enqueue(new MockResponse().setBody(EXPECTED).setHeadersDelay(1, TimeUnit.SECONDS));
        }
        HttpBackend backend = configuration.getBackend();
        ForestConnectionManager connectionManager = backend.getConnectionManager();
        ExecutorService executorService = Executors.newFixedThreadPool(count);
        CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            executorService.execute(() -> {
                poolClient.send();
                if (connectionManager instanceof OkHttp3ConnectionManager) {
                    ConnectionPool pool = ((OkHttp3ConnectionManager) connectionManager).getOkHttpPool();
                    System.out.println("connect count = " + pool.connectionCount());
                }
                latch.countDown();
            });
        }
        latch.await();
    }

}
