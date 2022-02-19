package com.dtflys.spring.test.interceptor;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.spring.test.component.ComponentA;
import junit.framework.TestCase;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2022-02-19 18:13
 */
public class TestInterceptorClient extends TestCase {

    private ClassPathXmlApplicationContext applicationContext;

    public void testValue() {
        applicationContext = new ClassPathXmlApplicationContext(
                new String[] { "classpath:interceptor-test.xml" });
        ComponentA componentA = applicationContext.getBean(ComponentA.class);
        assertThat(componentA).isNotNull();
        InterceptorClient interceptorClient = applicationContext.getBean(InterceptorClient.class);
        assertThat(interceptorClient).isNotNull();
        componentA.setName("xxx");
        interceptorClient.testValue();
        assertThat(componentA.getName()).isEqualTo("yyy");
    }

}
