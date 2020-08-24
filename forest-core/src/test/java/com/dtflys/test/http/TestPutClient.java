package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.mock.PutMockServer;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.backend.okhttp3.OkHttp3Backend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.PutClient;
import com.dtflys.test.mock.PutMockServer;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 17:13
 */
public class TestPutClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestPutClient.class);

    @Rule
    public PutMockServer server = new PutMockServer(this);

    private static ForestConfiguration configuration;

    private static PutClient putClient;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", PutMockServer.port);
    }

    public TestPutClient(HttpBackend backend) {
        super(backend, configuration);
        putClient = configuration.createInstance(PutClient.class);
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    @Test
    public void testPutHello() {
        String result = putClient.putHello();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(PutMockServer.EXPECTED, result);
    }

    @Test
    public void testSimplePut() {
        String result = putClient.simplePut();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(PutMockServer.EXPECTED, result);
    }

    @Test
    public void testSimplePut2() {
        String result = putClient.simplePut2();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(PutMockServer.EXPECTED, result);
    }

    @Test
    public void testSimplePut3() {
        String result = putClient.simplePut3();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(PutMockServer.EXPECTED, result);
    }



    @Test
    public void testTextParamPut() {
        String result = putClient.textParamPut("foo", "123456");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(PutMockServer.EXPECTED, result);
    }

    @Test
    public void testAnnParamPut() {
        String result = putClient.annParamPut("foo", "123456");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(PutMockServer.EXPECTED, result);
    }


}
