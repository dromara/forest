package org.forest.executors.httpclient;

import org.apache.http.client.methods.HttpTrace;
import org.forest.executors.httpclient.provider.HttpclientRequestProvider;
import org.forest.executors.httpclient.provider.HttpclientTraceRequestProvider;
import org.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 14:45
 */
public class HttpclientTraceExecutor extends AbstractHttpclientExecutor<HttpTrace> {

    @Override
    protected HttpclientRequestProvider<HttpTrace> getRequestProvider() {
        return new HttpclientTraceRequestProvider();
    }

    public HttpclientTraceExecutor(HttpclientConnectionManager connectionManager, ForestRequest request) {
        super(connectionManager, request);
    }

}
