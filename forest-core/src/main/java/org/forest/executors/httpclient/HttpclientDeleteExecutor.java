package org.forest.executors.httpclient;


import org.apache.http.client.methods.HttpDelete;
import org.forest.executors.httpclient.provider.HttpclientDeleteRequestProvider;
import org.forest.executors.httpclient.provider.HttpclientRequestProvider;
import org.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-09-15
 */
public class HttpclientDeleteExecutor extends AbstractHttpclientExecutor<HttpDelete> {

    @Override
    protected HttpclientRequestProvider<HttpDelete> getRequestProvider() {
        return new HttpclientDeleteRequestProvider();
    }

    public HttpclientDeleteExecutor(HttpclientConnectionManager connectionManager, ForestRequest request) {
        super(connectionManager, request);
    }

}



