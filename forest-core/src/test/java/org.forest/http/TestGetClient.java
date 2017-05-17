package org.forest.http;

import org.forest.config.ForestConfiguration;
import org.forest.mock.GetMockServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.junit.MockServerRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 15:02
 */
public class TestGetClient {

    private final static Logger log = LoggerFactory.getLogger(TestGetClient.class);

    @Rule
    public GetMockServer server = new GetMockServer(this);

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
    public void testGet() {
        String result = getClient.simpleGet();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(GetMockServer.EXPECTED, result);
    }


    @Test
    public void testJsonMapGet() {
        Map map = getClient.jsonMapGet();
        assertNotNull(map);
        assertEquals("ok", map.get("status"));
    }


    @Test
    public void testTextParamGet() {
        String result = getClient.textParamGet("foo");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(GetMockServer.EXPECTED, result);
    }


    @Test
    public void testTextParamInPathGet() {
        String result = getClient.textParamInPathGet("foo");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(GetMockServer.EXPECTED, result);
    }



    @Test
    public void testAnnParamGet() {
        String result = getClient.annParamGet("foo");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(GetMockServer.EXPECTED, result);
    }

    @Test
    public void testVarParamGet() {
        String result = getClient.varParamGet("foo");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(GetMockServer.EXPECTED, result);
    }


}
