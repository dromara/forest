package com.dtflys.forest.backend.httpclient.entity;

/**
 * HttpClient后端的Get方法请求体
 *
 * @author gongjun[@dt_flys@hotmail.com]
 * @since 1.4.0
 */
public class HttpGetWithBodyEntity extends AbstractHttpWithBodyEntity {

    public HttpGetWithBodyEntity(String uri) {
        super(uri, "GET");
    }

}
