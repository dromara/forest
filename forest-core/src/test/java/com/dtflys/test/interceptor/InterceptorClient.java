package com.dtflys.test.interceptor;

import com.dtflys.forest.annotation.Request;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-15 17:00
 */
public interface InterceptorClient {

    @Request(
            url = "http://localhost:5000/hello/user?username=foo",
            headers = {"Accept:text/plan"},
            interceptor = SimpleInterceptor.class
    )
    String simple();

    @Request(
            url = "http://localhost:5000/hello/user?username=foo",
            headers = {"Accept:text/plan"},
            interceptor = {SimpleInterceptor.class, Simple2Interceptor.class}
    )
    String multiple();


    @Request(
            url = "http://localhost:5000/hello/user?username=foo",
            headers = {"Accept:text/plan"},
            interceptor = FalseInterceptor.class
    )
    String beforeFalse();


}
