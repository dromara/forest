package org.forest.executors.httpclient.provider;

import org.apache.http.client.methods.HttpHead;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-20 17:08
 */
public class HttpclientHeadRequestProvider implements HttpclientRequestProvider<HttpHead> {

    @Override
    public HttpHead getRequest(String url) {
        return new HttpHead(url);
    }
}
