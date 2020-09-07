package com.dtflys.test.interceptor;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.BaseClientTest;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestBaseErrorClient extends BaseClientTest {

    private static ForestConfiguration configuration;

    private static BaseErrorInterceptorClient baseErrorInterceptorClient;


    public TestBaseErrorClient(HttpBackend backend) {
        super(backend, configuration);
        baseErrorInterceptorClient = configuration.createInstance(BaseErrorInterceptorClient.class);
    }


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setCacheEnabled(false);
    }
    
    @Test
    public void testBaseError() {
        ForestResponse<String> result = baseErrorInterceptorClient.testError();
        assertNotNull(result);
        assertEquals("Base OnError is OK", result.getResult());
    }

}
