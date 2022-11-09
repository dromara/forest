package com.dtflys.forest.http;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Forest Future 对象，它可以在请求发起的线程中阻塞线程，并等待请求返回响应结果
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.27
 */
public class ForestFuture extends ResultGetter implements Future<ForestResponse> {
    private final ForestRequest request;

    private final Future<ForestResponse> future;

    private ForestResponse response;

    public ForestFuture(ForestRequest request, Future<ForestResponse> future) {
        super(request);
        this.request = request;
        this.future = future;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        request.cancel();
        return true;
    }

    @Override
    public boolean isCancelled() {
        return request.isCanceled();
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    public ForestResponse await() {
        if (response == null) {
            try {
                response = get();
            } catch (InterruptedException e) {
                throw new ForestRuntimeException(e);
            } catch (ExecutionException e) {
                throw new ForestRuntimeException(e);
            }
        }
        return response;
    }

    public ForestResponse await(long timeout, TimeUnit unit) {
        if (response == null) {
            try {
                response = get(timeout, unit);
            } catch (InterruptedException e) {
                throw new ForestRuntimeException(e);
            } catch (ExecutionException e) {
                throw new ForestRuntimeException(e);
            } catch (TimeoutException e) {
                throw new ForestRuntimeException(e);
            }
        }
        return response;
    }


    @Override
    public ForestResponse get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    @Override
    public ForestResponse get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout, unit);
    }

    @Override
    protected ForestResponse getResponse() {
        return await();
    }

}
