package org.forest.executors.httpclient;

import org.apache.http.client.methods.HttpRequestBase;
import org.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 13:31
 */
public interface HttpclientRequestProvider<T extends HttpRequestBase> {

    T getRequest(String url);

}
