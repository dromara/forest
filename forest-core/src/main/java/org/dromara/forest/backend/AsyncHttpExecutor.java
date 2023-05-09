package org.dromara.forest.backend;

import org.dromara.forest.config.AsyncThreadPools;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.handler.LifeCycleHandler;
import org.dromara.forest.http.ForestFuture;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.http.ForestResponseFactory;
import org.dromara.forest.reflection.MethodLifeCycleHandler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
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
        ThreadPoolExecutor pool = AsyncThreadPools.get(configuration);
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
        ThreadPoolExecutor pool = AsyncThreadPools.get(configuration);
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
        public ForestResponse get() {
            executor.execute(lifeCycleHandler);
            if (lifeCycleHandler instanceof MethodLifeCycleHandler) {
                Object result = ((MethodLifeCycleHandler<?>) lifeCycleHandler).getResponse();
                return (ForestResponse) result;
            }
            return null;
        }
    }

    @Override
    public ForestRequest getRequest() {
        return syncExecutor.getRequest();
    }

    @Override
    public void execute(LifeCycleHandler lifeCycleHandler) {
        final ThreadPoolExecutor pool = AsyncThreadPools.getOrCreate(configuration);
        final AsyncTask<T> task = new AsyncTask<>(syncExecutor, lifeCycleHandler);
        final Future<ForestResponse<T>> future = CompletableFuture.supplyAsync(task, pool);
        final ForestFuture<T> forestFuture = new ForestFuture<>(getRequest(), future);
        responseHandler.handleFuture(getRequest(), forestFuture);
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
        ThreadPoolExecutor pool = AsyncThreadPools.get(ForestConfiguration.configuration());
        if (pool != null) {
            pool.shutdown();
        }
    }

}



