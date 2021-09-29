package com.dtflys.test.http.redirect;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Backend;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Redirection;
import com.dtflys.forest.callback.OnRedirection;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.http.ForestResponse;

@Redirection(false)
@Address(host = "localhost", port = "${port}")
public interface RedirectClient {

    @Post("/")
    ForestResponse<String> testNotAutoRedirect(OnRedirection onRedirection);

    @Redirection
    @Post("/")
    ForestResponse<String> testAutoRedirect(OnRedirection onRedirection);

    @Backend("okhttp3")
    @Redirection
    @Post(url = "/", async = true)
    String testAutoRedirect_async(OnRedirection onRedirection, OnSuccess onSuccess);

}
