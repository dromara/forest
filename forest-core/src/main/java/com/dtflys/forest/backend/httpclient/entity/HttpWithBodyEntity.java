package com.dtflys.forest.backend.httpclient.entity;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

public abstract class HttpWithBodyEntity extends HttpEntityEnclosingRequestBase {

    private final String httpMethod;


    @Override
    public String getMethod() {
        return httpMethod;
    }

    public HttpWithBodyEntity(String httpMethod) {
        super();
        this.httpMethod = httpMethod;
    }

    public HttpWithBodyEntity(final URI uri, String httpMethod) {
        super();
        this.httpMethod = httpMethod;
        setURI(uri);
    }

    public HttpWithBodyEntity(final String uri, String httpMethod) {
        super();
        this.httpMethod = httpMethod;
        setURI(URI.create(uri));
    }

}
