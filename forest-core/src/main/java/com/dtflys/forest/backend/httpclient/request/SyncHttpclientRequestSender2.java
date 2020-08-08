package com.dtflys.forest.backend.httpclient.request;

import com.dtflys.forest.backend.httpclient.conn.HttpclientConnectionManager;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestResponseFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.backend.httpclient.response.HttpclientForestResponseFactory;
import com.dtflys.forest.backend.httpclient.response.HttpclientResponseHandler;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-01-30 15:34
 */
public class SyncHttpclientRequestSender2 extends AbstractHttpclientRequestSender {

    public SyncHttpclientRequestSender2(HttpclientConnectionManager connectionManager, ForestRequest request) {
        super(connectionManager, request);
    }


    public static void logResponse(ForestRequest request, ForestResponse response) {
        if (!request.isLogEnable()) return;
        logContent("Response: Status=" + response.getStatusCode());
        if (response.isSuccess()) {
            logContent("Response: Content=" + response.getContent());
        }
    }


    @Override
    public void sendRequest(final ForestRequest request, final HttpclientResponseHandler responseHandler, final HttpUriRequest httpRequest, LifeCycleHandler lifeCycleHandler) throws IOException {
        final CloseableHttpAsyncClient client = connectionManager.getHttpAsyncClient(request);
        client.start();
        final AtomicReference<ForestResponse> forestResponseRef = new AtomicReference<>();
        final AtomicReference<Exception> exceptionRef = new AtomicReference<>();
        final ForestResponseFactory forestResponseFactory = new HttpclientForestResponseFactory();
        try {
            Future<HttpResponse> future = client.execute(httpRequest, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse httpResponse) {
                    ForestResponse response = forestResponseFactory.createResponse(request, httpResponse, lifeCycleHandler);
                    forestResponseRef.set(response);
                }

                public void failed(final Exception ex) {
                    ForestResponse response = forestResponseFactory.createResponse(request, null, lifeCycleHandler);
                    forestResponseRef.set(response);
                    exceptionRef.set(ex);
                }

                public void cancelled() {
                }
            });
            HttpResponse httpResponse = null;

            try {
                httpResponse = future.get();
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }
            ForestResponse response = forestResponseRef.get();
            if (response.isSuccess()) {
                logResponse(request, response);
                try {
                    responseHandler.handleSync(httpResponse, response);
                } catch (Exception ex) {
                    if (ex instanceof ForestRuntimeException) {
                        throw ex;
                    } else {
                        throw new ForestRuntimeException(ex);
                    }
                }
            } else {
                Exception ex = exceptionRef.get();
                if (ex == null) {
                    responseHandler.handleError(response);
                } else {
                    responseHandler.handleError(response, ex);
                }
            }
        } finally {
            client.close();
        }
/*
        if (failed.get()) {
            response = forestResponseFactory.createResponse(request, null);
            responseHandler.handleError(response, exception.get());
        } else {
            response = forestResponseFactory.createResponse(request, httpResponse);
            logResponse(request, response);
            try {
                responseHandler.handleSync(httpResponse, response);
            } catch (Exception ex) {
                if (ex instanceof ForestRuntimeException) {
                    throw ex;
                } else {
                    throw new ForestRuntimeException(ex);
                }
            }
        }
*/
    }

}
