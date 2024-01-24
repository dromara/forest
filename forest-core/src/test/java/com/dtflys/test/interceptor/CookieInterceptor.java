package com.dtflys.test.interceptor;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestCookie;
import com.dtflys.forest.http.ForestCookieSet;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;

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
    public void onSaveCookie(ForestRequest request, ForestCookieSet cookies) {
        String host = request.getURI().getHost();
        cookieCache.put(host, cookies.allCookies());
    }

    @Override
    public void onLoadCookie(ForestRequest request, ForestCookieSet cookies) {
        String host = request.getURI().getHost();
        List<ForestCookie> cookieList = cookieCache.get(host);
        cookies.addAllCookies(cookieList);
    }

    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {

    }

    @Override
    public void onSuccess(ForestRequest request, ForestResponse response) {

    }
}
