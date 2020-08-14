package com.dtflys.forest.backend.httpclient;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

public class HttpGetWithEntity extends HttpEntityEnclosingRequestBase {
    private final static String METHOD_NAME = "GET";

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }

    public HttpGetWithEntity() {
        super();
    }

    public HttpGetWithEntity(final URI uri) {
        super();
        setURI(uri);
    }

    public HttpGetWithEntity(final String uri) {
        super();
        setURI(URI.create(uri));
    }

}
