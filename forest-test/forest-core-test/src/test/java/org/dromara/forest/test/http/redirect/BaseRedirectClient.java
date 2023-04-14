package org.dromara.forest.test.http.redirect;

import org.dromara.forest.annotation.Address;
import org.dromara.forest.annotation.BaseRequest;
import org.dromara.forest.annotation.Post;
import org.dromara.forest.annotation.Redirection;
import org.dromara.forest.callback.OnRedirection;
import org.dromara.forest.http.ForestResponse;

@Address(host = "localhost", port = "${port}")
@BaseRequest(interceptor = RedirectInterceptor.class)
public interface BaseRedirectClient {

    @Post("/")
    ForestResponse<String> testAutoRedirect(OnRedirection onRedirection);


    @Post("/")
    @Redirection(false)
    ForestResponse<String> testNotAutoRedirect(OnRedirection onRedirection);

}
