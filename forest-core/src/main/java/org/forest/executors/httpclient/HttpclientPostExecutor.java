package org.forest.executors.httpclient;

import org.apache.http.client.methods.HttpPost;
import org.forest.http.ForestRequest;

/**
 * @author gongjun
 * @since 2016-05-24
 */
public class HttpclientPostExecutor extends AbstractEntityHttpExecutor<HttpPost> {

    @Override
    protected HttpclientRequestProvider<HttpPost> getRequestProvider() {
        return new HttpclientRequestProvider<HttpPost>() {
            @Override
            public HttpPost getRequest(String url) {
                return new HttpPost(url);
            }
        };
    }

    public HttpclientPostExecutor(HttpclientConnectionManager connectionManager, ForestRequest requst) {
        super(connectionManager, requst);
    }

}
