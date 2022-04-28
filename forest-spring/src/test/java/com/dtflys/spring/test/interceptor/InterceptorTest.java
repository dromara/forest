package com.dtflys.spring.test.interceptor;

import com.dtflys.spring.test.component.ComponentA;
import com.dtflys.spring.test.component.ComponentB;
import junit.framework.TestCase;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class InterceptorTest extends TestCase {

    private ClassPathXmlApplicationContext applicationContext;

    public void testInterceptor() {
        applicationContext = new ClassPathXmlApplicationContext(
                new String[] {"classpath:interceptor-test.xml"});
        ComponentA componentA = applicationContext.getBean("componentA", ComponentA.class);
        assertThat(componentA).isNotNull();
        componentA.setName("XXX");
        InterceptorClient interceptorClient = applicationContext.getBean(InterceptorClient.class);
        assertThat(interceptorClient).isNotNull();
        interceptorClient.testComponentA();
        assertThat(componentA.getName()).isEqualTo("aaa");

        ComponentB componentB = applicationContext.getBean("componentB", ComponentB.class);
        assertThat(componentB).isNotNull();
        assertThat(interceptorClient).isNotNull();
        componentB.setName("xxx");
        interceptorClient.testComponentB();
        assertThat(componentB.getName()).isEqualTo("bbb");

    }
}
