package org.forest.executors.httpclient;

import org.apache.http.client.methods.HttpPut;
import org.forest.http.ForestRequest;

/**
 * @author gongjun
 * @since 2016-05-24
 */
public class HttpclientPatchExecutor extends AbstractEntityHttpExecutor<HttpclientPatchExecutor.HttpPatch> {

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

    public HttpclientPatchExecutor(HttpclientConnectionManager connectionManager, ForestRequest requst) {
        super(connectionManager, requst);
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



