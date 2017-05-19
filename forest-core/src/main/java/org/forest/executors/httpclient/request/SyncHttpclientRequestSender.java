package org.forest.executors.httpclient.request;

import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.forest.executors.httpclient.response.HttpclientResponseHandler;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 20:16
 */
public class SyncHttpclientRequestSender<T extends HttpRequest> implements HttpclientRequestSender<T> {

    private HttpClient client;

    @Override
    public void sendRequest(T request, HttpclientResponseHandler responseHandler) {

    }

    public void sendRequest(int tryCount, T request, HttpclientResponseHandler responseHandler) {

    }

}
