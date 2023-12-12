package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.ForestJoinpoint;
import com.dtflys.forest.interceptor.Interceptor;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-04-09 16:27
 */
@BaseRequest(
        baseURL = "http://localhost:${port}/base",
        headers = {
                "Accept-Charset: ${encoding}",
                "Accept: text/plain"
        },
        userAgent = "${userAgent}",
        connectTimeout = 3000,
        readTimeout = 4000,
        interceptor = BaseReqClient.BaseReqInterceptor.class
)
public interface BaseReqClient {

    @Request(
            url = ""
    )
    ForestResponse simpleGetWithEmptyPath(@Var("encoding") String encoding);

    @Get
    ForestRequest testBaseTimeout(@Var("encoding") String encoding);


    class BaseReqInterceptor implements Interceptor<Object> {
        @Override
        public ForestJoinpoint beforeExecute(ForestRequest request) {
            assertThat(request.getConnectTimeout()).isEqualTo(3000);
            assertThat(request.getReadTimeout()).isEqualTo(4000);
            return proceed();
        }
    }

    @Get
    String testBaseTimeoutWithInterceptor(@Var("encoding") String encoding);
    @Request(
            url = "/hello/user?username=foo"
    )
    String simpleGet(OnSuccess onSuccess, @Var("encoding") String encoding);

    @Request(
            url = "http://localhost:${port}/hello/user?username=foo"
    )
    String simpleGetWithoutBaseUrl(OnSuccess onSuccess, @Var("encoding") String encoding);


}
