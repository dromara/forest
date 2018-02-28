package org.forest.backend.httpclient.response;

import org.forest.handler.ResponseHandler;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;
import org.forest.http.ForestResponseFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-08-03 14:53
 */
public class HttpclientForestFuture<T, R> implements Future<T> {
    private final ForestRequest request;
    private final Class<T> innerType;
    private final ResponseHandler responseHandler;
    private final Future<R> httpResponseFuture;
    private final ForestResponseFactory forestResponseFactory;

    public HttpclientForestFuture(ForestRequest request,
                                  Class<T> innerType,
                                  ResponseHandler responseHandler,
                                  Future<R> httpResponseFuture,
                                  ForestResponseFactory forestResponseFactory) {
        this.request = request;
        this.innerType = innerType;
        this.responseHandler = responseHandler;
        this.httpResponseFuture = httpResponseFuture;
        this.forestResponseFactory = forestResponseFactory;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return httpResponseFuture.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return httpResponseFuture.isCancelled();
    }

    @Override
    public boolean isDone() {
        return httpResponseFuture.isDone();
    }

    private T getResult(R httpResponse) throws InterruptedException {
        if (httpResponse != null && innerType.isAssignableFrom(httpResponse.getClass())) {
            return (T) httpResponse;
        }
        ForestResponse response = forestResponseFactory.createResponse(request, httpResponse);
        Object ret = responseHandler.handleResultType(request, response, innerType, innerType);
        return (T) ret;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        R httpResponse = httpResponseFuture.get();
        return getResult(httpResponse);
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        R httpResponse = httpResponseFuture.get(timeout, unit);
        return getResult(httpResponse);
    }
}
