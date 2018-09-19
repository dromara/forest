package com.dtflys.forest.backend.httpclient.executor;


import com.dtflys.forest.backend.httpclient.HttpclientDelete;
import com.dtflys.forest.backend.httpclient.HttpclientRequestProvider;
import com.dtflys.forest.backend.httpclient.request.HttpclientRequestSender;
import com.dtflys.forest.backend.httpclient.response.HttpclientResponseHandler;
import com.dtflys.forest.backend.url.URLBuilder;
import com.dtflys.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-09-15
 */
public class HttpclientDeleteExecutor extends AbstractHttpclientEntityExecutor<HttpclientDelete> {

    private final static HttpclientRequestProvider<HttpclientDelete> httpDeleteProvider =
            new HttpclientRequestProvider<HttpclientDelete>() {
                @Override
                public HttpclientDelete getRequest(String url) {
                    return new HttpclientDelete(url);
                }
            };


    @Override
    protected HttpclientRequestProvider<HttpclientDelete> getRequestProvider() {
        return httpDeleteProvider;
    }

    @Override
    protected URLBuilder getURLBuilder() {
        return URLBuilder.getQueryableURLBuilder();
    }

    public HttpclientDeleteExecutor(ForestRequest request, HttpclientResponseHandler httpclientResponseHandler, HttpclientRequestSender requestSender) {
        super(request, httpclientResponseHandler, requestSender);
    }

}



