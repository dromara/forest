package com.dtflys.forest.backend.httpclient.executor;

import com.dtflys.forest.backend.httpclient.HttpclientRequestProvider;
import com.dtflys.forest.backend.httpclient.request.HttpclientRequestSender;
import com.dtflys.forest.backend.httpclient.response.HttpclientResponseHandler;
import com.dtflys.forest.backend.url.URLBuilder;
import com.dtflys.forest.http.ForestRequest;
import org.apache.http.client.methods.HttpOptions;

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
