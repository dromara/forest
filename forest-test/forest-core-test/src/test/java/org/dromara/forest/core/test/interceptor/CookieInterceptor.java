package org.dromara.forest.core.test.interceptor;

import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestCookie;
import org.dromara.forest.http.ForestCookies;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.interceptor.Interceptor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-12-14 2:03
 */
public class CookieInterceptor implements Interceptor {

    private Map<String, List<ForestCookie>> cookieCache = new ConcurrentHashMap<>();

    @Override
    public void onSaveCookie(ForestRequest request, ForestCookies cookies) {
        String host = request.getURI().getHost();
        cookieCache.put(host, cookies.allCookies());
    }

    @Override
    public void onLoadCookie(ForestRequest request, ForestCookies cookies) {
        String host = request.getURI().getHost();
        List<ForestCookie> cookieList = cookieCache.get(host);
        cookies.addAllCookies(cookieList);
    }

    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {

    }

    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {

    }
}
