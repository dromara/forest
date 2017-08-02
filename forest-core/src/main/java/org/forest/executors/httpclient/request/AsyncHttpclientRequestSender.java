package org.forest.executors.httpclient.request;

import org.apache.http.ProtocolVersion;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.forest.exceptions.ForestNetworkException;
import org.forest.exceptions.ForestRuntimeException;
import org.forest.executors.httpclient.conn.HttpclientConnectionManager;
import org.forest.executors.httpclient.response.HttpclientForestResponseFactory;
import org.forest.executors.httpclient.response.HttpclientResponseHandler;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;
import org.forest.http.ForestResponseFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-07-21 15:49
 */
public class AsyncHttpclientRequestSender extends AbstractHttpclientRequestSender {

    ExecutorService executorService = Executors.newFixedThreadPool(200);

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
                ForestResponse response = forestResponseFactory.createResponse(request, httpResponse);
                responseHandler.handle(httpRequest, httpResponse, response);
            }

            public void failed(final Exception ex) {
                HttpResponse httpResponse = new BasicHttpResponse(
                        new BasicStatusLine(
                                new ProtocolVersion("1.1", 1, 1), 404, ""));
                ForestResponse response = forestResponseFactory.createResponse(request, httpResponse);
                responseHandler.handle(httpRequest, httpResponse, response);
            }

            public void cancelled() {
                System.out.println("cancelled");
            }
        });

/*
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpResponse httpResponse = future.get();
                    ForestResponse response = forestResponseFactory.createResponse(request, httpResponse);
                    responseHandler.handle(httpRequest, httpResponse, response);
                } catch (InterruptedException e) {
                    throw new ForestRuntimeException(e);
                } catch (ExecutionException e) {
                    throw new ForestRuntimeException(e);
                }
            }
        });
*/

    }

}
