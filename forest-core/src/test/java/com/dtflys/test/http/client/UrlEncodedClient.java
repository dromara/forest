package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Query;

@BaseRequest(baseURL = "http://localhost:${port}")
public interface UrlEncodedClient {


    @Get("/encoded?lang={0}&code={1}&data={2}&content={3}")
    String getUrlEncoded(String lang, String code, String data, String content);

    @Get("/encoded")
    String getUrlEncodedWithQuery(
            @Query(value = "lang") String lang,
            @Query(value = "code") String code,
            @Query(value = "data") String data,
            @Query(value = "content") String content);

    @Post("/encoded?lang=${0}&code=${1}&data=${2}&content=${3}")
    String postUrlEncoded(String lang, String code, String data, String content);

}
