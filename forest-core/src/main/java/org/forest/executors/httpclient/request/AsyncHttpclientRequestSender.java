package org.forest.executors.httpclient.request;

import com.twitter.finagle.Http;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.concurrent.BasicFuture;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.forest.exceptions.ForestNetworkException;
import org.forest.exceptions.ForestRuntimeException;
import org.forest.executors.httpclient.conn.HttpclientConnectionManager;
import org.forest.executors.httpclient.response.AsyncHttpclientResponseHandler;
import org.forest.executors.httpclient.response.HttpclientForestResponseFactory;
import org.forest.executors.httpclient.response.HttpclientResponseHandler;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;
import org.forest.http.ForestResponseFactory;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-07-21 15:49
 */
public class AsyncHttpclientRequestSender extends AbstractHttpclientRequestSender {

    private volatile boolean completed = false;

    public AsyncHttpclientRequestSender(HttpclientConnectionManager connectionManager, ForestRequest request) {
        super(connectionManager, request);
    }

    @Override
    public void sendRequest(final ForestRequest request, final HttpclientResponseHandler responseHandler, final HttpUriRequest httpRequest) throws IOException {
        CloseableHttpAsyncClient client = connectionManager.getHttpAsyncClient();
        client.start();
        final ForestResponseFactory forestResponseFactory = new HttpclientForestResponseFactory();

        final Future<HttpResponse> future = client.execute(httpRequest, new FutureCallback<HttpResponse>() {
            public void completed(final HttpResponse httpResponse) {
                completed = true;
//                AsyncHttpclientRequestSender.this.notifyAll();
                ForestResponse response = forestResponseFactory.createResponse(request, httpResponse);
                responseHandler.handleSuccess(response);
            }

            public void failed(final Exception ex) {
                completed = true;
//                AsyncHttpclientRequestSender.this.notifyAll();
                ForestResponse response = forestResponseFactory.createResponse(request, null);
                responseHandler.handleError(response, ex);
            }

            public void cancelled() {
                completed = true;
//                AsyncHttpclientRequestSender.this.notifyAll();
            }
        });

        responseHandler.handleFuture(this, future, forestResponseFactory);

    }

    public boolean isCompleted() {
        return completed;
    }
}
