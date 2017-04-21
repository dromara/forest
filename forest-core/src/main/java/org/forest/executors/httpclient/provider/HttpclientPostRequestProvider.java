package org.forest.executors.httpclient.provider;

import org.apache.http.client.methods.HttpPost;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 17:07
 */
public class HttpclientPostRequestProvider implements HttpclientRequestProvider<HttpPost> {

    @Override
    public HttpPost getRequest(String url) {
        return new HttpPost(url);
    }

}
