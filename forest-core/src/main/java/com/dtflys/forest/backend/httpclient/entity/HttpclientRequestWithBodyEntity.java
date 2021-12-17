package com.dtflys.forest.backend.httpclient.entity;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

/**
 * HttpClient请求体抽象基类
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.4.0
 */
public class HttpclientRequestWithBodyEntity extends HttpEntityEnclosingRequestBase {

    private final String httpMethod;

    @Override
    public String getMethod() {
        return httpMethod;
    }


    public HttpclientRequestWithBodyEntity(final String uri, String httpMethod) {
        super();
        this.httpMethod = httpMethod;
        setURI(URI.create(uri));
    }

}
