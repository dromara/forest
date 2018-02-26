package org.forest.backend.httpclient.executor;

import org.apache.http.client.methods.HttpTrace;
import org.forest.backend.httpclient.HttpclientRequestProvider;
import org.forest.backend.httpclient.request.HttpclientRequestSender;
import org.forest.backend.httpclient.response.HttpclientResponseHandler;
import org.forest.backend.url.URLBuilder;
import org.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 14:45
 */
public class HttpclientTraceExecutor extends AbstractHttpclientExecutor<HttpTrace> {

    private final static HttpclientRequestProvider<HttpTrace> httpTraceProvider =
            new HttpclientRequestProvider<HttpTrace>() {
                @Override
                public HttpTrace getRequest(String url) {
                    return new HttpTrace(url);
                }
            };

    @Override
    protected HttpclientRequestProvider<HttpTrace> getRequestProvider() {
        return httpTraceProvider;
    }

    @Override
    protected URLBuilder getURLBuilder() {
        return URLBuilder.getQueryableURLBuilder();
    }

    public HttpclientTraceExecutor(ForestRequest request, HttpclientResponseHandler httpclientResponseHandler, HttpclientRequestSender requestSender) {
        super(request, httpclientResponseHandler, requestSender);
    }

}
