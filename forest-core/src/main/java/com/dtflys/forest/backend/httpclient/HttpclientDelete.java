package com.dtflys.forest.backend.httpclient;

import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-28 12:42
 */
@NotThreadSafe
public class HttpclientDelete extends HttpEntityEnclosingRequestBase {

    public final static String METHOD_NAME = "DELETE";


    public HttpclientDelete(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }

}
