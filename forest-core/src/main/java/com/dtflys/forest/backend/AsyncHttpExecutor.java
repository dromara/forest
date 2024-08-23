package com.dtflys.forest.backend;

import com.dtflys.forest.config.AsyncThreadPools;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestFuture;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestResponseFactory;
import com.dtflys.forest.reflection.MethodLifeCycleHandler;
import com.dtflys.forest.utils.StringUtils;

import java.util.concurrent.CompletableFuture;
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


    private static final String DEFAULT_POOL_NAME = "default";


    /**
     * 获取最大异步线程数
     *
     * @return 最大异步线程数
     */
    public static int getMaxAsyncThreadSize(String... asyncPoolName) {
        String name = asyncPoolName == null || asyncPoolName.length == 0 ? DEFAULT_POOL_NAME : asyncPoolName[0];
        return getMaxAsyncThreadSize(ForestConfiguration.configuration(), name);
    }

    /**
     * 获取最大异步线程数
     *
     * @param configuration Forest配置对象
     * @return 最大异步线程数
     */
    public static int getMaxAsyncThreadSize(ForestConfiguration configuration, String... asyncPoolName) {
        String name = asyncPoolName == null || asyncPoolName.length == 0 ? DEFAULT_POOL_NAME : asyncPoolName[0];
        final ThreadPoolExecutor pool = AsyncThreadPools.get(configuration, name);
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
    public static int getAsyncThreadSize(String... asyncPoolName) {
        String name = asyncPoolName == null || asyncPoolName.length == 0 ? DEFAULT_POOL_NAME : asyncPoolName[0];
        return getAsyncThreadSize(ForestConfiguration.configuration(), name);
    }

    /**
     * 获取异步线程池大小
     *
     * @param configuration Forest配置对象
     * @return 异步线程池大小
     * @since 1.5.29
     */
    public static int getAsyncThreadSize(ForestConfiguration configuration, String... asyncPoolName) {
        String name = asyncPoolName == null || asyncPoolName.length == 0 ? DEFAULT_POOL_NAME : asyncPoolName[0];
        final ThreadPoolExecutor pool = AsyncThreadPools.get(configuration, name);
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
                final Object result = ((MethodLifeCycleHandler<?>) lifeCycleHandler).getResponse();
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
        final ThreadPoolExecutor pool = AsyncThreadPools.getOrCreate(configuration, getRequestMethodAsyncPoolName());
        final AsyncTask<T> task = new AsyncTask<>(syncExecutor, lifeCycleHandler);
        final CompletableFuture<ForestResponse<T>> future = CompletableFuture.supplyAsync(task, pool);
        final ForestFuture<T> forestFuture = new ForestFuture<>(getRequest(), future);
        responseHandler.handleFuture(getRequest(), forestFuture);
    }

    public String getRequestMethodAsyncPoolName() {
        ForestRequest<?> request = getRequest();
        String asyncPoolName = request.getMethod().getMetaRequest().getAsyncPoolName();
        if (StringUtils.isBlank(asyncPoolName)) {
            return DEFAULT_POOL_NAME;
        }
        return asyncPoolName;
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
    public static synchronized void closePool(String asyncPoolName) {
        final ThreadPoolExecutor pool = AsyncThreadPools.get(ForestConfiguration.configuration(), asyncPoolName);
        if (pool != null) {
            pool.shutdown();
        }
    }

}



