package org.dromara.forest.spring.test.interceptor;

import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.interceptor.Interceptor;
import org.dromara.forest.spring.test.component.ComponentB;
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
    public boolean beforeExecute(ForestRequest request) {
        componentB.setName("bbb");
        return true;
    }
}
