package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.Get;
import com.dtflys.test.model.TokenResult;

public interface GetTokenClient {

    @Get(url = "http://localhost:${port}/token",
            headers = {"Accept:text/plain"}
    )
    TokenResult getToken();
}
