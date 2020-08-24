package com.dtflys.forest.backend.httpclient.executor;

import com.dtflys.forest.backend.httpclient.HttpclientRequestProvider;
import com.dtflys.forest.backend.httpclient.entity.HttpHeadWithBodyEntity;
import com.dtflys.forest.backend.httpclient.request.HttpclientRequestSender;
import com.dtflys.forest.backend.httpclient.response.HttpclientResponseHandler;
import com.dtflys.forest.backend.url.URLBuilder;
import com.dtflys.forest.http.ForestRequest;
import org.apache.http.client.methods.HttpHead;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 14:34
 */
public class HttpclientHeadExecutor extends AbstractHttpclientExecutor<HttpHeadWithBodyEntity> {

    @Override
    protected HttpclientRequestProvider<HttpHeadWithBodyEntity> getRequestProvider() {
        return url -> new HttpHeadWithBodyEntity(url);
    }

    @Override
    protected URLBuilder getURLBuilder() {
        return URLBuilder.getQueryableURLBuilder();
    }

    public HttpclientHeadExecutor(ForestRequest request, HttpclientResponseHandler httpclientResponseHandler, HttpclientRequestSender requestSender) {
        super(request, httpclientResponseHandler, requestSender);
    }

}
