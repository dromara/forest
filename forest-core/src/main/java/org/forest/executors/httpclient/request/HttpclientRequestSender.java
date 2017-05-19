package org.forest.executors.httpclient.request;

import org.apache.http.HttpRequest;
import org.forest.executors.httpclient.response.HttpclientResponseHandler;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 15:47
 */
public interface HttpclientRequestSender<T extends HttpRequest> {

    void sendRequest(T request, HttpclientResponseHandler responseHandler);

}
