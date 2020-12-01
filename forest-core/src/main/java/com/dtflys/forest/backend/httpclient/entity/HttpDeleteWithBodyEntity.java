package com.dtflys.forest.backend.httpclient.entity;

/**
 * HttpClient后端的Delete方法请求体
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.4.0
 */
public class HttpDeleteWithBodyEntity extends AbstractHttpWithBodyEntity {

    public HttpDeleteWithBodyEntity(String uri) {
        super(uri, "DELETE");
    }

}
