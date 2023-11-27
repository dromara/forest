package com.dtflys.forest.springboot.test;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestAsyncMode;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.logging.DefaultLogHandler;
import com.dtflys.forest.logging.ForestLogger;
import com.dtflys.forest.retryer.BackOffRetryer;
import com.dtflys.forest.springboot.test.client0.BeastshopClient;
import com.dtflys.forest.springboot.test.client0.DisturbInterface;
import com.dtflys.forest.springboot.test.moudle.TestUser;
import com.dtflys.forest.springboot.test.service.impl.TestServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ActiveProfiles("test0")
@SpringBootTest(classes = Test0.class)
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.dtflys.forest.springboot.test.service")
public class Test0 {

    @Resource
    private BeastshopClient beastshopClient;

    @Autowired(required = false)
    private DisturbInterface disturbInterface;


    @Autowired
    private TestServiceImpl testService;

    @Resource(name = "config0")
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
        assertEquals("httpclient", config0.getBackend().getName());
        assertEquals("SSLv3", config0.getSslProtocol());
        assertTrue(config0.getLogHandler() instanceof DefaultLogHandler);
        assertEquals("https://www.thebeastshop.com/autopage", config0.getVariableValue("baseUrl"));
        assertEquals("xxx", config0.getVariableValue("myName"));
        assertNotNull(config0.getVariableValue("user"));
        assertEquals(Integer.valueOf(512), config0.getBackendClientCacheMaxSize());
        assertTrue(!config0.isLogEnabled());
        assertEquals(ForestAsyncMode.PLATFORM, config0.getAsyncMode());
        assertEquals(Integer.valueOf(12), config0.getVariableValue("myCount"));
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
                "\tGET https://www.thebeastshop.com/autopage/shops.htm HTTPS\n" +
                "\tHeaders: \n" +
                "\t\tUser-Agent: forest/dev");
        Mockito.verify(logger).info("[Forest] Request (" + backendName + "): \n" +
                "\t[Retry]: 2\n" +
                "\tGET https://www.thebeastshop.com/autopage/shops.htm HTTPS\n" +
                "\tHeaders: \n" +
                "\t\tUser-Agent: forest/dev");
        Mockito.verify(logger).info("[Forest] Request (" + backendName + "): \n" +
                "\t[Retry]: 3\n" +
                "\tGET https://www.thebeastshop.com/autopage/shops.htm HTTPS\n" +
                "\tHeaders: \n" +
                "\t\tUser-Agent: forest/dev");
        Mockito.verify(logger).info("[Forest] Request (" + backendName + "): \n" +
                "\t[Retry]: 4\n" +
                "\tGET https://www.thebeastshop.com/autopage/shops.htm HTTPS\n" +
                "\tHeaders: \n" +
                "\t\tUser-Agent: forest/dev");
        Mockito.verify(logger).info("[Forest] Request (" + backendName + "): \n" +
                "\t[Retry]: 5\n" +
                "\tGET https://www.thebeastshop.com/autopage/shops.htm HTTPS\n" +
                "\tHeaders: \n" +
                "\t\tUser-Agent: forest/dev");
//        Mockito.verify(logger).info("[Forest] [Network Error]: connect timed out");

    }

}
