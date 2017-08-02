package org.forest.executors.httpclient;

import org.apache.http.client.methods.HttpPut;
import org.forest.executors.httpclient.request.HttpclientRequestSender;
import org.forest.executors.httpclient.response.HttpclientResponseHandler;
import org.forest.executors.url.URLBuilder;
import org.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 14:28
 */
public class HttpclientPutExecutorHttpclient extends AbstractHttpclientEntityExecutor<HttpPut> {

    private final static HttpclientRequestProvider<HttpPut> httpPutProvider =
            new HttpclientRequestProvider<HttpPut>() {
                @Override
                public HttpPut getRequest(String url) {
                    return new HttpPut(url);
                }
            };

    @Override
    protected HttpclientRequestProvider<HttpPut> getRequestProvider() {
        return httpPutProvider;
    }

    @Override
    protected URLBuilder getURLBuilder() {
        return URLBuilder.getSimpleURLBuilder();
    }

    public HttpclientPutExecutorHttpclient(ForestRequest requst, HttpclientResponseHandler httpclientResponseHandler, HttpclientRequestSender requestSender) {
        super(requst, httpclientResponseHandler, requestSender);
    }

}
