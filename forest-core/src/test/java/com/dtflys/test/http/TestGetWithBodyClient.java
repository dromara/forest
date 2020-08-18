package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestNetworkException;
import com.dtflys.test.http.client.GetWithBodyClient;
import com.dtflys.test.http.client.PostClient;
import com.dtflys.test.http.model.UserParam;
import com.dtflys.test.mock.GetMockServer;
import com.dtflys.test.mock.GetWithBodyMockServer;
import com.dtflys.test.mock.PostMockServer;
import com.dtflys.test.mock.PutMockServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 16:55
 */
public class TestGetWithBodyClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestGetWithBodyClient.class);

    @Rule
    public GetWithBodyMockServer server = new GetWithBodyMockServer(this);

    private static ForestConfiguration configuration;

    private static GetWithBodyClient getWithBodyClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", GetWithBodyMockServer.port);
    }

    public TestGetWithBodyClient(HttpBackend backend) {
        super(backend, configuration);
        getWithBodyClient = configuration.createInstance(GetWithBodyClient.class);
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    @Test
    public void testGetWithBody1() {
        if (configuration.getBackend().getName().equals("httpclient")) {
            String result = getWithBodyClient.getWithBody1("1", "foo", "123456");
            assertNotNull(result);
            assertEquals(GetWithBodyMockServer.EXPECTED, result);
        }
    }


}
