package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.client.GetClient;
import com.dtflys.test.mock.Get2MockServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 15:02
 */
public class TestGet2Client extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestGet2Client.class);

    @Rule
    public Get2MockServer server = new Get2MockServer(this);

    private static ForestConfiguration configuration;

    private GetClient getClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", Get2MockServer.port);
    }


    public TestGet2Client(HttpBackend backend) {
        super(backend, configuration);
        getClient = configuration.createInstance(GetClient.class);
    }



    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    @Test
    public void testGet() {
        String result = getClient.simpleGetMultiQuery("bar");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(Get2MockServer.EXPECTED, result);
    }

    @Test
    public void testGet2() {
        String result = getClient.simpleGetMultiQuery2("foo", "bar");
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(Get2MockServer.EXPECTED, result);
    }

    @Test
    public void testGet3() {
        String result = getClient.simpleGetMultiQuery3();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(Get2MockServer.EXPECTED, result);
    }

    @Test
    public void testGet4() {
        ForestResponse<String> response = getClient.simpleGetMultiQuery4();
        assertNotNull(response);
        assertTrue(response.isSuccess());
        String result = response.getResult();
        log.info("response: " + result);
        assertEquals("http://localhost:5022/hello/user?username=foo&password=bar",
                response.getRequest().getUrl() + "?" + response.getRequest().getQueryString());
        assertNotNull(result);
        assertEquals(Get2MockServer.EXPECTED, result);
    }

    @Test
    public void testGet5() {
        ForestResponse<String> response = getClient.simpleGetMultiQuery5("foo");
        assertNotNull(response);
        assertTrue(response.isSuccess());
        String result = response.getResult();
        log.info("response: " + result);
        assertEquals("http://localhost:5022/hello/user?username=foo&username=foo&password=bar",
                response.getRequest().getUrl() + "?" + response.getRequest().getQueryString());
        assertNotNull(result);
        assertEquals(Get2MockServer.EXPECTED, result);
    }


    @Test
    public void testGetBooleanResultTrue() {
        Boolean ret = getClient.getBooleanResultTrue();
        assertTrue(ret);
    }


    @Test
    public void testGetBooleanResultFalse() {
        Boolean ret = getClient.getBooleanResultFalse();
        assertFalse(ret);
    }


    @Test
    public void testGetBooleanResultTrue2() {
        Boolean ret = getClient.getBooleanResultTrue2();
        assertTrue(ret);
    }


    @Test
    public void testGetBooleanResultFalse2() {
        Boolean ret = getClient.getBooleanResultFalse2();
        assertFalse(ret);
    }


}
