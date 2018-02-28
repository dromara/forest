package org.forest.backend.httpclient.executor;


import org.apache.http.client.methods.HttpDelete;
import org.forest.backend.NoneBodyBuilder;
import org.forest.backend.httpclient.HttpclientDelete;
import org.forest.backend.httpclient.HttpclientRequestProvider;
import org.forest.backend.httpclient.body.HttpclientBodyBuilder;
import org.forest.backend.httpclient.request.HttpclientRequestSender;
import org.forest.backend.httpclient.response.HttpclientResponseHandler;
import org.forest.backend.url.URLBuilder;
import org.forest.http.ForestRequest;

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



