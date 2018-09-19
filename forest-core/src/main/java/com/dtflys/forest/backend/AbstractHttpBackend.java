package com.dtflys.forest.backend;


import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.handler.ResponseHandler;
import com.dtflys.forest.http.ForestRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 15:41
 */
public abstract class AbstractHttpBackend implements HttpBackend {

    private volatile boolean initialized = false;

    private final Map<String, HttpExecutorCreator> executorCreatorMap = new HashMap<>();

    private final ForestConnectionManager connectionManager;

    public AbstractHttpBackend(ForestConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void init(ForestConfiguration configuration) {
        synchronized (this) {
            if (!initialized) {
                this.connectionManager.init(configuration);
                init();
                initialized = true;
            }
        }
    }

    protected abstract HttpExecutor createHeadExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler);

    protected abstract HttpExecutor createGetExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler);

    protected abstract HttpExecutor createPostExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler);

    protected abstract HttpExecutor createPutExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler);

    protected abstract HttpExecutor createDeleteExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler);

    protected abstract HttpExecutor createOptionsExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler);

    protected abstract HttpExecutor createTraceExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler);

    protected abstract HttpExecutor createPatchExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler);

    private void init() {
        executorCreatorMap.put("GET", new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
                return createGetExecutor(connectionManager, request, responseHandler);
            }
        });
        executorCreatorMap.put("HEAD", new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
                return createHeadExecutor(connectionManager, request, responseHandler);
            }
        });
        executorCreatorMap.put("DELETE", new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
                return createDeleteExecutor(connectionManager, request, responseHandler);
            }
        });
        executorCreatorMap.put("OPTIONS", new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
                return createOptionsExecutor(connectionManager, request, responseHandler);
            }
        });
        executorCreatorMap.put("TRACE", new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
                return createTraceExecutor(connectionManager, request, responseHandler);
            }
        });
        executorCreatorMap.put("POST", new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
                return createPostExecutor(connectionManager, request, responseHandler);
            }
        });
        executorCreatorMap.put("PUT", new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
                return createPutExecutor(connectionManager, request, responseHandler);
            }
        });
        executorCreatorMap.put("PATCH", new HttpExecutorCreator() {
            @Override
            public HttpExecutor createExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
                return createPatchExecutor(connectionManager, request, responseHandler);
            }
        });
    }

    @Override
    public HttpExecutor createExecutor(ForestRequest request, ResponseHandler responseHandler) {
        String key = request.getType().toUpperCase();
        HttpExecutorCreator httpExecutorCreator = executorCreatorMap.get(key);
        if (httpExecutorCreator == null) {
            throw new ForestRuntimeException("Http request type\"" + key + "\" is not be supported.");
        }
        HttpExecutor executor = httpExecutorCreator.createExecutor(connectionManager, request, responseHandler);
        return executor;
    }


}
