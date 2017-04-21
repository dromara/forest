package org.forest.executors.httpclient;

import org.apache.http.client.methods.HttpGet;
import org.forest.executors.httpclient.provider.HttpclientGetRequestProvider;
import org.forest.executors.httpclient.provider.HttpclientRequestProvider;
import org.forest.http.ForestRequest;

/**
 * @author gongjun
 * @since 2016-05-24
 */
public class HttpclientGetExecutor extends AbstractHttpclientExecutor<HttpGet> {

    @Override
    protected HttpclientRequestProvider<HttpGet> getRequestProvider() {
        return new HttpclientGetRequestProvider();
    }

    public HttpclientGetExecutor(HttpclientConnectionManager connectionManager, ForestRequest request) {
        super(connectionManager, request);
    }


}
