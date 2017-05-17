package org.forest.executors.httpclient;

import org.apache.http.client.methods.HttpPut;
import org.forest.http.ForestRequest;

/**
 * @author gongjun
 * @since 2016-05-24
 */
public class HttpclientPatchExecutor extends AbstractEntityHttpExecutor<HttpclientPatchExecutor.HttpPatch> {

    @Override
    protected HttpclientRequestProvider<HttpPatch> getRequestProvider() {
        return new HttpclientRequestProvider<HttpPatch>() {
            @Override
            public HttpPatch getRequest(String url) {
                return new HttpPatch(url);
            }
        };
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



