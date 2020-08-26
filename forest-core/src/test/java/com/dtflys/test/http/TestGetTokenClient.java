package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.GetTokenClient;
import com.dtflys.test.mock.GetTokenMockServer;
import com.dtflys.test.model.TokenResult;
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
 * @since 2017-05-11 15:02
 */
public class TestGetTokenClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestGetTokenClient.class);

    @Rule
    public GetTokenMockServer server = new GetTokenMockServer(this);

    private static ForestConfiguration configuration;

    private GetTokenClient getTokenClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", GetTokenMockServer.port);
    }


    public TestGetTokenClient(HttpBackend backend) {
        super(backend, configuration);
        getTokenClient = configuration.createInstance(GetTokenClient.class);
    }



    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    @Test
    public void testGetToken() {
        TokenResult result = getTokenClient.getToken();
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(604800L, result.getTokenTimeout());
        assertEquals("eyJjfeljlOfjelajflaFJLjlaefjl", result.getURLToken());
    }



}
