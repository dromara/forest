package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.ssl.SSLKeyStore;
import com.dtflys.forest.ssl.SSLSocketFactoryBuilder;
import com.dtflys.test.mock.GetMockServer;
import com.github.dreamhead.moco.HttpsCertificate;
import com.github.dreamhead.moco.HttpsServer;
import com.github.dreamhead.moco.Runner;
import com.dtflys.test.http.client.GetClient;
import com.dtflys.test.http.client.SSLClient;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

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

    private static HttpsServer server;
    private final static HttpsCertificate serverCertificate = certificate(
            pathResource("ssl_server.keystore"), "server", "123456");

    @Rule
    public GetMockServer getServer = new GetMockServer(this);

    private GetClient getClient;


    public final static String EXPECTED = "{\"status\": \"ok\", \"ssl\": \"true\"}";

    private Runner runner;

    static {
        server = httpsServer(5555, serverCertificate);
        server
                .get(by(uri("/hello/user")))
                .response(EXPECTED);
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
                new MySSLSocketFactoryBuilder());
        configuration.registerKeyStore(sslKeyStore);

        SSLKeyStore sslKeyStore2 = new SSLKeyStore(
                "ssl_client2",
                "ssl_client.keystore",
                "client",
                "456789",
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
    }


    public TestSSLClient(HttpBackend backend) {
        super(backend, configuration);
        sslClient = configuration.createInstance(SSLClient.class);
        getClient = configuration.createInstance(GetClient.class);
    }



    @Before
    public void prepareMockServer() {
        runner = runner(server);
        runner.start();
        getServer.initServer();
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
    public void truestSSLGet() {
        ForestResponse<String> response = sslClient.truestSSLGet("SSLv3");
        ForestRequest request = response.getRequest();
        String protocol = request.getSslProtocol();
        assertNotNull(protocol);
        assertEquals("SSLv3", protocol);

        response = sslClient.truestSSLGet("TLSv1.3");
        request = response.getRequest();
        protocol = request.getSslProtocol();
        assertNotNull(protocol);
        assertEquals("TLSv1.3", protocol);

        response = sslClient.truestSSLGet(null);
        request = response.getRequest();
        protocol = request.getSslProtocol();
        assertNotNull(protocol);
        assertEquals("TLS", protocol);
    }



    @Test
    public void mixAllGet() {
/*
        for (int i = 0; i < 10; i++) {
            String simpleResult = getClient.simpleGet();
            assertEquals(GetMockServer.EXPECTED, simpleResult);
            String result = sslClient.truestAllGet();
            log.info("response: " + result);
            assertNotNull(result);
            assertEquals(EXPECTED, result);
        }
*/
    }


}
