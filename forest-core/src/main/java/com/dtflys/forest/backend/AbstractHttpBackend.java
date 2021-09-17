package com.dtflys.forest.backend;


import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestType;

import java.util.HashMap;
import java.util.Map;

import static com.dtflys.forest.http.ForestRequestType.DELETE;
import static com.dtflys.forest.http.ForestRequestType.GET;
import static com.dtflys.forest.http.ForestRequestType.HEAD;
import static com.dtflys.forest.http.ForestRequestType.OPTIONS;
import static com.dtflys.forest.http.ForestRequestType.PATCH;
import static com.dtflys.forest.http.ForestRequestType.POST;
import static com.dtflys.forest.http.ForestRequestType.PUT;
import static com.dtflys.forest.http.ForestRequestType.TRACE;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 15:41
 */
public abstract class AbstractHttpBackend implements HttpBackend {

    private volatile boolean initialized = false;

    private final Map<ForestRequestType, HttpExecutorCreator> executorCreatorMap = new HashMap<>();

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
                    init();
                    initialized = true;
                }
            }
        }
    }

    protected abstract HttpExecutor createHeadExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler);

    protected abstract HttpExecutor createGetExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler);

    protected abstract HttpExecutor createPostExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler);

    protected abstract HttpExecutor createPutExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler);

    protected abstract HttpExecutor createDeleteExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler);

    protected abstract HttpExecutor createOptionsExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler);

    protected abstract HttpExecutor createTraceExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler);

    protected abstract HttpExecutor createPatchExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler);

    private void init() {
        executorCreatorMap.put(GET, new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
                return createGetExecutor(connectionManager, request, lifeCycleHandler);
            }
        });
        executorCreatorMap.put(HEAD, new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
                return createHeadExecutor(connectionManager, request, lifeCycleHandler);
            }
        });
        executorCreatorMap.put(DELETE, new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
                return createDeleteExecutor(connectionManager, request, lifeCycleHandler);
            }
        });
        executorCreatorMap.put(OPTIONS, new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
                return createOptionsExecutor(connectionManager, request, lifeCycleHandler);
            }
        });
        executorCreatorMap.put(TRACE, new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
                return createTraceExecutor(connectionManager, request, lifeCycleHandler);
            }
        });
        executorCreatorMap.put(POST, new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
                return createPostExecutor(connectionManager, request, lifeCycleHandler);
            }
        });
        executorCreatorMap.put(PUT, new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
                return createPutExecutor(connectionManager, request, lifeCycleHandler);
            }
        });
        executorCreatorMap.put(PATCH, new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
                return createPatchExecutor(connectionManager, request, lifeCycleHandler);
            }
        });
    }

    @Override
    public HttpExecutor createExecutor(ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        ForestRequestType type = request.getType();
        HttpExecutorCreator httpExecutorCreator = executorCreatorMap.get(type);
        if (httpExecutorCreator == null) {
            throw new ForestRuntimeException("Http request type \"" + type.getName() + "\" is not be supported.");
        }
        HttpExecutor executor = httpExecutorCreator.createExecutor(connectionManager, request, lifeCycleHandler);
        return executor;
    }


}
