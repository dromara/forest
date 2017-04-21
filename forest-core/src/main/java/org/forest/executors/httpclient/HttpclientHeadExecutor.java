package org.forest.executors.httpclient;

import org.apache.http.client.methods.HttpHead;
import org.forest.executors.httpclient.provider.HttpclientHeadRequestProvider;
import org.forest.executors.httpclient.provider.HttpclientRequestProvider;
import org.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 14:34
 */
public class HttpclientHeadExecutor extends AbstractHttpclientExecutor<HttpHead> {

    @Override
    protected HttpclientRequestProvider<HttpHead> getRequestProvider() {
        return new HttpclientHeadRequestProvider();
    }

    public HttpclientHeadExecutor(HttpclientConnectionManager connectionManager, ForestRequest request) {
        super(connectionManager, request);
    }

}
