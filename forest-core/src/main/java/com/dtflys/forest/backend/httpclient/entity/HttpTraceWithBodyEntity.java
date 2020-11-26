package com.dtflys.forest.backend.httpclient.entity;

/**
 * HttpClient后端的Trace方法请求体
 *
 * @author gongjun[@dt_flys@hotmail.com]
 * @since 1.4.0
 */
public class HttpTraceWithBodyEntity extends AbstractHttpWithBodyEntity {

    public HttpTraceWithBodyEntity(String uri) {
        super(uri, "TRACE");
    }

}
