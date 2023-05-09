package org.dromara.forest.core.test.http;

import org.dromara.forest.backend.HttpBackend;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.core.test.http.client.GetWithBodyClient;
import org.dromara.forest.core.test.mock.GetWithBodyMockServer;
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
    }

    @Override
    public void afterRequests() {
    }

    public TestGetWithBodyClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
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
