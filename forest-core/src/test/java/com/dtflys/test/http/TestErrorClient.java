package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.client.GetClient;
import com.dtflys.test.mock.ErrorMockServer;
import com.dtflys.test.mock.GetMockServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 15:02
 */
public class TestErrorClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestErrorClient.class);

    @Rule
    public ErrorMockServer server = new ErrorMockServer(this);

    private static ForestConfiguration configuration;

    private GetClient getClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", ErrorMockServer.port);
    }


    public TestErrorClient(HttpBackend backend) {
        super(backend, configuration);
        getClient = configuration.createInstance(GetClient.class);
    }



    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    @Test
    public void testGet() {
        AtomicReference<String> content = new AtomicReference<>(null);
        String result = getClient.errorGet((ex, request, response) -> {
            content.set(response.getContent());
        });
        String str = content.get();
        assertNotNull(str);
        log.info("response: " + str);
        assertEquals(ErrorMockServer.EXPECTED, str);
        assertEquals(ErrorMockServer.EXPECTED, result);
    }

    @Test
    public void testGet2() {
        ForestResponse<String> response = getClient.errorGet2();
        assertNotNull(response);
        String result = response.getResult();
        assertNotNull(result);
        log.info("response: " + result);
        assertEquals(ErrorMockServer.EXPECTED, result);
    }



}
