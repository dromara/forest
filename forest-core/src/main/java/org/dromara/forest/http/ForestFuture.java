package org.dromara.forest.http;

import org.dromara.forest.exceptions.ForestRuntimeException;
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
public class ForestFuture<T> extends ResultGetter implements Future<T> {
    private final ForestRequest<T> request;

    private final Future<ForestResponse<T>> future;

    private ForestResponse<T> response;

    public ForestFuture(ForestRequest<T> request, Future<ForestResponse<T>> future) {
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



    public ForestResponse<T> await() {
        if (response == null) {
            try {
                Object result = future.get();
                response = (ForestResponse<T>) result;
            } catch (InterruptedException e) {
                throw new ForestRuntimeException(e);
            } catch (ExecutionException e) {
                throw new ForestRuntimeException(e);
            }
        }
        return response;
    }

    public ForestResponse<T> await(long timeout, TimeUnit unit) {
        if (response == null) {
            try {
                response = future.get(timeout, unit);
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
    public T get() throws InterruptedException, ExecutionException {
        ForestResponse<T> res = await();
        return res.getResult();
    }

    @Override
    public T get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        ForestResponse<T> res = await(timeout, unit);
        return res.getResult();

    }

    @Override
    public ForestResponse<T> getResponse() {
        return await();
    }

}
