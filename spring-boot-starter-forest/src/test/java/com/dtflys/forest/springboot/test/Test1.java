package com.dtflys.forest.springboot.test;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


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
        assertEquals(Integer.valueOf(5000), forestConfiguration.getTimeout());
        assertEquals(Integer.valueOf(4000), forestConfiguration.getConnectTimeout());
        assertEquals(Integer.valueOf(0), forestConfiguration.getRetryCount());
        assertEquals("httpclient", forestConfiguration.getBackend().getName());
        assertEquals("TLSv1.2", forestConfiguration.getSslProtocol());
    }

    @Test
    public void testClient1() {
        String result = baiduClient.index();
        assertNotNull(result);
    }

}
