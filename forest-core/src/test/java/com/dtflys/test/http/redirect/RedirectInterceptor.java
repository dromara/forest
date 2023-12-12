package com.dtflys.test.http.redirect;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-18 23:43
 */
public class RedirectInterceptor implements Interceptor {

    public final static String BODY = "redirection";

    @Override
    public void onRedirection(ForestRequest<?> redirectReq, ForestRequest<?> prevReq, ForestResponse<?> prevRes) {
        redirectReq.addBody("body", BODY);
    }
}
