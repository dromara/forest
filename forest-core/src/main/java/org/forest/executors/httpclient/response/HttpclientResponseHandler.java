package org.forest.executors.httpclient.response;

import org.apache.commons.collections.iterators.ObjectArrayIterator;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.util.Args;
import org.forest.exceptions.ForestNetworkException;
import org.forest.executors.httpclient.request.AsyncHttpclientRequestSender;
import org.forest.handler.ResponseHandler;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;
import org.forest.http.ForestResponseFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 15:46
 */
public abstract class HttpclientResponseHandler {

    protected final ForestRequest request;

    protected final ResponseHandler responseHandler;

    protected HttpclientResponseHandler(ForestRequest request, ResponseHandler responseHandler) {
        this.request = request;
        this.responseHandler = responseHandler;
    }

    public abstract void handle(HttpRequest httpRequest, HttpResponse httpResponse, ForestResponse response);

    public void handleSuccess(ForestResponse response) {
        responseHandler.handleResultType(request, response);
        responseHandler.handleSuccess(request, response);
    }

    public void handleError(ForestResponse response, Exception ex) {
        responseHandler.handleError(request, response, ex);
    }


    public void handleFuture(
                     final Future<HttpResponse> httpResponseFuture,
                     ForestResponseFactory forestResponseFactory) {
        Type returnType = responseHandler.getReturnType();
        Type paramType;
        Class paramClass = null;
        if (returnType instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) returnType).getRawType();
            if (!Future.class.isAssignableFrom((Class<?>) rawType)) {
                return;
            }
            paramType = ((ParameterizedType) returnType).getActualTypeArguments()[0];
            if (paramType == null) {
                paramType = Object.class;
            }
            paramClass = (Class) paramType;
        }
        else if (returnType instanceof Class) {
            if (!Future.class.isAssignableFrom((Class<?>) returnType)) {
                return;
            }
            paramClass = Object.class;
        }
        handleFutureResult(httpResponseFuture, paramClass, forestResponseFactory);

    }


    private  <T> void handleFutureResult(
            final Future<HttpResponse> httpResponseFuture,
            final Class<T> innerType,
            final ForestResponseFactory forestResponseFactory) {
        responseHandler.handleResult(new Future<T>() {
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

            private T getResult(HttpResponse httpResponse) throws InterruptedException {
                ForestResponse response = forestResponseFactory.createResponse(request, httpResponse);
                Object ret = responseHandler.handleResultType(request, response, innerType, innerType);
                return (T) ret;
            }

            @Override
            public T get() throws InterruptedException, ExecutionException {
                HttpResponse httpResponse = httpResponseFuture.get();
                return getResult(httpResponse);
            }

            @Override
            public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                HttpResponse httpResponse = httpResponseFuture.get(timeout, unit);
                return getResult(httpResponse);
            }


        });
    }


}
