package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.client.OptionsClient;
import com.dtflys.test.http.client.TraceClient;
import com.dtflys.test.mock.OptionsMockServer;
import com.dtflys.test.mock.TraceMockServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 18:26
 */
public class TestTraceClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestTraceClient.class);


    @Rule
    public TraceMockServer server = new TraceMockServer(this);

    private static ForestConfiguration configuration;

    private static TraceClient traceClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
    }

    public TestTraceClient(HttpBackend backend) {
        super(backend, configuration);
        traceClient = configuration.createInstance(TraceClient.class);
    }


    @Before
    public void prepareMockServer() {
        server.initServer();
    }


    @Test
    public void testSimpleOptions() {
        ForestResponse response = traceClient.simpleTrace();
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
    }


    @Test
    public void testTextParamOptions() {
        String result = traceClient.textParamTrace("foo");
        assertNotNull(result);
        assertEquals(OptionsMockServer.EXPECTED, result);
    }

    @Test
    public void testAnnParamOptions() {
        String result = traceClient.annParamTrace("foo");
        assertNotNull(result);
        assertEquals(OptionsMockServer.EXPECTED, result);
    }

}
