package org.forest.test.http;

import org.forest.backend.HttpBackend;
import org.forest.config.ForestConfiguration;
import org.forest.test.http.client.SSLClient;
import org.forest.test.mock.GetMockServer;
import org.forest.test.mock.SSLMockServer;
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
 * @since 2018-03-01 19:41
 */
public class TestSSLClient extends BaseClientTest {


    private final static Logger log = LoggerFactory.getLogger(TestSSLClient.class);

    @Rule
    public SSLMockServer server = new SSLMockServer(this);

    private static ForestConfiguration configuration;

    private SSLClient sslClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
    }


    public TestSSLClient(HttpBackend backend) {
        super(backend, configuration);
        sslClient = configuration.createInstance(SSLClient.class);
    }



    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    @Test
    public void truestAllGet() {
        String result = sslClient.truestAllGet();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(GetMockServer.EXPECTED, result);
    }

}
