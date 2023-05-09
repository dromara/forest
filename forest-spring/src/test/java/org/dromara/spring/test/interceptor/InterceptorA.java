package org.dromara.spring.test.interceptor;

import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.interceptor.Interceptor;
import org.dromara.spring.test.component.ComponentA;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class InterceptorA implements Interceptor<String> {

    @Resource
    private ComponentA componentA;

    @Override
    public boolean beforeExecute(ForestRequest request) {
        componentA.setName("YYY");
        return true;
    }
}
