package org.forest.executors.httpclient.request;

import org.apache.http.HttpRequest;
import org.forest.executors.httpclient.response.HttpclientResponseHandler;
import org.forest.http.ForestRequest;

import java.io.IOException;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 15:47
 */
public interface HttpclientRequestSender<T extends HttpRequest> {

    void sendRequest(ForestRequest request, HttpclientResponseHandler responseHandler) throws IOException;

}
