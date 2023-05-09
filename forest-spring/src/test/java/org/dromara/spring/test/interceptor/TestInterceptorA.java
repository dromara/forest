package org.dromara.spring.test.interceptor;

import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.interceptor.Interceptor;
import org.dromara.spring.test.component.ComponentA;
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
