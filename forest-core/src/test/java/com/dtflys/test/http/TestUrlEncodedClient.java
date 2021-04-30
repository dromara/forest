package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.UrlEncodedClient;
import com.dtflys.test.mock.UrlEncodedMockServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestUrlEncodedClient extends BaseClientTest {

    @Rule
    public UrlEncodedMockServer server = new UrlEncodedMockServer(this);

    private static ForestConfiguration configuration;

    private UrlEncodedClient urlEncodedClient;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", UrlEncodedMockServer.port);
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    public TestUrlEncodedClient(HttpBackend backend) {
        super(backend, configuration);
        urlEncodedClient = configuration.createInstance(UrlEncodedClient.class);
    }


    @Test
    public void testGetUrlEncoded() {
        String result = urlEncodedClient.getUrlEncoded("中文", "AbcD12#$iTXI", "il&felUFO3o=P", "中文内容");
        assertEquals(UrlEncodedMockServer.EXPECTED, result);
    }

    @Test
    public void testGetUrlEncodedWithQuery() {
        String result = urlEncodedClient.getUrlEncodedWithQuery("中文", "AbcD12#$iTXI", "il&felUFO3o=P", "中文内容");
        assertEquals(UrlEncodedMockServer.EXPECTED, result);
    }

}
