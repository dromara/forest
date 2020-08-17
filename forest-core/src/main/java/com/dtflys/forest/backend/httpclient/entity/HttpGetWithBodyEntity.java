package com.dtflys.forest.backend.httpclient.entity;

public class HttpGetWithBodyEntity extends HttpWithBodyEntity {

    public HttpGetWithBodyEntity(String uri) {
        super(uri, "GET");
    }

}
