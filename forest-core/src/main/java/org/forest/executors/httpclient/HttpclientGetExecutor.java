package org.forest.executors.httpclient;

import org.apache.http.client.methods.HttpGet;
import org.forest.http.ForestRequest;

/**
 * @author gongjun
 * @since 2016-05-24
 */
public class HttpclientGetExecutor extends AbstractHttpclientExecutor<HttpGet> {

    @Override
    protected HttpclientRequestProvider<HttpGet> getRequestProvider() {
        return new HttpclientRequestProvider<HttpGet>() {
            @Override
            public HttpGet getRequest(String url) {
                return new HttpGet(url);
            }
        };
    }

    public HttpclientGetExecutor(HttpclientConnectionManager connectionManager, ForestRequest request) {
        super(connectionManager, request);
    }


}
