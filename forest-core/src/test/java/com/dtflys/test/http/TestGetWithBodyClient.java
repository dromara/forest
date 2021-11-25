package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.GetWithBodyClient;
import com.dtflys.test.mock.GetWithBodyMockServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 16:55
 */
public class TestGetWithBodyClient extends BaseClientTest {

    @Rule
    public GetWithBodyMockServer server = new GetWithBodyMockServer(this);

    private static ForestConfiguration configuration;

    private static GetWithBodyClient getWithBodyClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
        configuration.setVariableValue("port", GetWithBodyMockServer.port);
    }

    @Override
    public void afterRequests() {
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
            String result = getWithBodyClient.getWithBody1("1", "foo", "123456");
            assertNotNull(result);
            assertEquals(GetWithBodyMockServer.EXPECTED, result);
    }


}
