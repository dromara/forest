/*
package com.dtflys.spring.test;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.proxy.ForestClientProxy;
import com.dtflys.spring.test.client0.BeastshopClient;
import com.dtflys.spring.test.client1.BaiduClient;
import com.dtflys.spring.test.client2.GithubClient;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.junit.Assert.*;

*/
/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-09-25 12:19
 *//*

public class BootTest {

    @Test
    public void testBootConfig0() {
        ForestClientProxy client = (ForestClientProxy) getClient(BeastshopClient.class, BootConfig0.class);
        assertNull(client);
    }


    @Test
    public void testBootConfig1() {
        ForestClientProxy client = (ForestClientProxy) getClient(BaiduClient.class, BootConfig1.class);
        assertEquals("httpclient", client.getProxyHandler().getConfiguration().getBackend().getName());
    }

    @Test
    public void testBootConfig2() {
        ForestClientProxy client = (ForestClientProxy) getClient(GithubClient.class, BootConfig2.class);
        assertEquals("okhttp3", client.getProxyHandler().getConfiguration().getBackend().getName());
    }



    private <T> T getClient(Class<T> beanClass, Class<?>... annotatedClasses) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(annotatedClasses);
        T bean = context.getBean(beanClass);
//        assertNotNull(bean);
        return bean;
    }

    @Configuration
//    @ForestScan(basePackages = "com.dtflys.spring.test.client0")
    static class BootConfig0 {
    }


    @Configuration
//    @ForestScan(basePackages = "com.dtflys.spring.test.client1")
    static class BootConfig1 {

        @Bean
        public ForestConfiguration cnf1() {
            return ForestConfiguration.configuration().setBackendName("httpclient");
        }
    }


    @Configuration
//    @ForestScan(basePackages = "com.dtflys.spring.test.client2", configuration = "cnf1")
    static class BootConfig2 {

        @Bean
        public ForestConfiguration cnf0() {
            return ForestConfiguration.configuration().setBackendName("httpclient");
        }

        @Bean
        public ForestConfiguration cnf1() {
            return ForestConfiguration.configuration().setBackendName("okhttp3");
        }
    }



}
*/
