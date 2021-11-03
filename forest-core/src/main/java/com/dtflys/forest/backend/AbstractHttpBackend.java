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
    private HttpExecutorCreator SYNC_EXECUTOR_CREATOR = (connectionManager, request, lifeCycleHandler) ->
            createSyncExecutor(connectionManager, request, lifeCycleHandler);

    /**
     * 异步HTTP执行器构造器
     */
    private HttpExecutorCreator ASYNC_EXECUTOR_CREATOR = (connectionManager, request, lifeCycleHandler) ->
            createAsyncExecutor(connectionManager, request, lifeCycleHandler);

    private volatile boolean initialized = false;

    private final ForestConnectionManager connectionManager;

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

    public AsyncHttpExecutor createAsyncExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        HttpExecutor syncHttpExecutor = createSyncExecutor(connectionManager, request, lifeCycleHandler);
        AsyncHttpExecutor asyncHttpExecutor = new AsyncHttpExecutor(syncHttpExecutor, request, syncHttpExecutor.getResponseHandler());
        return asyncHttpExecutor;
    }

    @Override
    public HttpExecutor createExecutor(ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        if (request.isAsync()) {
            return ASYNC_EXECUTOR_CREATOR.createExecutor(connectionManager, request, lifeCycleHandler);
        }
        return SYNC_EXECUTOR_CREATOR.createExecutor(connectionManager, request, lifeCycleHandler);
    }

}
