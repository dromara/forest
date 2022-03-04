package com.dtflys.spring.test.interceptor;

import com.dtflys.spring.test.component.ComponentA;
import junit.framework.TestCase;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class InterceptorTest extends TestCase {

    private ClassPathXmlApplicationContext applicationContext;

    public void testInterceptor() {
        applicationContext = new ClassPathXmlApplicationContext(
                new String[] { "classpath:interceptor-test.xml" });
        ComponentA componentA = applicationContext.getBean(ComponentA.class);
        assertThat(componentA).isNotNull();
        componentA.setName("XXX");
        InterceptorClient interceptorClient = applicationContext.getBean(InterceptorClient.class);
        assertThat(interceptorClient).isNotNull();
        interceptorClient.index();
        assertThat(componentA.getName()).isEqualTo("YYY");
    }
}
