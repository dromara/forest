package com.dtflys.spring.test.interceptor;

import com.dtflys.forest.annotation.Get;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2022-02-19 18:11
 */
public interface InterceptorClient {

    @Get(url = "http://www.baidu.com", interceptor = TestInterceptor.class)
    String testValue();

}
