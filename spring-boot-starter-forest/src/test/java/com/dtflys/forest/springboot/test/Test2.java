package com.dtflys.forest.springboot.test;

import com.dtflys.forest.annotation.ForestScan;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.interceptor.SpringInterceptorFactory;
import com.dtflys.forest.springboot.test.client1.BaiduClient;
import com.dtflys.forest.springboot.test.client2.GithubClient;
import com.dtflys.forest.springboot.test.interceptor.GlobalInterceptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@ActiveProfiles("test2")
@SpringBootTest(classes = Test2.class)
@ComponentScan(basePackageClasses = GlobalInterceptor.class)
@ForestScan(basePackageClasses = GithubClient.class)
@EnableAutoConfiguration
public class Test2 {

    @Autowired
    private GithubClient githubClient;

    @Autowired
    private ForestConfiguration forestConfiguration;

    @Test
    public void testConfiguration() {
        assertEquals("okhttp3", forestConfiguration.getBackendName());
        assertEquals(Integer.valueOf(6000), forestConfiguration.getMaxConnections());
        assertEquals(Integer.valueOf(6600), forestConfiguration.getMaxRouteConnections());
        assertEquals(Integer.valueOf(6000), forestConfiguration.getTimeout());
        assertEquals(Integer.valueOf(5000), forestConfiguration.getConnectTimeout());
        assertEquals(Integer.valueOf(0), forestConfiguration.getRetryCount());
        assertEquals(SpringInterceptorFactory.class, forestConfiguration.getInterceptorFactory().getClass());
        assertEquals(1, forestConfiguration.getInterceptors().size());
        assertEquals(GlobalInterceptor.class, forestConfiguration.getInterceptors().get(0));
    }

    @Test
    public void testClient2() {
        String result = githubClient.index();
        assertNotNull(result);
        assertTrue(result.startsWith("Global: "));
    }

}
