package com.dtflys.forest.backend.httpclient.request;

import com.dtflys.forest.backend.httpclient.conn.HttpclientConnectionManager;
import com.dtflys.forest.backend.httpclient.response.HttpclientForestResponseFactory;
import com.dtflys.forest.backend.httpclient.response.HttpclientResponseHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestResponseFactory;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-07-21 15:49
 */
public class AsyncHttpclientRequestSender extends AbstractHttpclientRequestSender {


    public AsyncHttpclientRequestSender(HttpclientConnectionManager connectionManager, ForestRequest request) {
        super(connectionManager, request);
    }

    @Override
    public void sendRequest(final ForestRequest request, final HttpclientResponseHandler responseHandler, final HttpUriRequest httpRequest) throws IOException {
        final CloseableHttpAsyncClient client = connectionManager.getHttpAsyncClient(request);
        client.start();
        final ForestResponseFactory forestResponseFactory = new HttpclientForestResponseFactory();

        final Future<HttpResponse> future = client.execute(httpRequest, new FutureCallback<HttpResponse>() {
            public void completed(final HttpResponse httpResponse) {
                ForestResponse response = forestResponseFactory.createResponse(request, httpResponse);
                if (response.isSuccess()) {
                    if (request.getOnSuccess() != null) {
                        responseHandler.handleSuccess(response);
                    }
                } else {
                    responseHandler.handleError(response);
                }
            }

            public void failed(final Exception ex) {
                ForestResponse response = forestResponseFactory.createResponse(request, null);
                responseHandler.handleError(response, ex);
                synchronized (client) {
                    try {
                        client.close();
                    } catch (IOException e) {
                    }
                }
            }

            public void cancelled() {
                synchronized (client) {
                    try {
                        client.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
        responseHandler.handleFuture(future, forestResponseFactory);
    }
}
