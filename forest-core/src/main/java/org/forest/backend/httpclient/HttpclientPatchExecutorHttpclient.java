package org.forest.backend.httpclient;

import org.apache.http.client.methods.HttpPut;
import org.forest.backend.httpclient.request.HttpclientRequestSender;
import org.forest.backend.httpclient.response.HttpclientResponseHandler;
import org.forest.backend.url.URLBuilder;
import org.forest.http.ForestRequest;

/**
 * @author gongjun
 * @since 2016-05-24
 */
public class HttpclientPatchExecutorHttpclient extends AbstractHttpclientEntityExecutor<HttpclientPatchExecutorHttpclient.HttpPatch> {

    private final static HttpclientRequestProvider<HttpPatch> httpPatchProvider =
            new HttpclientRequestProvider<HttpPatch>() {
                @Override
                public HttpPatch getRequest(String url) {
                    return new HttpPatch(url);
                }
            };

    @Override
    protected HttpclientRequestProvider<HttpPatch> getRequestProvider() {
        return httpPatchProvider;
    }

    @Override
    protected URLBuilder getURLBuilder() {
        return URLBuilder.getSimpleURLBuilder();
    }

    public HttpclientPatchExecutorHttpclient(ForestRequest request, HttpclientResponseHandler httpclientResponseHandler, HttpclientRequestSender requestSender) {
        super(request, httpclientResponseHandler, requestSender);
    }


    public static class HttpPatch extends HttpPut {

        public HttpPatch(String url) {
            super(url);
        }

        @Override
        public String getMethod(){
            return "PATCH";
        }
    }

}



