package org.dromara.forest.solon.test;

import org.dromara.forest.backend.HttpBackend;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestAsyncMode;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.logging.DefaultLogHandler;
import org.dromara.forest.logging.ForestLogger;
import org.dromara.forest.retryer.BackOffRetryer;

import org.dromara.forest.solon.test.client0.BeastshopClient;
import org.dromara.forest.solon.test.client0.DisturbInterface;
import org.dromara.forest.solon.test.moudle.TestUser;
import org.dromara.forest.solon.test.service.impl.TestServiceImpl;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.AppContext;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(env = "test0")
public class Test0 {

    @Inject
    AppContext aopContext;

    @Inject
    private BeastshopClient beastshopClient;

    @Inject(required = false)
    private DisturbInterface disturbInterface;


    @Inject
    private TestServiceImpl testService;

    @Inject("config0")
    private ForestConfiguration config0;

    @Test
    public void testScanFilter() {
        assertNull(disturbInterface);
    }

    @Test
    public void testConfiguration() {
        assertEquals(Integer.valueOf(1200), config0.getMaxConnections());
        assertEquals(Integer.valueOf(1100), config0.getMaxRouteConnections());
        assertEquals(Integer.valueOf(1000), config0.getTimeout());
        assertEquals("UTF-8", config0.getCharset());
        assertEquals(Integer.valueOf(5), config0.getMaxRetryCount());
        Assert.assertEquals("httpclient", config0.getBackend().getName());
        assertEquals("SSLv3", config0.getSslProtocol());
        assertTrue(config0.getLogHandler() instanceof DefaultLogHandler);
        assertEquals("https://www.thebeastshop.com/autopage", config0.getVariableValue("baseUrl"));
        assertEquals("xxx", config0.getVariableValue("myName"));
        assertNotNull(config0.getVariableValue("user"));
        assertTrue(!config0.isLogEnabled());
        Assert.assertEquals(ForestAsyncMode.PLATFORM, config0.getAsyncMode());
        assertEquals("12", config0.getVariableValue("myCount"));
        assertEquals(BackOffRetryer.class, config0.getRetryer());
        assertEquals(Integer.valueOf(5), config0.getMaxRetryCount());
        assertEquals(Long.valueOf(2000), Long.valueOf(config0.getMaxRetryInterval()));

    }

    @Test
    public void testClient0() {
        ForestResponse<String> response = beastshopClient.shops("xxx");
        assertNotNull(response);
        assertNotNull(response.getContent());
        ForestRequest request = response.getRequest();
        assertNotNull(request);
        assertEquals(ForestAsyncMode.PLATFORM, request.asyncMode());
        assertEquals("www.thebeastshop.com", request.getHost());
        assertEquals("/static/stores", request.getPath());
    }

    @Test
    public void testServiceClient0() {
        beastshopClient.shops();
        ForestResponse<String> response = testService.shops();
        assertNotNull(response);
        assertNotNull(response.getResult());
        ForestRequest request = response.getRequest();
        assertNotNull(request);
        String name = request.getHeaderValue("MyName");
        String pass = request.getHeaderValue("MyPass");
        assertEquals("foo", name);
        assertEquals("bar", pass);
    }


    @Test
    public void testBug() {
        String result = beastshopClient.testBug(1);
        assertNotNull(result);
    }


    @Test
    public void testBug2() {
        TestUser user = new TestUser();
        user.setUsername("foo");
        user.setPassword("bar");
        try {
            beastshopClient.testBug2(user);
        } catch (Throwable th) {
        }
    }



    @Test
    public void testRetry() {
        ForestLogger logger = Mockito.mock(ForestLogger.class);
        config0.getLogHandler().setLogger(logger);
        try {
            beastshopClient.testRetry();
        } catch (ForestRuntimeException e) {
        }
        HttpBackend backend = config0.getBackend();
        String backendName = backend.getName();
        Mockito.verify(logger).info("[Forest] Request (" + backendName + "): \n" +
                "\t[Retry]: 1\n" +
                "\tGET https://www.thebeastshop.com/autopage/shops.htm HTTPS");
        Mockito.verify(logger).info("[Forest] Request (" + backendName + "): \n" +
                "\t[Retry]: 2\n" +
                "\tGET https://www.thebeastshop.com/autopage/shops.htm HTTPS");
        Mockito.verify(logger).info("[Forest] Request (" + backendName + "): \n" +
                "\t[Retry]: 3\n" +
                "\tGET https://www.thebeastshop.com/autopage/shops.htm HTTPS");
        Mockito.verify(logger).info("[Forest] Request (" + backendName + "): \n" +
                "\t[Retry]: 4\n" +
                "\tGET https://www.thebeastshop.com/autopage/shops.htm HTTPS");
        Mockito.verify(logger).info("[Forest] Request (" + backendName + "): \n" +
                "\t[Retry]: 5\n" +
                "\tGET https://www.thebeastshop.com/autopage/shops.htm HTTPS");
//        Mockito.verify(logger).info("[Forest] [Network Error]: connect timed out");

    }

}
