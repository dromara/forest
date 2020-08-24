package com.dtflys.forest.backend.httpclient.entity;

public class HttpHeadWithBodyEntity extends HttpWithBodyEntity {

    public HttpHeadWithBodyEntity(String uri) {
        super(uri, "HEAD");
    }

}
