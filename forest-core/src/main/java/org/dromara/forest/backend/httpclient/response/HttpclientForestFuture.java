package org.dromara.forest.backend.httpclient.response;

import org.dromara.forest.handler.LifeCycleHandler;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.http.ForestResponseFactory;

import java.util.Date;
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
    private final Date requestTime;
    private final Class<T> innerType;
    private final LifeCycleHandler lifeCycleHandler;
    private final Future<R> httpResponseFuture;
    private final ForestResponseFactory forestResponseFactory;

    public HttpclientForestFuture(ForestRequest request,
                                  Date requestTime,
                                  Class<T> innerType,
                                  LifeCycleHandler lifeCycleHandler,
                                  Future<R> httpResponseFuture,
                                  ForestResponseFactory forestResponseFactory) {
        this.request = request;
        this.requestTime = requestTime;
        this.innerType = innerType;
        this.lifeCycleHandler = lifeCycleHandler;
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

    public Date getRequestTime() {
        return requestTime;
    }

    @Override
    public boolean isDone() {
        return httpResponseFuture.isDone();
    }

    private T getResult(R httpResponse) throws InterruptedException {
        if (httpResponse != null && innerType.isAssignableFrom(httpResponse.getClass())) {
            return (T) httpResponse;
        }
        ForestResponse response = forestResponseFactory.createResponse(request, httpResponse, this.lifeCycleHandler, null, requestTime);
        Object ret = lifeCycleHandler.handleResultType(request, response, innerType, innerType);
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
