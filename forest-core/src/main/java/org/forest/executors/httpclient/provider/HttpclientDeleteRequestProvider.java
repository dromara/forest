package org.forest.executors.httpclient.provider;

import org.apache.http.client.methods.HttpDelete;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-20 17:10
 */
public class HttpclientDeleteRequestProvider implements HttpclientRequestProvider<HttpDelete> {

    @Override
    public HttpDelete getRequest(String url) {
        return new HttpDelete(url);
    }

}
