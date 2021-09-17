package com.dtflys.test.http.redirect;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.http.ForestResponse;

@Address(host = "localhost", port = "${port}")
public interface RedirectClient {

    @Post("/")
    ForestResponse<String> testRedirect();

}
