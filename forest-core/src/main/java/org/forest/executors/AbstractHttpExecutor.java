package org.forest.executors;

import org.forest.executors.httpclient.HttpclientConnectionManager;
import org.forest.executors.httpclient.response.HttpclientForestResponseFactory;
import org.forest.handler.ResponseHandler;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;
import org.forest.http.ForestResponseFactory;

/**
 * @author gongjun
 * @since 2016-05-18
 */
public abstract class AbstractHttpExecutor implements HttpExecutor {

    protected final HttpclientConnectionManager connectionManager;

    protected final ForestResponseFactory forestResponseFactory = new HttpclientForestResponseFactory();

    protected final ForestRequest request;

    protected ForestResponse response;

    public ForestRequest getRequest() {
        return request;
    }

    public AbstractHttpExecutor(HttpclientConnectionManager connectionManager, ForestRequest request) {
        this.connectionManager = connectionManager;
        this.request = request;
    }

    @Override
    public HttpclientConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public ForestResponse getResponse() {
        return response;
    }

    public void setResponse(ForestResponse response) {
        this.response = response;
    }

    public abstract void execute(ResponseHandler responseHandler);

    public abstract void close();

}
