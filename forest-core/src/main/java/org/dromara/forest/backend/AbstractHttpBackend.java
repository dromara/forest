package org.dromara.forest.backend;


import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.handler.LifeCycleHandler;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.utils.AsyncUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 15:41
 */
public abstract class AbstractHttpBackend implements HttpBackend {

    /**
     * 同步HTTP执行器构造器
     */
    private HttpExecutorCreator SYNC_EXECUTOR_CREATOR = this::createSyncExecutor;

    /**
     * 异步HTTP执行器构造器
     */
    private HttpExecutorCreator ASYNC_EXECUTOR_CREATOR = this::createAsyncExecutor;


    /**
     * 异步(Kotlin协程)HTTP执行器构造器
     */
    private HttpExecutorCreator KOTLIN_COROUTINE_EXECUTOR_CREATOR = this::createKotlinCoroutineExecutor;


    private static AsyncHttpExecutorCreator ASYNC_HTTP_EXECUTOR_CREATOR;

    private static AsyncHttpExecutorCreator KOTLIN_COROUTINE_HTTP_EXECUTOR_CREATOR;

    private volatile boolean initialized = false;

    private final ForestConnectionManager connectionManager;

    private static Constructor<?> kotlinCoroutineExecutorConstructor = null;

    static {
        ASYNC_HTTP_EXECUTOR_CREATOR = AsyncHttpExecutor::new;
        KOTLIN_COROUTINE_HTTP_EXECUTOR_CREATOR = (configuration, syncExecutor, responseHandler) -> {
            try {
                if (kotlinCoroutineExecutorConstructor == null) {
                    kotlinCoroutineExecutorConstructor = Class.forName("org.dromara.forest.backend.KotlinCoroutineHttpExecutor")
                            .getConstructor(
                                    ForestConfiguration.class,
                                    HttpExecutor.class,
                                    ResponseHandler.class);
                }
                return (HttpExecutor) kotlinCoroutineExecutorConstructor.newInstance(configuration, syncExecutor, responseHandler);
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                     InstantiationException | IllegalAccessException e) {
                throw new ForestRuntimeException(e);
            }
        };
    }

    public AbstractHttpBackend(ForestConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void init(ForestConfiguration configuration) {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    this.connectionManager.init(configuration);
                    initialized = true;
                }
            }
        }
    }

    @Override
    public ForestConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public abstract HttpExecutor createSyncExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler);

    public HttpExecutor createAsyncExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        final HttpExecutor syncHttpExecutor = createSyncExecutor(connectionManager, request, lifeCycleHandler);
        return ASYNC_HTTP_EXECUTOR_CREATOR.create(request.getConfiguration(), syncHttpExecutor, syncHttpExecutor.getResponseHandler());
    }

    public HttpExecutor createKotlinCoroutineExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        final HttpExecutor syncHttpExecutor = createSyncExecutor(connectionManager, request, lifeCycleHandler);
        return KOTLIN_COROUTINE_HTTP_EXECUTOR_CREATOR.create(request.getConfiguration(), syncHttpExecutor, syncHttpExecutor.getResponseHandler());
    }

    private HttpExecutorCreator getHttpExecutorCreator(final ForestRequest request) {
        if (request.isAsync()) {
            switch (request.asyncMode()) {
                case PLATFORM:
                    return ASYNC_EXECUTOR_CREATOR;
                case KOTLIN_COROUTINE:
                    if (AsyncUtil.isEnableCoroutine()) {
                        return KOTLIN_COROUTINE_EXECUTOR_CREATOR;
                    } else {
                        return ASYNC_EXECUTOR_CREATOR;
                    }
                default:
                    throw new ForestRuntimeException("Forest not support async mode '[" + request.asyncMode().name() + "]'");
            }
        }
        return SYNC_EXECUTOR_CREATOR;
    }

    @Override
    public HttpExecutor createExecutor(final ForestRequest request, final LifeCycleHandler lifeCycleHandler) {
        final HttpExecutorCreator httpExecutorCreator = getHttpExecutorCreator(request);
        return httpExecutorCreator.createExecutor(connectionManager, request, lifeCycleHandler);
    }

    @FunctionalInterface
    private interface AsyncHttpExecutorCreator {

        /**
         * 构建具体异步Http请求执行器
         *
         * @param configuration
         * @param syncExecutor
         * @param responseHandler
         * @return HttpExecutor
         */
        HttpExecutor create(ForestConfiguration configuration, HttpExecutor syncExecutor, ResponseHandler responseHandler);
    }

}
