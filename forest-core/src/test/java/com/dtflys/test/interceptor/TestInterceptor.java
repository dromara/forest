package com.dtflys.test.interceptor;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.test.http.BaseClientTest;
import com.dtflys.test.mock.GetMockServer;
import com.dtflys.forest.interceptor.DefaultInterceptorFactory;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.test.http.TestGetClient;
import com.dtflys.forest.interceptor.Interceptor;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.*;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-18 16:57
 */
public class TestInterceptor extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestGetClient.class);

    @Rule
    public GetMockServer server = new GetMockServer(this);

    private static ForestConfiguration configuration;

    private static InterceptorClient interceptorClient;

    private static BaseInterceptorClient baseInterceptorClient;

    public TestInterceptor(HttpBackend backend) {
        super(backend, configuration);
        interceptorClient = configuration.createInstance(InterceptorClient.class);
        baseInterceptorClient = configuration.createInstance(BaseInterceptorClient.class);
    }

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setCacheEnabled(false);
        configuration.setVariableValue("port", GetMockServer.port);
    }


    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    @Test
    public void testSimpleInterceptor() {
        String result = interceptorClient.simple();
        assertNotNull(result);
        assertEquals("XX: " + GetMockServer.EXPECTED, result);
    }

    @Test
    public void testMultipleInterceptor() {
        String result = interceptorClient.multiple();
        assertNotNull(result);
        assertEquals("YY: XX: " + GetMockServer.EXPECTED, result);
    }

    @Test
    public void testWrongInterceptorClass() {
        boolean error = false;
        try {
            configuration.createInstance(WrongInterceptorClient.class);
        } catch (ForestRuntimeException e) {
            error = true;
            log.error(e.getMessage());
            assertEquals("Class [" + DefaultInterceptorFactory.class.getName() + "] is not a implement of [" +
                    Interceptor.class.getName() + "] interface.", e.getMessage());
        }
        assertTrue(error);
    }

    @Test
    public void testFalseInterceptor() {
        String result = interceptorClient.beforeFalse("a");
        assertNull(result);
    }


    @Test
    public void testBaseNoneInterceptor() {
        String result = baseInterceptorClient.none();
        assertNotNull(result);
        assertEquals("Base: " + GetMockServer.EXPECTED, result);
    }

    @Test
    public void testBaseSimpleInterceptor() {
        String result = baseInterceptorClient.simple();
        assertNotNull(result);
        assertEquals("XX: Base: " + GetMockServer.EXPECTED, result);
    }

}
