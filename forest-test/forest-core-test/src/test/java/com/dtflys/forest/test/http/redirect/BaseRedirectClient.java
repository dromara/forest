package com.dtflys.forest.test.http.redirect;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Redirection;
import com.dtflys.forest.callback.OnRedirection;
import com.dtflys.forest.http.ForestResponse;

@Address(host = "localhost", port = "${port}")
@BaseRequest(interceptor = RedirectInterceptor.class)
public interface BaseRedirectClient {

    @Post("/")
    ForestResponse<String> testAutoRedirect(OnRedirection onRedirection);


    @Post("/")
    @Redirection(false)
    ForestResponse<String> testNotAutoRedirect(OnRedirection onRedirection);

}
