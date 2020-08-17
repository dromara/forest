package com.dtflys.forest.backend.httpclient.entity;

public class HttpOptionsWithBodyEntity extends HttpWithBodyEntity {

    public HttpOptionsWithBodyEntity(String uri) {
        super(uri, "OPTIONS");
    }

}
