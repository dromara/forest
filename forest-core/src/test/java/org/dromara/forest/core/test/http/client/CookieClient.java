package org.dromara.forest.core.test.http.client;

import org.dromara.forest.annotation.Post;
import org.dromara.forest.callback.OnLoadCookie;
import org.dromara.forest.callback.OnSaveCookie;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.core.test.interceptor.CookieInterceptor;

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
            url = "http://localhost:${port}/test/xxx",
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


    @Post("http://localhost:${port}/test")
    ForestResponse<String> testInvalidCookie();

}
