package com.dtflys.forest.test.interceptor;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Request;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-15 17:00
 */
@BaseRequest(interceptor = BaseErrorInterceptor.class)
public interface InterceptorClient {

    @Post(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {"Accept:text/plain"},
            interceptor = SimpleInterceptor.class
    )
    String simple();

    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {"Accept:text/plain"},
            interceptor = {SimpleInterceptor.class, Simple2Interceptor.class}
    )
    String multiple();


    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {"Accept:text/plain"},
            interceptor = FalseInterceptor.class
    )
    String beforeFalse(String arg);


}
