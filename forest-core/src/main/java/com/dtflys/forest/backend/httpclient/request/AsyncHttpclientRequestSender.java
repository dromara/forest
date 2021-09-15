package com.dtflys.forest.backend.httpclient.request;

import com.dtflys.forest.backend.httpclient.conn.HttpclientConnectionManager;
import com.dtflys.forest.backend.httpclient.response.HttpclientForestResponseFactory;
import com.dtflys.forest.backend.httpclient.response.HttpclientResponseHandler;
import com.dtflys.forest.exceptions.ForestNetworkException;
import com.dtflys.forest.exceptions.ForestRetryException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestCookies;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestResponseFactory;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import java.io.IOException;
import java.util.Date;
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
    public void sendRequest(
            final ForestRequest request, final HttpclientResponseHandler responseHandler,
            final HttpUriRequest httpRequest, LifeCycleHandler lifeCycleHandler,
            CookieStore cookieStore, Date startDate, int retryCount)  {
        final CloseableHttpAsyncClient client = connectionManager.getHttpAsyncClient(request);
        final ForestResponseFactory forestResponseFactory = new HttpclientForestResponseFactory();
        logRequest(retryCount, (HttpRequestBase) httpRequest);
        final Future<HttpResponse> future = client.execute(httpRequest, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(final HttpResponse httpResponse) {
                ForestResponse response = forestResponseFactory.createResponse(request, httpResponse, lifeCycleHandler, null, startDate);

                // 是否重试
                ForestRetryException retryEx = request.canRetry(response);
                if (retryEx != null && !retryEx.isMaxRetryCountReached()) {
                    sendRequest(request, responseHandler, httpRequest, lifeCycleHandler, cookieStore, startDate, retryCount + 1);
                    return;
                }

                // 验证响应
                if (response.isError()) {
                    ForestNetworkException networkException =
                            new ForestNetworkException("", response.getStatusCode(), response);
                    ForestRetryException retryException = new ForestRetryException(
                            networkException,  request, request.getRetryCount(), retryCount);
                    // 如果重试条件满足，触发重试
                    try {
                        request.canRetry(response, retryException);
                    } catch (Throwable th) {
                        response = forestResponseFactory.createResponse(request, httpResponse, lifeCycleHandler, th, startDate);
                        responseHandler.handleError(response);
                        return;
                    }
                    sendRequest(request, responseHandler, httpRequest, lifeCycleHandler, cookieStore, startDate, retryCount + 1);
                    return;
                }

                ForestCookies cookies = getCookiesFromHttpCookieStore(cookieStore);
                lifeCycleHandler.handleSaveCookie(request, cookies);
                responseHandler.handleSuccess(response);
            }

            @Override
            public void failed(final Exception ex) {
                ForestResponse<?> response = forestResponseFactory.createResponse(
                        request, null, lifeCycleHandler, ex, startDate);
                ForestRetryException retryException = new ForestRetryException(
                        ex,  request, request.getRetryCount(), retryCount);
                try {
                    request.canRetry(response, retryException);
                } catch (Throwable e) {
                    response = forestResponseFactory.createResponse(
                            request, null, lifeCycleHandler, ex, startDate);
                    responseHandler.handleError(response, ex);
                    return;
                }
                sendRequest(request, responseHandler, httpRequest, lifeCycleHandler, cookieStore, startDate, retryCount + 1);
            }

            @Override
            public void cancelled() {
            }
        });
        responseHandler.handleFuture(future, startDate, forestResponseFactory);
    }
}
