package org.forest.executors.httpclient.request;

import org.apache.http.ProtocolVersion;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.forest.executors.httpclient.HttpclientConnectionManager;
import org.forest.executors.httpclient.response.HttpclientForestResponseFactory;
import org.forest.executors.httpclient.response.HttpclientResponseHandler;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;
import org.forest.http.ForestResponseFactory;

import javax.annotation.Nullable;
import java.io.IOException;

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
        CloseableHttpAsyncClient client = connectionManager.getHttpAsyncClient();
        client.start();
        final ForestResponseFactory forestResponseFactory = new HttpclientForestResponseFactory();
        client.execute(httpRequest, new FutureCallback<HttpResponse>() {

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
            }
        });
    }

}
