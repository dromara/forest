package com.dtflys.forest.springboot.test;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.thebeastshop.forest.springboot.annotation.ForestScan;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.springboot.test.client1.BaiduClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@ActiveProfiles("test1")
@SpringBootTest(classes = Test1.class)
@ForestScan("com.dtflys.forest.springboot.test.client1")
@EnableAutoConfiguration
public class Test1 {

    @Autowired
    private BaiduClient baiduClient;

    @Autowired
    private ForestConfiguration forestConfiguration;

    @Test
    public void testConfiguration() {
        assertEquals(Integer.valueOf(5000), forestConfiguration.getMaxConnections());
        assertEquals(Integer.valueOf(5500), forestConfiguration.getMaxRouteConnections());
        assertEquals(Integer.valueOf(50), forestConfiguration.getTimeout());
        assertEquals(Integer.valueOf(4000), forestConfiguration.getConnectTimeout());
        assertEquals("GBK", forestConfiguration.getCharset());
        assertEquals(Integer.valueOf(0), forestConfiguration.getRetryCount());
        assertTrue(forestConfiguration.isLogEnabled());
        assertEquals("okhttp3", forestConfiguration.getBackend().getName());
        assertEquals("TLSv1.2", forestConfiguration.getSslProtocol());
    }

    @Test
    public void testClient1() {
        StopWatch sw = new StopWatch();
        sw.start();
        ForestResponse response = baiduClient.testTimeout();
        sw.stop();
        assertNotNull(response);
        ForestRequest request = response.getRequest();
        int reqTimeout = request.getTimeout();
        assertEquals(50, reqTimeout);
        long time = sw.getTotalTimeMillis();
        assertTrue(time >= 50);
        assertTrue(time <= 900);
    }

}
