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


    @Override
    protected HttpclientRequestProvider<HttpPost> getRequestProvider() {
        return url -> new HttpPost(url);
    }

    @Override
    protected URLBuilder getURLBuilder() {
        return URLBuilder.getQueryableURLBuilder();
    }


    public HttpclientPostExecutor(ForestRequest requst, HttpclientResponseHandler httpclientResponseHandler, HttpclientRequestSender requestSender) {
        super(requst, httpclientResponseHandler, requestSender);
    }

}
