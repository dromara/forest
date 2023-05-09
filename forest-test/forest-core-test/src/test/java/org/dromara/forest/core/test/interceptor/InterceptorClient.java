package org.dromara.forest.core.test.interceptor;

import org.dromara.forest.annotation.BaseRequest;
import org.dromara.forest.annotation.Post;
import org.dromara.forest.annotation.Request;


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
