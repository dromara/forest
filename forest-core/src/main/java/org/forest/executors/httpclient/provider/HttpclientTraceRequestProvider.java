package org.forest.executors.httpclient.provider;

import org.apache.http.client.methods.HttpTrace;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-20 17:10
 */
public class HttpclientTraceRequestProvider implements HttpclientRequestProvider<HttpTrace> {

    @Override
    public HttpTrace getRequest(String url) {
        return new HttpTrace(url);
    }
}
