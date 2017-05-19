package org.forest.executors.httpclient;

import org.apache.http.client.methods.HttpPost;
import org.forest.http.ForestRequest;

/**
 * @author gongjun
 * @since 2016-05-24
 */
public class HttpclientPostExecutor extends AbstractEntityHttpExecutor<HttpPost> {

    private final static HttpclientRequestProvider<HttpPost> httpPostProvider =
            new HttpclientRequestProvider<HttpPost>() {
                @Override
                public HttpPost getRequest(String url) {
                    return new HttpPost(url);
                }
            };

    @Override
    protected HttpclientRequestProvider<HttpPost> getRequestProvider() {
        return httpPostProvider;
    }

    public HttpclientPostExecutor(HttpclientConnectionManager connectionManager, ForestRequest requst) {
        super(connectionManager, requst);
    }

}
