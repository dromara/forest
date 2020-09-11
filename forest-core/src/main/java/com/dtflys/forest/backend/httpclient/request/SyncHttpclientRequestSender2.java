package com.dtflys.forest.backend.httpclient.request;

import com.dtflys.forest.backend.httpclient.conn.HttpclientConnectionManager;
import com.dtflys.forest.backend.okhttp3.executor.AbstractOkHttp3Executor;
import com.dtflys.forest.exceptions.ForestNetworkException;
import com.dtflys.forest.exceptions.ForestRetryException;
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
import java.util.Date;
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
    public void sendRequest(final ForestRequest request, final HttpclientResponseHandler responseHandler, final HttpUriRequest httpRequest, LifeCycleHandler lifeCycleHandler, long startTime, int retryCount) throws IOException {
        final CloseableHttpAsyncClient client = connectionManager.getHttpAsyncClient(request);
        client.start();
        final AtomicReference<ForestResponse> forestResponseRef = new AtomicReference<>();
        final AtomicReference<Exception> exceptionRef = new AtomicReference<>();
        final ForestResponseFactory forestResponseFactory = new HttpclientForestResponseFactory();
        Future<HttpResponse> future = null;
        try {
            future = client.execute(httpRequest, new FutureCallback<HttpResponse>() {
                @Override
                public void completed(final HttpResponse httpResponse) {
                    ForestResponse response = forestResponseFactory.createResponse(request, httpResponse, lifeCycleHandler);
                    forestResponseRef.set(response);
                }

                @Override
                public void failed(final Exception ex) {
                    ForestResponse response = forestResponseFactory.createResponse(request, null, lifeCycleHandler);
                    forestResponseRef.set(response);
                    exceptionRef.set(ex);
                }

                @Override
                public void cancelled() {
                }
            });

        } finally {
            client.close();
        }

        HttpResponse httpResponse = null;
        try {
            httpResponse = future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
                ForestNetworkException networkException =
                        new ForestNetworkException("", response.getStatusCode(), response);
                ForestRetryException retryException = new ForestRetryException(
                        networkException,  request, request.getRetryCount(), retryCount);
                try {
                    request.getRetryer().canRetry(retryException);
                } catch (Throwable throwable) {
                    responseHandler.handleError(response);
                    return;
                }
            } else {
                ForestRetryException retryException = new ForestRetryException(
                        ex,  request, request.getRetryCount(), retryCount);
                try {
                    request.getRetryer().canRetry(retryException);
                } catch (Throwable th) {
                    responseHandler.handleError(response, th);
                    return;
                }
            }
            startTime = new Date().getTime();
            sendRequest(request, responseHandler, httpRequest, lifeCycleHandler, startTime, retryCount + 1);

        }

    }

}
