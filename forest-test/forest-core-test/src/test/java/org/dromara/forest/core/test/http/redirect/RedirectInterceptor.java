package org.dromara.forest.core.test.http.redirect;

import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.interceptor.Interceptor;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-18 23:43
 */
public class RedirectInterceptor implements Interceptor<Object> {

    public final static String BODY = "redirection";

    @Override
    public void onRedirection(ForestRequest<?> redirectReq, ForestRequest<?> prevReq, ForestResponse<?> prevRes) {
        redirectReq.addBody("body", BODY);
    }
}
