package org.forest.backend.okhttp3.response;

import okhttp3.Response;
import org.forest.backend.BackbendResponseHandler;
import org.forest.handler.ResponseHandler;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;
import org.forest.http.ForestResponseFactory;

import java.util.concurrent.Future;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 15:46
 */
public class OkHttp3ResponseHandler extends BackbendResponseHandler<Object> {

    public OkHttp3ResponseHandler(ForestRequest request, ResponseHandler responseHandler) {
        super(request, responseHandler);
    }

    public Object handleSync(Response okResponse, ForestResponse response) {
        int statusCode = okResponse.code();
        String msg = okResponse.message();
        return handleSync(response, statusCode, msg);
    }

    @Override
    public void handleFuture(Future<Object> httpResponseFuture, ForestResponseFactory forestResponseFactory) {
        responseHandler.handleResult(httpResponseFuture);
    }


}
