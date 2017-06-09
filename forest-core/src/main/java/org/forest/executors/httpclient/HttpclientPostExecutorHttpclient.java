package org.forest.executors.httpclient;

import org.apache.http.client.methods.HttpPost;
import org.forest.executors.httpclient.response.HttpclientResponseHandler;
import org.forest.executors.url.URLBuilder;
import org.forest.http.ForestRequest;

/**
 * @author gongjun
 * @since 2016-05-24
 */
public class HttpclientPostExecutorHttpclient extends AbstractHttpclientEntityExecutor<HttpPost> {

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

    @Override
    protected URLBuilder getURLBuilder() {
        return URLBuilder.getSimpleURLBuilder();
    }


    public HttpclientPostExecutorHttpclient(HttpclientConnectionManager connectionManager, ForestRequest requst, HttpclientResponseHandler httpclientResponseHandler) {
        super(connectionManager, requst, httpclientResponseHandler);
    }

}
