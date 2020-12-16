package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.callback.OnLoadCookie;
import com.dtflys.forest.callback.OnSaveCookie;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.interceptor.CookieInterceptor;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-12-14 1:02
 */
public interface CookieClient {

    @Post(
            url = "http://localhost:${port}/login?username=foo",
            headers = {"Accept:text/plain"}
    )
    ForestResponse testLoginWithCallback(OnSaveCookie onSaveCookie);

    @Post(
            url = "http://localhost:${port}/test",
            headers = {"Accept:text/plain"}
    )
    ForestResponse<String> testCookieWithCallback(OnLoadCookie onLoadCookie);


    @Post(
            url = "http://localhost:${port}/login?username=foo",
            headers = {"Accept:text/plain"},
            interceptor = CookieInterceptor.class
    )
    ForestResponse testLoginWithInterceptor();

    @Post(
            url = "http://localhost:${port}/test",
            headers = {"Accept:text/plain"},
            interceptor = CookieInterceptor.class
    )
    ForestResponse<String> testCookieWithInterceptor();

}
