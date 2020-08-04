package com.dtflys.test.interceptor;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.BaseClientTest;
import com.dtflys.test.http.TestGetClient;
import com.dtflys.test.mock.BasicAuthGetMockServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class TestBaseAuthClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestGetClient.class);

    @Rule
    public BasicAuthGetMockServer server = new BasicAuthGetMockServer(this);

    private static ForestConfiguration configuration;

    private static BaseAuthClient baseAuthClient;


    public TestBaseAuthClient(HttpBackend backend) {
        super(backend, configuration);
        baseAuthClient = configuration.createInstance(BaseAuthClient.class);
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setCacheEnabled(false);
        configuration.setVariableValue("port", BasicAuthGetMockServer.port);
    }

    @Test
    public void testBaseAuth() {
        String result = baseAuthClient.send("foo");
        assertNotNull(result);
        assertEquals(BasicAuthGetMockServer.EXPECTED, result);

        result = baseAuthClient.send2("foo");
        assertNotNull(result);
        assertEquals(BasicAuthGetMockServer.EXPECTED, result);
    }

}
