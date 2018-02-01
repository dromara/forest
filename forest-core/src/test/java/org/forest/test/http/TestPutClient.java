package org.forest.test.http;

import org.forest.config.ForestConfiguration;
import org.forest.test.http.client.PutClient;
import org.forest.test.mock.PutMockServer;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 17:13
 */
public class TestPutClient {

    private final static Logger log = LoggerFactory.getLogger(TestPutClient.class);

    @Rule
    public PutMockServer server = new PutMockServer(this);

    private static ForestConfiguration configuration;

    private static PutClient putClient;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        putClient = configuration.createInstance(PutClient.class);
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    @Test
    public void testSimplePut() {
        String result = putClient.simplePut();
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
