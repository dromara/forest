package org.forest.executors.httpclient.provider;

import org.apache.http.client.methods.HttpPut;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-20 17:13
 */
public class HttpclientPutRequestProvider implements HttpclientRequestProvider<HttpPut> {

    @Override
    public HttpPut getRequest(String url) {
        return new HttpPut(url);
    }

}
