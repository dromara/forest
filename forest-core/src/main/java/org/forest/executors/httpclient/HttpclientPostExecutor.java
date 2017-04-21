package org.forest.executors.httpclient;

import org.apache.http.client.methods.HttpPost;
import org.forest.executors.httpclient.provider.HttpclientPostRequestProvider;
import org.forest.executors.httpclient.provider.HttpclientRequestProvider;
import org.forest.http.ForestRequest;

/**
 * @author gongjun
 * @since 2016-05-24
 */
public class HttpclientPostExecutor extends AbstractEntityHttpExecutor<HttpPost> {

    @Override
    protected HttpclientRequestProvider<HttpPost> getRequestProvider() {
        return new HttpclientPostRequestProvider();
    }

    public HttpclientPostExecutor(HttpclientConnectionManager connectionManager, ForestRequest requst) {
        super(connectionManager, requst);
    }

}
