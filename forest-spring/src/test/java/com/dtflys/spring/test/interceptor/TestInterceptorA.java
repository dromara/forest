package com.dtflys.spring.test.interceptor;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.spring.test.component.ComponentA;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2022-02-19 18:07
 */
@Component
public class TestInterceptorA implements Interceptor<String> {

    @Resource
    private ComponentA componentA;

    @Override
    public boolean beforeExecute(ForestRequest request) {
        componentA.setName("aaa");
        return true;
    }
}
