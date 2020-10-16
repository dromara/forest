package com.dtflys.forest.springboot.test;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.logging.DefaultLogHandler;
import com.dtflys.forest.retryer.BackOffRetryer;
import com.thebeastshop.forest.springboot.annotation.ForestScan;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.springboot.test.client0.BeastshopClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test0")
@SpringBootTest(classes = Test0.class)
@ForestScan(basePackages = "com.dtflys.forest.springboot.test.client0")
@EnableAutoConfiguration
public class Test0 {

    @Autowired
    private BeastshopClient beastshopClient;

    @Resource(name = "config0")
    private ForestConfiguration config0;

    @Test
    public void testConfiguration() {
        assertEquals(Integer.valueOf(1200), config0.getMaxConnections());
        assertEquals(Integer.valueOf(1100), config0.getMaxRouteConnections());
        assertEquals(Integer.valueOf(1000), config0.getTimeout());
        assertEquals(Integer.valueOf(1123), config0.getConnectTimeout());
        assertEquals("UTF-8", config0.getCharset());
        assertEquals(Integer.valueOf(5), config0.getRetryCount());
        assertEquals("okhttp3", config0.getBackend().getName());
        assertEquals("SSLv3", config0.getSslProtocol());
        assertTrue(config0.getLogHandler() instanceof DefaultLogHandler);
        assertEquals("http://www.thebeastshop.com", config0.getVariableValue("baseUrl"));
        assertEquals("xxx", config0.getVariableValue("myName"));
        assertNotNull(config0.getVariableValue("user"));
        assertTrue(!config0.isLogEnabled());
        assertEquals(Integer.valueOf(12), config0.getVariableValue("myCount"));
        assertEquals(BackOffRetryer.class, config0.getRetryer());
        assertEquals(Integer.valueOf(5), config0.getRetryCount());
        assertEquals(Long.valueOf(2000), Long.valueOf(config0.getMaxRetryInterval()));

    }

    @Test
    public void testClient0() {
        ForestResponse<String> response = beastshopClient.shops();
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


}
