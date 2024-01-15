package com.dtflys.spring.test.interceptor;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.ForestJointPoint;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.spring.test.component.ComponentA;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class InterceptorA implements Interceptor {

    @Resource
    private ComponentA componentA;

    @Override
    public ForestJointPoint beforeExecute(ForestRequest request) {
        componentA.setName("YYY");
        return proceed();
    }
}
