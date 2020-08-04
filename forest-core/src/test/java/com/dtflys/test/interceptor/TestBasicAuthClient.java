package com.dtflys.test.interceptor;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.BaseClientTest;
import com.dtflys.test.http.TestGetClient;
import com.dtflys.test.mock.BasicAuthGetMockServer;
import com.dtflys.test.mock.GetMockServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertNotNull;

public class TestBasicAuthClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestGetClient.class);

    @Rule
    public BasicAuthGetMockServer server = new BasicAuthGetMockServer(this);

    private static ForestConfiguration configuration;

    private static BasicAuthClient basicAuthClient;


    public TestBasicAuthClient(HttpBackend backend) {
        super(backend, configuration);
        basicAuthClient = configuration.createInstance(BasicAuthClient.class);
    }

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setCacheEnabled(false);
        configuration.setVariableValue("port", GetMockServer.port);
    }

    @Test
    public void testBasicAUth() {
        String result = basicAuthClient.send("foo");
        assertNotNull(result);
    }

}
