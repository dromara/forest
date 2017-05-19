package org.forest.executors.httpclient;

import org.apache.http.client.methods.HttpOptions;
import org.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 14:44
 */
public class HttpclientOptionsExecutor extends AbstractHttpclientExecutor<HttpOptions> {

    private final static HttpclientRequestProvider<HttpOptions> httpOptionsProvider =
            new HttpclientRequestProvider<HttpOptions>() {
                @Override
                public HttpOptions getRequest(String url) {
                    return new HttpOptions(url);
                }
            };

    @Override
    protected HttpclientRequestProvider<HttpOptions> getRequestProvider() {
        return httpOptionsProvider;
    }

    public HttpclientOptionsExecutor(HttpclientConnectionManager connectionManager, ForestRequest request) {
        super(connectionManager, request);
    }

}
