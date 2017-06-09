package org.forest.executors.httpclient.response;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.forest.handler.ResponseHandler;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-06-09 18:46
 */
public class AsyncHttpclientResponseHandler extends HttpclientResponseHandler {

    public AsyncHttpclientResponseHandler(ForestRequest request, ResponseHandler responseHandler) {
        super(request, responseHandler);
    }

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse, ForestResponse response) {

    }
}
