package com.dtflys.forest.backend;


import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;


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

    private static AsyncHttpExecutorCreator ASYNC_HTTP_EXECUTOR_CREATOR;

    private volatile boolean initialized = false;

    private final ForestConnectionManager connectionManager;

    static {
        try {
            Class.forName("kotlinx.coroutines.CoroutineScope");
            Class.forName("kotlinx.coroutines.Dispatchers");
            Class.forName("kotlinx.coroutines.channels.Channel");
            Class.forName("kotlinx.coroutines.launch");
            ASYNC_HTTP_EXECUTOR_CREATOR = CoroutineHttpExecutor::new;
        } catch (ClassNotFoundException e) {
            ASYNC_HTTP_EXECUTOR_CREATOR = AsyncHttpExecutor::new;
        }
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
        HttpExecutor syncHttpExecutor = createSyncExecutor(connectionManager, request, lifeCycleHandler);
        return ASYNC_HTTP_EXECUTOR_CREATOR.create(request.getConfiguration(), syncHttpExecutor, syncHttpExecutor.getResponseHandler());
    }

    @Override
    public HttpExecutor createExecutor(ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        if (request.isAsync()) {
            return ASYNC_EXECUTOR_CREATOR.createExecutor(connectionManager, request, lifeCycleHandler);
        }
        return SYNC_EXECUTOR_CREATOR.createExecutor(connectionManager, request, lifeCycleHandler);
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
