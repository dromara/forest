package org.forest.executors.httpclient;

import org.apache.http.client.methods.HttpOptions;
import org.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 14:44
 */
public class HttpclientOptionsExecutor extends AbstractHttpclientExecutor<HttpOptions> {

    @Override
    protected HttpclientRequestProvider<HttpOptions> getRequestProvider() {
        return new HttpclientRequestProvider<HttpOptions>() {
            @Override
            public HttpOptions getRequest(String url) {
                return new HttpOptions(url);
            }
        };
    }

    public HttpclientOptionsExecutor(HttpclientConnectionManager connectionManager, ForestRequest request) {
        super(connectionManager, request);
    }

}
