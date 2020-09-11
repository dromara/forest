package com.dtflys.forest.backend.httpclient.request;

import com.dtflys.forest.backend.httpclient.conn.HttpclientConnectionManager;
import com.dtflys.forest.backend.httpclient.response.HttpclientForestResponseFactory;
import com.dtflys.forest.backend.httpclient.response.HttpclientResponseHandler;
import com.dtflys.forest.exceptions.ForestNetworkException;
import com.dtflys.forest.exceptions.ForestRetryException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestResponseFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.*;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.util.Date;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 20:16
 */
public class SyncHttpclientRequestSender extends AbstractHttpclientRequestSender {

    private HttpClient client;

    public SyncHttpclientRequestSender(HttpclientConnectionManager connectionManager, ForestRequest request) {
        super(connectionManager, request);
    }

    protected HttpClient getHttpClient() {
        HttpClient client = connectionManager.getHttpClient(request);
        setupHttpClient(client);
        return client;
    }

    protected void setupHttpClient(HttpClient client) {
//        client0.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, request.getTimeout());
//        if (client0 instanceof DefaultHttpClient) {
//            ((DefaultHttpClient) client0).getCookieSpecs().register("default", defaultCookieSF);
//            client0.getParams().setParameter(ClientPNames.COOKIE_POLICY, "default");
//        }
    }

    private final static CookieSpecFactory defaultCookieSF = new CookieSpecFactory() {
        @Override
        public CookieSpec newInstance(HttpParams params) {
            return new BrowserCompatSpec() {
                @Override
                public void validate(Cookie cookie, CookieOrigin origin)
                        throws MalformedCookieException {
                }
            };
        }
    };



    public static void logResponse(ForestRequest request, ForestResponse response) {
        if (!request.isLogEnable()) {
            return;
        }
        logContent("Response: Status=" + response.getStatusCode());
        if (response.isSuccess()) {
            logContent("Response: Content=" + response.getContent());
        }
    }

    public void logResponse(long startTime, ForestResponse response) {
        if (!request.isLogEnable()) {
            return;
        }
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        logContent("Response: Status = " + response.getStatusCode() + ", Time = " + time + "ms");
    }


    @Override
    public void sendRequest(ForestRequest request, HttpclientResponseHandler responseHandler, HttpUriRequest httpRequest, LifeCycleHandler lifeCycleHandler, long startTime, int retryCount)
            throws IOException {
        HttpResponse httpResponse = null;
        ForestResponse response = null;
        client = getHttpClient();
        try {
            httpResponse = client.execute(httpRequest);
            ForestResponseFactory forestResponseFactory = new HttpclientForestResponseFactory();
            response = forestResponseFactory.createResponse(request, httpResponse, lifeCycleHandler);
            logResponse(request, response);
        } catch (IOException e) {
            httpRequest.abort();
            ForestRetryException retryException = new ForestRetryException(
                    e,  request, request.getRetryCount(), retryCount);
            try {
                request.getRetryer().canRetry(retryException);
            } catch (Throwable throwable) {
                ForestResponseFactory forestResponseFactory = new HttpclientForestResponseFactory();
                response = forestResponseFactory.createResponse(request, httpResponse, lifeCycleHandler);
                logResponse(startTime, response);
                lifeCycleHandler.handleSyncWitchException(request, response, e);
                return;
            }
            startTime = System.currentTimeMillis();
            sendRequest(request, responseHandler, httpRequest, lifeCycleHandler, startTime, retryCount + 1);
        } finally {
            connectionManager.afterConnect();
        }

        if (response.isError()) {
            ForestNetworkException networkException =
                    new ForestNetworkException("", response.getStatusCode(), response);
            ForestRetryException retryException = new ForestRetryException(
                    networkException,  request, request.getRetryCount(), retryCount);
            try {
                request.getRetryer().canRetry(retryException);
            } catch (Throwable throwable) {
                responseHandler.handleSync(httpResponse, response);
                return;
            }
            sendRequest(request, responseHandler, httpRequest, lifeCycleHandler, startTime, retryCount + 1);
            return;
        }

        try {
            responseHandler.handleSync(httpResponse, response);
        } catch (Exception ex) {
            if (ex instanceof ForestRuntimeException) {
                throw ex;
            }
            else {
                throw new ForestRuntimeException(ex);
            }
        }
    }

}
