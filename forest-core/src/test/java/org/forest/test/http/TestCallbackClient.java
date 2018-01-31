package org.forest.http;

import org.forest.callback.OnSuccess;
import org.forest.config.ForestConfiguration;
import org.forest.http.client.CallbackClient;
import org.forest.test.mock.GetMockServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-06-06 15:56
 */
public class TestCallbackClient {

    private final static Logger log = LoggerFactory.getLogger(TestCallbackClient.class);

    @Rule
    public GetMockServer server = new GetMockServer(this);

    private static ForestConfiguration configuration;

    private static CallbackClient callbackClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        callbackClient = configuration.createInstance(CallbackClient.class);
    }


    @Before
    public void prepareMockServer() {
        server.initServer();
    }


    @Test
    public void testGetOnSuccess() {
        callbackClient.getOnSuccess("foo", new OnSuccess<String>() {
            @Override
            public void onSuccess(String data, ForestRequest request, ForestResponse response) {
                log.info("response: " + data);
                assertNotNull(data);
                assertEquals(GetMockServer.EXPECTED, data);
            }
        });
    }


    @Test
    public void testGetOnSuccessMap() {
        callbackClient.getOnSuccessMap("foo", new OnSuccess<Map>() {
            @Override
            public void onSuccess(Map data, ForestRequest request, ForestResponse response) {
                log.info("response: " + data);
                assertNotNull(data);
                assertEquals("ok", data.get("status"));
            }
        });
    }

}
