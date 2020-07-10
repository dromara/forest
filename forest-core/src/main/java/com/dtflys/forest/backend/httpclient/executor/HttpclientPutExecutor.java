package com.dtflys.forest.backend.httpclient.executor;

import com.dtflys.forest.backend.httpclient.HttpclientRequestProvider;
import com.dtflys.forest.backend.httpclient.request.HttpclientRequestSender;
import com.dtflys.forest.backend.httpclient.response.HttpclientResponseHandler;
import com.dtflys.forest.backend.url.URLBuilder;
import com.dtflys.forest.http.ForestRequest;
import org.apache.http.client.methods.HttpPut;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 14:28
 */
public class HttpclientPutExecutor extends AbstractHttpclientEntityExecutor<HttpPut> {

    @Override
    protected HttpclientRequestProvider<HttpPut> getRequestProvider() {
        return url -> new HttpPut(url);
    }

    @Override
    protected URLBuilder getURLBuilder() {
        return URLBuilder.getSimpleURLBuilder();
    }

    public HttpclientPutExecutor(ForestRequest requst, HttpclientResponseHandler httpclientResponseHandler, HttpclientRequestSender requestSender) {
        super(requst, httpclientResponseHandler, requestSender);
    }

}
