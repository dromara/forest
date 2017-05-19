package org.forest.executors.httpclient;

import org.apache.http.client.methods.HttpPut;
import org.forest.executors.url.URLBuilder;
import org.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 14:28
 */
public class HttpclientPutHttpExecutor extends AbstractEntityHttpExecutor<HttpPut> {

    private final static HttpclientRequestProvider<HttpPut> httpPutProvider =
            new HttpclientRequestProvider<HttpPut>() {
                @Override
                public HttpPut getRequest(String url) {
                    return new HttpPut(url);
                }
            };

    @Override
    protected HttpclientRequestProvider<HttpPut> getRequestProvider() {
        return httpPutProvider;
    }

    @Override
    protected URLBuilder getURLBuilder() {
        return URLBuilder.getSimpleURLBuilder();
    }

    public HttpclientPutHttpExecutor(HttpclientConnectionManager connectionManager, ForestRequest requst) {
        super(connectionManager, requst);
    }

}
