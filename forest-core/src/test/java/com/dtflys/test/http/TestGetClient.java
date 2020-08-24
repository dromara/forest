package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.model.JsonTestUser;
import com.dtflys.test.mock.GetMockServer;
import com.dtflys.test.http.client.GetClient;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 15:02
 */
public class TestGetClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestGetClient.class);

    @Rule
    public GetMockServer server = new GetMockServer(this);

    private static ForestConfiguration configuration;

    private GetClient getClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", GetMockServer.port);
    }


    public TestGetClient(HttpBackend backend) {
        super(backend, configuration);
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
    public void testGet2() {
        String result = getClient.simpleGet2();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(GetMockServer.EXPECTED, result);
    }

    @Test
    public void testGet3() {
        String result = getClient.simpleGet3();
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
    public void testAnnQueryGet() {
        String result = getClient.annQueryGet("foo");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(GetMockServer.EXPECTED, result);
    }


    @Test
    public void testAnnObjectGet() {
        JsonTestUser user = new JsonTestUser();
        user.setUsername("foo");
        String result = getClient.annObjectGet(user);
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(GetMockServer.EXPECTED, result);
    }

    @Test
    public void testAnnObjectGet2() {
        Map<String, Object> map = new HashMap<>();
        map.put("username", "foo");
        String result = getClient.annObjectGet(map);
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
