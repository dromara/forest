package org.forest.executors.httpclient;

import org.apache.http.client.methods.HttpHead;
import org.forest.executors.httpclient.request.HttpclientRequestSender;
import org.forest.executors.httpclient.response.HttpclientResponseHandler;
import org.forest.executors.url.URLBuilder;
import org.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 14:34
 */
public class HttpclientHeadExecutor extends AbstractHttpclientExecutor<HttpHead> {

    private final static HttpclientRequestProvider<HttpHead> httpHeadProvider =
            new HttpclientRequestProvider<HttpHead>() {
                @Override
                public HttpHead getRequest(String url) {
                    return new HttpHead(url);
                }
            };

    @Override
    protected HttpclientRequestProvider<HttpHead> getRequestProvider() {
        return httpHeadProvider;
    }

    @Override
    protected URLBuilder getURLBuilder() {
        return URLBuilder.getQueryableURLBuilder();
    }

    public HttpclientHeadExecutor(ForestRequest request, HttpclientResponseHandler httpclientResponseHandler, HttpclientRequestSender requestSender) {
        super(request, httpclientResponseHandler, requestSender);
    }

}
