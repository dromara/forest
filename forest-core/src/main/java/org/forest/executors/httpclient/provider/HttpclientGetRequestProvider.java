package org.forest.executors.httpclient.provider;

import org.apache.http.client.methods.HttpGet;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 17:02
 */
public class HttpclientGetRequestProvider implements HttpclientRequestProvider<HttpGet> {

    @Override
    public HttpGet getRequest(String url) {
        return new HttpGet(url);
    }

}
