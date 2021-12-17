package com.dtflys.forest.backend.httpclient;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 13:31
 */
public interface HttpclientRequestProvider<T extends HttpUriRequest> {

    T getRequest(String url);

}
