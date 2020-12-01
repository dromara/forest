package com.dtflys.forest.backend.httpclient.entity;

/**
 * HttpClient后端的Head方法请求体
 *
 * @author gongjun[@dt_flys@hotmail.com]
 * @since 1.4.0
 */
public class HttpHeadWithBodyEntity extends AbstractHttpWithBodyEntity {

    public HttpHeadWithBodyEntity(String uri) {
        super(uri, "HEAD");
    }

}
