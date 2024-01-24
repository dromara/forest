package com.dtflys.forest.backend;

import com.dtflys.forest.config.AsyncThreadPools;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestFuture;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestResponseFactory;
import com.dtflys.forest.http.ResultGetter;
import com.dtflys.forest.reflection.MethodLifeCycleHandler;
import com.dtflys.forest.utils.TypeReference;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Forest异步请求执行器
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.12
 */
public class AsyncHttpExecutor<T> implements HttpExecutor {

    protected final ForestConfiguration configuration;

    /**
     * Forest同步请求执行器
     */
    protected final HttpExecutor syncExecutor;

    /**
     * Forest响应对象处理器
     */
    protected final ResponseHandler responseHandler;




    /**
     * 获取最大异步线程数
     *
     * @return 最大异步线程数
     */
    public static int getMaxAsyncThreadSize() {
        return getMaxAsyncThreadSize(ForestConfiguration.configuration());
    }

    /**
     * 获取最大异步线程数
     *
     * @param configuration Forest配置对象
     * @return 最大异步线程数
     */
    public static int getMaxAsyncThreadSize(ForestConfiguration configuration) {
        final ThreadPoolExecutor pool = AsyncThreadPools.get(configuration);
        if (pool == null) {
            return -1;
        }
        return pool.getMaximumPoolSize();
    }


    /**
     * 获取异步线程池大小
     *
     * @return 异步线程池大小
     * @since 1.5.29
     */
    public static int getAsyncThreadSize() {
        return getAsyncThreadSize(ForestConfiguration.configuration());
    }

    /**
     * 获取异步线程池大小
     *
     * @param configuration Forest配置对象
     * @return 异步线程池大小
     * @since 1.5.29
     */
    public static int getAsyncThreadSize(ForestConfiguration configuration) {
        final ThreadPoolExecutor pool = AsyncThreadPools.get(configuration);
        if (pool == null) {
            return -1;
        }
        return pool.getPoolSize();
    }


    public AsyncHttpExecutor(ForestConfiguration configuration, HttpExecutor syncExecutor, ResponseHandler responseHandler) {
        this.configuration = configuration;
        this.syncExecutor = syncExecutor;
        this.responseHandler = responseHandler;
    }

    public static class AsyncTask<T> implements Supplier<ForestResponse<T>> {

        private final HttpExecutor executor;

        private final LifeCycleHandler lifeCycleHandler;

        public AsyncTask(HttpExecutor executor, LifeCycleHandler lifeCycleHandler) {
            this.executor = executor;
            this.lifeCycleHandler = lifeCycleHandler;
        }

        public LifeCycleHandler getLifeCycleHandler() {
            return lifeCycleHandler;
        }


        @Override
        public ForestResponse<T> get() {
            ResultGetter resultGetter = executor.execute(lifeCycleHandler);
            if (resultGetter instanceof ForestResponse) {
                return (ForestResponse) resultGetter;
            }
//            if (lifeCycleHandler instanceof MethodLifeCycleHandler) {
//                final Object result = ((MethodLifeCycleHandler<?>) lifeCycleHandler).getResponse();
//                return (ForestResponse) result;
//            }
            return resultGetter.result(new TypeReference<>() {});
        }
    }

    @Override
    public ForestRequest getRequest() {
        return syncExecutor.getRequest();
    }

    @Override
    public ResultGetter execute(LifeCycleHandler lifeCycleHandler) {
        final ThreadPoolExecutor pool = AsyncThreadPools.getOrCreate(configuration);
        final AsyncTask<T> task = new AsyncTask<>(syncExecutor, lifeCycleHandler);
        final Future<ForestResponse<T>> future = CompletableFuture.supplyAsync(task, pool);
        final ForestFuture<T> forestFuture = new ForestFuture<>(getRequest(), future);
        responseHandler.handleFuture(getRequest(), forestFuture);
        return forestFuture;
    }

    @Override
    public ResponseHandler getResponseHandler() {
        return responseHandler;
    }

    @Override
    public ForestResponseFactory getResponseFactory() {
        return syncExecutor.getResponseFactory();
    }

    @Override
    public void close() {

    }

    /**
     * 关闭异步请求线程池
     *
     * @since 1.5.23
     */
    public static synchronized void closePool() {
        final ThreadPoolExecutor pool = AsyncThreadPools.get(ForestConfiguration.configuration());
        if (pool != null) {
            pool.shutdown();
        }
    }

}



