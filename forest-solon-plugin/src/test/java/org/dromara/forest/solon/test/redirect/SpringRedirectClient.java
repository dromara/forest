package org.dromara.forest.solon.test.redirect;

import org.dromara.forest.annotation.Address;
import org.dromara.forest.annotation.ForestClient;
import org.dromara.forest.annotation.Post;
import org.dromara.forest.annotation.Redirection;
import org.dromara.forest.http.ForestResponse;

@ForestClient
@Address(port = "${port}")
public interface SpringRedirectClient {

    @Post("/")
    ForestResponse<String> testRedirect1();

    @Redirection
    @Post("/")
    ForestResponse<String> testRedirect2();

    @Redirection(false)
    @Post("/")
    ForestResponse<String> testRedirect3();


}
