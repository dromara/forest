package com.dtflys.spring.test.interceptor;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.ForestJointPoint;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.spring.test.component.ComponentB;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2022-02-19 18:07
 */
@Component
public class TestInterceptorB implements Interceptor<String> {

    @Resource
    private ComponentB componentB;

    @Override
    public ForestJointPoint beforeExecute(ForestRequest request) {
        componentB.setName("bbb");
        return proceed();
    }
}
