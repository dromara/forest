package com.dtflys.forest.backend.httpclient.executor;

import com.dtflys.forest.backend.httpclient.HttpclientRequestProvider;
import com.dtflys.forest.backend.httpclient.request.HttpclientRequestSender;
import com.dtflys.forest.backend.httpclient.response.HttpclientResponseHandler;
import com.dtflys.forest.backend.url.URLBuilder;
import com.dtflys.forest.http.ForestRequest;
import org.apache.http.client.methods.HttpPost;

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
