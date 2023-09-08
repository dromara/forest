package org.dromara.forest.solon.test;

import cn.hutool.core.date.StopWatch;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.http.ForestAsyncMode;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.logging.LogConfiguration;

import org.dromara.forest.solon.test.client1.BaiduClient;
import org.dromara.forest.solon.test.logging.TestLogHandler;
import org.dromara.forest.utils.ForestDataType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.AppContext;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;


import static org.junit.Assert.*;


@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(env = "test1")
public class Test1 {

    @Inject
    AppContext aopContext;

    @Inject
    private BaiduClient baiduClient;

    @Inject
    private ForestConfiguration forestConfiguration;

    @Test
    public void testConfiguration() {
        assertEquals(Integer.valueOf(5000), forestConfiguration.getMaxConnections());
        assertEquals(Integer.valueOf(5500), forestConfiguration.getMaxRouteConnections());
        assertEquals(Integer.valueOf(50), forestConfiguration.getTimeout());
        assertEquals("GBK", forestConfiguration.getCharset());
        assertEquals(Integer.valueOf(0), forestConfiguration.getMaxRetryCount());
        assertTrue(forestConfiguration.isLogEnabled());
        Assert.assertEquals("okhttp3", forestConfiguration.getBackend().getName());
        assertEquals("TLSv1.2", forestConfiguration.getSslProtocol());
        assertTrue(forestConfiguration.isLogEnabled());
        assertTrue(forestConfiguration.isLogRequest());
        assertTrue(!forestConfiguration.isLogResponseStatus());
        assertTrue(forestConfiguration.isLogResponseContent());
        assertTrue(forestConfiguration.getLogHandler() instanceof TestLogHandler);
        assertTrue(forestConfiguration.hasFilter("test"));
        assertNotNull(forestConfiguration.getConverter(ForestDataType.BINARY));
        Assert.assertEquals(ForestAsyncMode.KOTLIN_COROUTINE, forestConfiguration.getAsyncMode());
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
