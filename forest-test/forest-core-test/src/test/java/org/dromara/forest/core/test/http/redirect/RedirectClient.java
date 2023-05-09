package org.dromara.forest.core.test.http.redirect;

import org.dromara.forest.annotation.Address;
import org.dromara.forest.annotation.Backend;
import org.dromara.forest.annotation.Post;
import org.dromara.forest.annotation.Redirection;
import org.dromara.forest.callback.OnRedirection;
import org.dromara.forest.callback.OnSuccess;
import org.dromara.forest.http.ForestResponse;

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
