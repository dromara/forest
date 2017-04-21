package org.forest.executors.httpclient;

import org.apache.http.client.methods.HttpOptions;
import org.forest.executors.httpclient.provider.HttpclientOptionsRequestProvider;
import org.forest.executors.httpclient.provider.HttpclientRequestProvider;
import org.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 14:44
 */
public class HttpclientOptionsExecutor extends AbstractHttpclientExecutor<HttpOptions> {

    @Override
    protected HttpclientRequestProvider<HttpOptions> getRequestProvider() {
        return new HttpclientOptionsRequestProvider();
    }

    public HttpclientOptionsExecutor(HttpclientConnectionManager connectionManager, ForestRequest request) {
        super(connectionManager, request);
    }

}
