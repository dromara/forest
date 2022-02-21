package com.dtflys.spring.test.interceptor;

import com.dtflys.forest.annotation.Get;

public interface InterceptorClient {

    @Get(url = "http://www.baidu.com", interceptor = InterceptorA.class)
    String index();
}
