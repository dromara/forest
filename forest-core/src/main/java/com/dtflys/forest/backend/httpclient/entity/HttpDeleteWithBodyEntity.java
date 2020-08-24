package com.dtflys.forest.backend.httpclient.entity;

public class HttpDeleteWithBodyEntity extends HttpWithBodyEntity {

    public HttpDeleteWithBodyEntity(String uri) {
        super(uri, "DELETE");
    }

}
