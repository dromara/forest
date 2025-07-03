package com.dtflys.forest.test.http.cookie;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.AutoCookieSaveAndLoad;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.http.ForestResponse;

@AutoCookieSaveAndLoad
@Address(host = "{hostname}", port = "{port}")
public interface CookieClient {

    @Post(url = "/login", contentType = ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
    ForestResponse<String> login(@Body("username") String username, @Body("password") String password);

    @Post(url = "/do")
    void doSomething();
}
