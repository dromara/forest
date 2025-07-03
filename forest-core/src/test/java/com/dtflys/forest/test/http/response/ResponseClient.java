package com.dtflys.forest.test.http.response;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.AutoCookieSaveAndLoad;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.UnclosedResponse;

@AutoCookieSaveAndLoad
@Address(host = "localhost", port = "{port}")
public interface ResponseClient {

    @Get("/headers")
    ForestResponse<String> getResponseHeaders();

    @Get("/unclosed")
    UnclosedResponse<String> getUnclosedResponseHeaders();
}
