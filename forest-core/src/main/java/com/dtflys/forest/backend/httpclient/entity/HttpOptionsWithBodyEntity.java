package com.dtflys.forest.backend.httpclient.entity;

/**
 * HttpClient后端的Options方法请求体
 *
 * @author gongjun[@dt_flys@hotmail.com]
 * @since 1.4.0
 */
public class HttpOptionsWithBodyEntity extends AbstractHttpWithBodyEntity {

    public HttpOptionsWithBodyEntity(String uri) {
        super(uri, "OPTIONS");
    }

}
