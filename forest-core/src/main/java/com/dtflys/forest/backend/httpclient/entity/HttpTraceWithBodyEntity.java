package com.dtflys.forest.backend.httpclient.entity;

public class HttpTraceWithBodyEntity extends HttpWithBodyEntity {

    public HttpTraceWithBodyEntity(String uri) {
        super(uri, "TRACE");
    }

}
