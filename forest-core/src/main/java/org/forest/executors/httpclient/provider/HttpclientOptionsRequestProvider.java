package org.forest.executors.httpclient.provider;

import org.apache.http.client.methods.HttpOptions;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-20 17:09
 */
public class HttpclientOptionsRequestProvider implements HttpclientRequestProvider<HttpOptions> {

    @Override
    public HttpOptions getRequest(String url) {
        return new HttpOptions(url);
    }

}
