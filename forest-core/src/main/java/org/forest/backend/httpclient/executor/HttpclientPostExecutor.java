package org.forest.backend.httpclient.executor;

import org.apache.http.client.methods.HttpPost;
import org.forest.backend.httpclient.HttpclientRequestProvider;
import org.forest.backend.httpclient.request.HttpclientRequestSender;
import org.forest.backend.httpclient.response.HttpclientResponseHandler;
import org.forest.backend.url.URLBuilder;
import org.forest.http.ForestRequest;

/**
 * @author gongjun
 * @since 2016-05-24
 */
public class HttpclientPostExecutor extends AbstractHttpclientEntityExecutor<HttpPost> {

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


    public HttpclientPostExecutor(ForestRequest requst, HttpclientResponseHandler httpclientResponseHandler, HttpclientRequestSender requestSender) {
        super(requst, httpclientResponseHandler, requestSender);
    }

}
