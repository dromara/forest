package com.dtflys.forest.springboot.test;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.logging.LogConfiguration;
import com.dtflys.forest.springboot.annotation.ForestScan;
import com.dtflys.forest.springboot.test.logging.TestLogHandler;
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
        assertTrue(forestConfiguration.isLogEnabled());
        assertTrue(forestConfiguration.isLogRequest());
        assertTrue(!forestConfiguration.isLogResponseStatus());
        assertTrue(forestConfiguration.isLogResponseContent());
        assertTrue(forestConfiguration.getLogHandler() instanceof TestLogHandler);
        assertTrue(forestConfiguration.hasFilter("test"));
    }

    @Test
    public void testClient1() {
        StopWatch sw = new StopWatch();
        sw.start();
        ForestResponse response = baiduClient.testTimeout("xxx");
        sw.stop();
        assertNotNull(response);
        ForestRequest request = response.getRequest();
        int reqTimeout = request.getTimeout();
        assertEquals(50, reqTimeout);
        long time = sw.getTotalTimeMillis();
        assertTrue(time >= 50);
        assertTrue(time <= 900);
        LogConfiguration logConfiguration = request.getLogConfiguration();
        assertTrue(logConfiguration.isLogEnabled());
        assertTrue(logConfiguration.isLogRequest());
//        assertTrue(logConfiguration.isLogResponseStatus());
//        assertTrue(!logConfiguration.isLogResponseContent());
//        assertTrue(logConfiguration.getLogHandler() instanceof TestLogHandler);
    }

}
