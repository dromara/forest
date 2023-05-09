package org.dromara.forest.core.test.http.client;

import org.dromara.forest.annotation.BaseRequest;
import org.dromara.forest.annotation.Get;
import org.dromara.forest.annotation.Post;
import org.dromara.forest.annotation.Query;
import org.dromara.forest.annotation.URLEncode;

@BaseRequest(baseURL = "http://localhost:${port}")
public interface UrlEncodedClient {

    @Get("/encoded/?a=${0}&b=${1}")
    String getEncodedArgs(String a, String b);

    @Get("/encoded/?a={0}&b={1}")
    String getEncodedArgs2(String a, String b);

    @Get("/encoded/?url1={0}&url2={1}&lang={2}&code={3}&data={4}&content={5}")
    String getUrlEncoded(String url1, String url2, String lang, String code, String data, String content);

    @Get("/encoded")
    String getUrlEncodedWithQuery(
            @Query("url1") @URLEncode String url1,
            @Query("url2") String url2,
            @Query("lang") String lang,
            @Query("code") String code,
            @Query("data") String data,
            @Query("content") String content);

    @Post("/encoded?url1={0}&url2={1}&lang={2}&code=${3}&data=${4}&content=${5}")
    String postUrlEncoded(String lang, String code, String data, String content);

}
