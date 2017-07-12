package org.forest.executors.httpclient.response;


import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.forest.exceptions.ForestNetworkException;
import org.forest.handler.ResponseHandler;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 20:10
 */
public class SyncHttpclientResponseHandler extends HttpclientResponseHandler {


    public SyncHttpclientResponseHandler(ForestRequest request, ResponseHandler responseHandler) {
        super(request, responseHandler);
    }

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse, ForestResponse response) {
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        responseHandler.handle(request, response);
        if (response.isError()) {
            throw new ForestNetworkException(
                    httpResponse.getStatusLine().getReasonPhrase(), statusCode, response);
        }
    }
}
