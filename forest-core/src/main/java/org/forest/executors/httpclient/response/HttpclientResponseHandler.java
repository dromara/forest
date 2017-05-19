package org.forest.executors.httpclient.response;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.forest.handler.ResponseHandler;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 15:46
 */
public abstract class HttpclientResponseHandler {

    protected final ForestRequest request;

    protected final ResponseHandler responseHandler;

    protected HttpclientResponseHandler(ForestRequest request, ResponseHandler responseHandler) {
        this.request = request;
        this.responseHandler = responseHandler;
    }

    public abstract void handle(HttpRequest httpRequest, HttpResponse httpResponse, ForestResponse response);

}
