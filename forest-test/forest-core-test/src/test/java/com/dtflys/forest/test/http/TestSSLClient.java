package com.dtflys.forest.test.http;

import cn.hutool.core.map.MapUtil;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.ssl.SSLKeyStore;
import com.dtflys.forest.ssl.SSLSocketFactoryBuilder;
import com.dtflys.forest.test.http.client.SSLClient;
import com.dtflys.forest.test.http.ssl.MyHostnameVerifier;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpsCertificate;
import com.github.dreamhead.moco.HttpsServer;
import com.github.dreamhead.moco.Runner;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.Runner.runner;
import static com.github.dreamhead.moco.HttpsCertificate.certificate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.assertj.core.api.Assertions.*;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-03-01 19:41
 */
public class TestSSLClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestSSLClient.class);

    @Rule
    public final MockWebServer webServer = new MockWebServer();

    private final HttpsServer server;
    private final static HttpsCertificate serverCertificate = certificate(
            pathResource("ssl_server.keystore"), "server", "123456");



    public final static String EXPECTED = "{\"status\": \"ok\", \"ssl\": \"true\"}";

    private Runner runner;

    static {
    }


    private static ForestConfiguration configuration;

    private SSLClient sslClient;

    @Override
    public void afterRequests() {
    }

    private static class MySSLSocketFactoryBuilder implements SSLSocketFactoryBuilder {

        @Override
        public SSLSocketFactory getSSLSocketFactory(ForestRequest request, String protocol) throws Exception {
            return null;
        }
    }

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
        SSLKeyStore sslKeyStore = new SSLKeyStore(
                "ssl_client",
                "ssl_client.keystore",
                "client",
                "456789",
                null,
                null,
                new MySSLSocketFactoryBuilder());
        configuration.registerKeyStore(sslKeyStore);

        SSLKeyStore sslKeyStore2 = new SSLKeyStore(
                "ssl_client2",
                "ssl_client.keystore",
                "client",
                "456789",
                null,
                new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        if ("localhost".equals(s)) {
                            return false;
                        }
                        return true;
                    }
                },
                new MySSLSocketFactoryBuilder());
        configuration.registerKeyStore(sslKeyStore2);
        configuration.registerKeyStore(new SSLKeyStore(
                "ssl_client3",
                null,
                null,
                null,
                null,
                new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        if ("localhost".equals(s)) {
                            return false;
                        }
                        return true;
                    }
                },
                null
            ));
    }


    public TestSSLClient(String backend, String jsonConverter) throws IOException {
        super(backend, jsonConverter, configuration);
        sslClient = configuration.createInstance(SSLClient.class);
        int port = webServer.getPort();
        webServer.close();
        server = httpsServer(port, serverCertificate);

        server
                .get(by(uri("/hello/user")))
                .response(
                        text(request -> {
                            HttpRequest httpRequest = (HttpRequest) request;
                            Object id = httpRequest.getQueries().get("id");
                            if (id == null) {
                                return EXPECTED;
                            }
                            return  "{\"id\": \"" + ((String[]) id)[0] + "\"}";
                        }));
        configuration.setVariableValue("port", port);
    }



    @Before
    public void prepareMockServer() {
        runner = runner(server);
        runner.start();
    }

    @After
    public void tearDown() {
        runner.stop();
    }


    @Test
    public void truestAllGet() {
        sslClient.truestAllGet();
        String result = sslClient.truestAllGet();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(EXPECTED, result);
    }

    @Test
    public void testHostVerifier() {
        sslClient.truestAllGet();
        Throwable th = null;
        String result = null;
        try {
            result = sslClient.testHostVerifier("localhost");
        } catch (ForestRuntimeException ex) {
            th = ex.getCause();
        }
        assertThat(th).isNotNull().isInstanceOf(SSLPeerUnverifiedException.class);
        th = null;
        try {
            result = sslClient.testHostVerifier("127.0.0.1");
        } catch (ForestRuntimeException ex) {
            th = ex.getCause();
        }
        assertThat(th).isNull();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testHostVerifier2() {
        sslClient.truestAllGet();
        Throwable th = null;
        String result = null;
        ForestRequest<String> request = sslClient.testHostVerifier2("localhost");
        assertThat(request.getHostnameVerifier()).isNotNull().isInstanceOf(MyHostnameVerifier.class);
        assertThat(request.getSslSocketFactoryBuilder())
                .isNotNull().
                isInstanceOf(com.dtflys.forest.test.http.ssl.MySSLSocketFactoryBuilder.class);
        try {
            result = request.executeAsString();
        } catch (ForestRuntimeException ex) {
            th = ex.getCause();
        }
        assertThat(th).isNotNull().isInstanceOf(SSLPeerUnverifiedException.class);
        th = null;
        String host = null;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            host = addr.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        request = sslClient.testHostVerifier2(host);
        assertThat(request.getHostnameVerifier()).isNotNull().isInstanceOf(MyHostnameVerifier.class);
        assertThat(request.getSslSocketFactoryBuilder())
                .isNotNull().
                isInstanceOf(com.dtflys.forest.test.http.ssl.MySSLSocketFactoryBuilder.class);
        try {
            result = request.executeAsString();
        } catch (ForestRuntimeException ex) {
            th = ex.getCause();
        }
        assertThat(th).isNull();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }



    @Test
    public void truestSSLGet() {
        ForestResponse<String> response = sslClient.truestSSLGet("SSLv3");
        ForestRequest request = response.getRequest();
        String protocol = request.getSslProtocol();
        assertNotNull(protocol);
        assertEquals("SSLv3", protocol);

        response = sslClient.truestSSLGet("TLS");
        request = response.getRequest();
        protocol = request.getSslProtocol();
        assertNotNull(protocol);
        assertEquals("TLS", protocol);

        response = sslClient.truestSSLGet(null);
        request = response.getRequest();
        protocol = request.getSslProtocol();
        assertNotNull(protocol);
        assertEquals("TLS", protocol);
    }


    @Test
    public void testConcurrent() throws InterruptedException {
        int len = 100;
        CountDownLatch latch = new CountDownLatch(len);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < len / 2; i++) {
            int finalI = i;
            executor.execute(() -> {
                ForestResponse<String> response = sslClient.testConcurrent(finalI);
                ForestRequest req = response.getRequest();
                String reqId = String.valueOf(req.getQuery("id"));
                String json = response.getContent();
                Map<String, Object> map = configuration.getJsonConverter().convertObjectToMap(json);
                String resId = MapUtil.getStr(map, "id");
                System.out.println(finalI + " -> " + reqId + " -> " + resId);
                assertThat(reqId).isEqualTo(String.valueOf(finalI));
                assertThat(resId).isEqualTo(String.valueOf(finalI));
                latch.countDown();
            });
        }
        for (int i = len / 2; i < len; i++) {
            Thread.sleep(20);
            int finalI = i;
            executor.execute(() -> {
                ForestResponse<String> response = sslClient.testConcurrent(finalI);
                ForestRequest req = response.getRequest();
                String reqId = String.valueOf(req.getQuery("id"));
                String json = response.getContent();
                Map<String, Object> map = configuration.getJsonConverter().convertObjectToMap(json);
                String resId = MapUtil.getStr(map, "id");
                System.out.println(finalI + " -> " + reqId + " -> " + resId);
                assertThat(reqId).isEqualTo(String.valueOf(finalI));
                assertThat(resId).isEqualTo(String.valueOf(finalI));
                latch.countDown();
            });
        }

        latch.await();
    }


}
