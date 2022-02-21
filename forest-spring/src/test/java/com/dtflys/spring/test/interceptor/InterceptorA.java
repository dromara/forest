package com.dtflys.spring.test.interceptor;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.spring.test.component.ComponentA;
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
