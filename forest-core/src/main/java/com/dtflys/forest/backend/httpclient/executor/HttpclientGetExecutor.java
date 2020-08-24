package com.dtflys.forest.backend.httpclient.executor;

import com.dtflys.forest.backend.httpclient.entity.HttpGetWithBodyEntity;
import com.dtflys.forest.backend.httpclient.HttpclientRequestProvider;
import com.dtflys.forest.backend.httpclient.request.HttpclientRequestSender;
import com.dtflys.forest.backend.httpclient.response.HttpclientResponseHandler;
import com.dtflys.forest.backend.url.URLBuilder;
import com.dtflys.forest.http.ForestRequest;

/**
 * @author gongjun
 * @since 2016-05-24
 */
public class HttpclientGetExecutor extends AbstractHttpclientExecutor<HttpGetWithBodyEntity> {

    @Override
    protected HttpclientRequestProvider<HttpGetWithBodyEntity> getRequestProvider() {
        return url -> new HttpGetWithBodyEntity(url);
    }

    @Override
    protected URLBuilder getURLBuilder() {
        return URLBuilder.getQueryableURLBuilder();
    }

    public HttpclientGetExecutor(ForestRequest request, HttpclientResponseHandler httpclientResponseHandler, HttpclientRequestSender requestSender) {
        super(request, httpclientResponseHandler, requestSender);
    }


}
