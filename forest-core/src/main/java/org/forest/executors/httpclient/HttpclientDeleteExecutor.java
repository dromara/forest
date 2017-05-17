package org.forest.executors.httpclient;


import org.apache.http.client.methods.HttpDelete;
import org.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-09-15
 */
public class HttpclientDeleteExecutor extends AbstractHttpclientExecutor<HttpDelete> {

    @Override
    protected HttpclientRequestProvider<HttpDelete> getRequestProvider() {
        return new HttpclientRequestProvider<HttpDelete>() {
            @Override
            public HttpDelete getRequest(String url) {
                return new HttpDelete(url);
            }
        };
    }

    public HttpclientDeleteExecutor(HttpclientConnectionManager connectionManager, ForestRequest request) {
        super(connectionManager, request);
    }

}



