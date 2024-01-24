package com.dtflys.forest.springboot3.test;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestAsyncMode;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.logging.LogConfiguration;
import com.dtflys.forest.springboot3.test.client1.BaiduClient;
import com.dtflys.forest.springboot3.test.logging.TestLogHandler;
import com.dtflys.forest.utils.ForestDataType;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@ActiveProfiles("test1")
@SpringBootTest(classes = Test1.class)
@EnableAutoConfiguration
public class Test1 {

    @Resource
    private BaiduClient baiduClient;

    @Resource
    private ForestConfiguration forestConfiguration;

    @Test
    public void testConfiguration() {
        assertEquals(Integer.valueOf(5000), forestConfiguration.getMaxConnections());
        assertEquals(Integer.valueOf(5500), forestConfiguration.getMaxRouteConnections());
        assertEquals(Integer.valueOf(50), forestConfiguration.getTimeout());
        assertEquals("GBK", forestConfiguration.charset());
        assertEquals(Integer.valueOf(0), forestConfiguration.maxRetryCount());
        assertTrue(forestConfiguration.isLogEnabled());
        assertEquals("okhttp3", forestConfiguration.getBackend().getName());
        assertEquals("TLSv1.2", forestConfiguration.getSslProtocol());
        assertTrue(forestConfiguration.isLogEnabled());
        assertTrue(forestConfiguration.isLogRequest());
        assertTrue(!forestConfiguration.isLogResponseStatus());
        assertTrue(forestConfiguration.isLogResponseContent());
        assertTrue(forestConfiguration.getLogHandler() instanceof TestLogHandler);
        assertTrue(forestConfiguration.hasFilter("test"));
        assertNotNull(forestConfiguration.getConverter(ForestDataType.BINARY));
        assertEquals(ForestAsyncMode.KOTLIN_COROUTINE, forestConfiguration.getAsyncMode());
    }

    @Test
    public void testClient1() {
        StopWatch sw = new StopWatch();
        sw.start();
        ForestResponse response = baiduClient.testTimeout("xxx");
        sw.stop();
        assertNotNull(response);
        ForestRequest request = response.getRequest();
        assertEquals(ForestAsyncMode.KOTLIN_COROUTINE, request.asyncMode());
        int reqTimeout = request.getTimeout();
        assertEquals(50, reqTimeout);
        long time = sw.getTotalTimeMillis();
        assertTrue(time >= 50);
        assertTrue(time <= 3600);
        LogConfiguration logConfiguration = request.getLogConfiguration();
        assertTrue(logConfiguration.isLogEnabled());
        assertTrue(logConfiguration.isLogRequest());
//        assertTrue(logConfiguration.isLogResponseStatus());
//        assertTrue(!logConfiguration.isLogResponseContent());
//        assertTrue(logConfiguration.getLogHandler() instanceof TestLogHandler);
    }

}
