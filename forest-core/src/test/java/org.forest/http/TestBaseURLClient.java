package org.forest.http;

import org.forest.config.ForestConfiguration;
import org.forest.mock.GetMockServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertEquals;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 16:12
 */
public class TestBaseURLClient {


    private final static Logger log = LoggerFactory.getLogger(TestGetClient.class);

    @Rule
    public GetMockServer server = new GetMockServer(this);

    private static ForestConfiguration configuration;

    private static BaseURLClient baseURLClient;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        baseURLClient = configuration.createInstance(BaseURLClient.class);
    }


    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    @Test
    public void testBaseURL() {
        String result = baseURLClient.simpleGet();
        assertEquals(GetMockServer.EXPECTED, result);
    }

}
