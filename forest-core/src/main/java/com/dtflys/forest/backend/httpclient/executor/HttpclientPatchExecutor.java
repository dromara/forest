package com.dtflys.forest.backend.httpclient.executor;

import com.dtflys.forest.backend.httpclient.HttpclientRequestProvider;
import com.dtflys.forest.backend.httpclient.request.HttpclientRequestSender;
import com.dtflys.forest.backend.httpclient.response.HttpclientResponseHandler;
import com.dtflys.forest.backend.url.URLBuilder;
import com.dtflys.forest.http.ForestRequest;
import org.apache.http.client.methods.HttpPut;

/**
 * @author gongjun
 * @since 2016-05-24
 */
public class HttpclientPatchExecutor extends AbstractHttpclientEntityExecutor<HttpclientPatchExecutor.HttpPatch> {

    @Override
    protected HttpclientRequestProvider<HttpPatch> getRequestProvider() {
        return  url -> new HttpPatch(url);
    }

    @Override
    protected URLBuilder getURLBuilder() {
        return URLBuilder.getSimpleURLBuilder();
    }

    public HttpclientPatchExecutor(ForestRequest request, HttpclientResponseHandler httpclientResponseHandler, HttpclientRequestSender requestSender) {
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



