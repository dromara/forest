package org.dromara.forest.test.interceptor;

import org.dromara.forest.interceptor.DefaultInterceptorFactory;
import org.dromara.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-18 18:10
 */
public interface WrongInterceptorClient {

    @Request(
            url = "http://localhost:5000/hello/user?username=foo",
            headers = {"Accept:text/plain"},
            interceptor = DefaultInterceptorFactory.class
    )
    String wrongClass();

}
