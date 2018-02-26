package org.forest.backend.httpclient;

import org.apache.http.client.methods.HttpOptions;
import org.forest.backend.httpclient.request.HttpclientRequestSender;
import org.forest.backend.httpclient.response.HttpclientResponseHandler;
import org.forest.backend.url.URLBuilder;
import org.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 14:44
 */
public class HttpclientOptionsExecutor extends AbstractHttpclientExecutor<HttpOptions> {

    private final static HttpclientRequestProvider<HttpOptions> httpOptionsProvider =
            new HttpclientRequestProvider<HttpOptions>() {
                @Override
                public HttpOptions getRequest(String url) {
                    return new HttpOptions(url);
                }
            };


    @Override
    protected HttpclientRequestProvider<HttpOptions> getRequestProvider() {
        return httpOptionsProvider;
    }

    @Override
    protected URLBuilder getURLBuilder() {
        return URLBuilder.getQueryableURLBuilder();
    }


    public HttpclientOptionsExecutor(ForestRequest request, HttpclientResponseHandler httpclientResponseHandler, HttpclientRequestSender requestSender) {
        super(request, httpclientResponseHandler, requestSender);
    }

}
