package org.forest.interceptor;

import org.forest.annotation.Request;

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
    String get();

}
